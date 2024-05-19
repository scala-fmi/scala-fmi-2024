package io

import concurrent.{ConcurrentUtils, ExecutionContexts}
import product.ProductFactory

import java.util.concurrent.atomic.AtomicReference
import scala.concurrent.ExecutionContext
import scala.io.StdIn
import scala.util.{Failure, Success, Try}

case class IORuntime(defaultEc: ExecutionContext, blockingEc: ExecutionContext, shutdown: () => Unit)

object IORuntime:
  val default = IORuntime(
    ExecutionContexts.default,
    ExecutionContexts.blocking,
    () => ExecutionContexts.blocking.shutdown()
  )

sealed trait IO[+A]:
  def map[B](f: A => B): IO[B] = flatMap(a => IO.of(f(a)))
  def flatMap[B](f: A => IO[B]): IO[B] = FlatMap(this, f)

  def >>=[B](f: A => IO[B]): IO[B] = flatMap(f)
  def >>[B](nextIO: IO[B]): IO[B] = flatMap(_ => nextIO)

  infix def zip[B](ioB: IO[B]): IO[(A, B)] = Concurrent(this, ioB)

  def zipMap[B, C](ioB: IO[B])(f: (A, B) => C) = zip(ioB).map(f.tupled)

  def unsafeRun(): A = this match
    case Pure(value) => value
    case Error(throwable: Throwable) => throw throwable
    case Delay(delayedValue, _) => delayedValue()
    case FlatMap(io, f) => f(io.unsafeRun()).unsafeRun()
    case FlatMapError(io, f) =>
      Try(io.unsafeRun()) match
        case Failure(error) => f(error).unsafeRun()
        case Success(value) => value
    case Concurrent(firstIO, secondIO) => (firstIO.unsafeRun(), secondIO.unsafeRun())
    case Async(initiateAction) =>
      ConcurrentUtils.waitToFinish(callback => initiateAction(callback, ExecutionContexts.currentThreadEc))

  def unsafeRunAsync(onComplete: Callback[A])(runtime: IORuntime): Unit =
    def execute(work: => Any): Unit = runtime.defaultEc.execute(() => work)
    def executeBlocking(work: => Any): Unit = runtime.blockingEc.execute(() => work)

    this match
      case Pure(value) => execute(onComplete(Success(value)))
      case Error(error) => execute(onComplete(Failure(error)))
      case Delay(delayedValue, false) => execute(onComplete(Success(delayedValue())))
      case Delay(delayedValue, true) => executeBlocking(onComplete(Success(delayedValue())))
      case FlatMap(io, f) =>
        io.unsafeRunAsync {
          case Success(value) => f(value).unsafeRunAsync(onComplete)(runtime)
          case Failure(e) => onComplete(Failure(e))
        }(runtime)
      case FlatMapError(io, f) =>
        io.unsafeRunAsync {
          case success @ Success(_) => onComplete(success)
          case Failure(e) => f(e).unsafeRunAsync(onComplete)(runtime)
        }(runtime)
      case Async(initiateAction) => initiateAction(onComplete, runtime.defaultEc)
      case concurrentIo @ Concurrent(ioA, ioB) =>
        val firstCompletedResult =
          new AtomicReference[Option[Either[Try[concurrentIo.AType], Try[concurrentIo.BType]]]](None)

        def completeWith(aResult: Try[concurrentIo.AType], bResult: Try[concurrentIo.BType]): Unit =
          val result =
            for
              a <- aResult
              b <- bResult
            yield (a, b)

          onComplete(result)

        def completeA(aResult: Try[concurrentIo.AType]): Unit =
          val existingValue = firstCompletedResult.getAndUpdate:
            case None => Some(Left(aResult))
            case value => value

          existingValue
            .collect:
              case Right(bResult) => bResult
            .foreach(completeWith(aResult, _))

        def completeB(bResult: Try[concurrentIo.BType]): Unit =
          val existingValue = firstCompletedResult.getAndUpdate:
            case None => Some(Right(bResult))
            case value => value

          existingValue
            .collect:
              case Left(aResult) => aResult
            .foreach(completeWith(_, bResult))

        ioA.unsafeRunAsync(completeA)(runtime)
        ioB.unsafeRunAsync(completeB)(runtime)

  def unsafeRunSync(runtime: IORuntime): A =
    ConcurrentUtils.waitToFinish(callback => unsafeRunAsync(callback)(runtime))

case class Pure[A](value: A) extends IO[A]
case class Error(throwable: Throwable) extends IO[Nothing]
case class Delay[A](delayedValue: () => A, isBlocking: Boolean = false) extends IO[A]
case class FlatMap[A, B](ioA: IO[A], f: A => IO[B]) extends IO[B]
case class FlatMapError[A](ioA: IO[A], f: Throwable => IO[A]) extends IO[A]
case class Concurrent[A, B](ioA: IO[A], ioB: IO[B]) extends IO[(A, B)]:
  type AType = A
  type BType = B
case class Async[A](initiateAction: (Callback[A], ExecutionContext) => Unit) extends IO[A]

type Callback[-A] = Try[A] => Unit

object IO:
  def apply[A](delayedValue: => A): IO[A] = Delay(() => delayedValue)
  def blocking[A](delayedValue: => A): IO[A] = Delay(() => delayedValue, true)
  def of[A](value: A): IO[A] = Pure(value)

  def async[A](initiateAction: (Callback[A], ExecutionContext) => Unit) = Async(initiateAction)

  def println(str: String): IO[Unit] = IO.blocking(Predef.println(str))
  def readln: IO[String] = IO.blocking(StdIn.readLine())

@main
def bookProducingExample(): Unit =
  def produceBook: IO[Product] = IO:
    ProductFactory.produceProduct("book")

  def produce2Books: IO[(Product, Product)] =
    produceBook zip produceBook

  val result = produce2Books.unsafeRunSync(IORuntime.default)
  println("Echo")
  println(result)

  IORuntime.default.shutdown()

@main
def largerComposition(): Unit =
  val task1: IO[Int] =
    val computation = IO:
      Thread.sleep(2000)
      42

    IO.println("Running task 1") >> computation

  val task2: IO[Int] =
    val computation = IO:
      Thread.sleep(2000)
      10

    IO.println("Running task 2") >> computation

  def double(n: Int): IO[Int] =
    val computation = IO:
      Thread.sleep(1000)
      n * 2

    IO.println("Running double") >> computation

  val composedResult = for
    (result1, result2) <- task1 zip task2
    doubledSum <- double(result1 + result2)
    _ <- IO.println(s"Result: $doubledSum")
  yield doubledSum

  composedResult.unsafeRunSync(IORuntime.default)
  IORuntime.default.shutdown()
