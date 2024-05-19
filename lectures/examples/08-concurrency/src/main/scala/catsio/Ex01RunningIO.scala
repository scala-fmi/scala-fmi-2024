package catsio

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import cats.syntax.parallel.*
import cats.syntax.all.*

import scala.concurrent.duration.DurationInt

object Ex01RunningIO:
  def double(n: Int): IO[Int] = IO.sleep(1.second) >> IO(n * 2)

  def square(n: Int): IO[Int] = IO.sleep(1.second) >> IO(n * n)

  val calc: IO[Int] = for
    (a, b, c) <- (square(2), square(10), square(20)).parTupled
    d <- double(a + b + c)
  yield d

  val calcOutput = calc.timed >>= IO.println

  def main(args: Array[String]): Unit =
    calcOutput.unsafeRunSync()
