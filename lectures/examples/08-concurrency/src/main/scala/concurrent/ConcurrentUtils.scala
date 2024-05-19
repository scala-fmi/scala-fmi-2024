package concurrent

import java.util.concurrent.{ArrayBlockingQueue, TimeUnit}
import scala.util.Try

object ConcurrentUtils:
  def waitToFinish[A](initiateAction: (Try[A] => Unit) => Unit): A =
    val resultingQueue = new ArrayBlockingQueue[Try[A]](1)

    initiateAction(result => resultingQueue.offer(result))

    resultingQueue.poll(Long.MaxValue, TimeUnit.NANOSECONDS).get
