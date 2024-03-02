package examples

import cats.effect.{IO, IOApp}
import cats.implicits.*

import scala.concurrent.duration.DurationInt

object Concurrency extends IOApp.Simple:
  def square(n: Int) = IO.sleep(1.second) >> IO.println(s"Square $n") >> IO(n * n)

  def double(n: Int) = IO.sleep(2.second) >> IO.println(s"Double $n") >> IO(n * 2)

  val calc = for
    (a, b, c) <- (square(2), square(10), square(20)).parTupled
    d <- double(a + b + c)
  yield d

  override def run: IO[Unit] = calc.timed >>= IO.println