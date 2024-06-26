package concurrent.future

import concurrent.ExecutionContexts

import java.util.concurrent.atomic.AtomicReference
import java.util.concurrent.locks.LockSupport
import scala.annotation.tailrec
import scala.concurrent.duration.Duration
import scala.concurrent.{CanAwait, ExecutionContext, TimeoutException}
import scala.util.{Failure, Success, Try}

class Promise[A]:
  case class Handler(handler: Try[A] => Any, ec: ExecutionContext):
    def executeWithValue(value: Try[A]): Unit = ec.execute(() => handler(value))

  sealed trait State
  case class Completed(value: Try[A]) extends State
  case class Pending(handlers: List[Handler]) extends State

  // Most of the time AtomicReference is better for synchronization as it uses non-blocking techniques.
  // Instead of forcing threads to be suspended while waiting on a lock, here there are no locks involved.
  // Instead CPU's comapareAndSet operation is set. It can be used to atomically set a new value if the current
  // ones matches the expected current one passed to the compareAndSet method.
  //
  // compareAndSet returns a Boolean, true if the update completed, false otherwise.
  // If the update has failed then it can be retried by the user, first reading the current value again
  // (as the failure indicated it has been updated by some other thread) and then reapplying the calculation on that value.
  //
  // This technique is called optimistic locking. See [executeWhenComplete] and [completeWithValue] for how it can be applied.
  //
  // AtomicReference keeps its value in a volatile variable internally.
  private val state = new AtomicReference[State](Pending(List.empty))

  @tailrec
  private def executeWhenComplete(handler: Handler): Unit = state.get() match
    case Completed(value) => handler.executeWithValue(value)
    case s @ Pending(handlers) =>
      val newState = Pending(handler :: handlers)
      if !state.compareAndSet(s, newState) then executeWhenComplete(handler)

  @tailrec
  private def completeWithValue(value: Try[A]): List[Handler] = state.get() match
    case Completed(_) => List.empty
    case s @ Pending(handlers) =>
      if state.compareAndSet(s, Completed(value)) then handlers
      else completeWithValue(value)

  val future: Future[A] = new Future[A]:
    def value: Option[Try[A]] = state.get() match
      case Completed(value) => Some(value)
      case _ => None

    def onComplete(handler: Try[A] => Unit)(using ec: ExecutionContext): Unit = executeWhenComplete(
      Handler(handler, ec)
    )

    // Blocks the current thread until the result is ready
    def ready(atMost: Duration)(using permit: CanAwait): this.type =
      if !isComplete && Duration.Zero < atMost then
        val thread = Thread.currentThread
        onComplete(_ => LockSupport.unpark(thread))(using ExecutionContexts.currentThreadEc)

        if atMost == Duration.Inf then LockSupport.park()
        else LockSupport.parkNanos(atMost.toNanos)

      if isComplete then this
      else throw new TimeoutException

    def result(atMost: Duration)(using permit: CanAwait): A = ready(atMost).value.get.get

  def complete(value: Try[A]): Promise[A] =
    completeWithValue(value).foreach(_.executeWithValue(value))
    this

  def succeed(value: A): Promise[A] = complete(Success(value))

  def fail(e: Throwable): Promise[A] = complete(Failure(e))

object Promise:
  def apply[T] = new Promise[T]
