package catsio

import cats.effect.IOApp
import cats.effect.IO
import cats.syntax.all.*

import scala.concurrent.duration.*

object Ex03Cancellation extends IOApp.Simple:
  def run: IO[Unit] =
    val calc1 = IO.sleep(2.second) >> IO.println("Running calc 1") >> IO.pure(42)
    val calc2 = IO.sleep(1.second) >> IO.println("Running calc 2") >> IO.raiseError[Int](new RuntimeException("Error"))

//    (calc1, calc2).parMapN(_ + _).timed >>= IO.println

//    (
//      calc1.onCancel(IO.println("calc 1 cancelled")),
//      calc2.onCancel(IO.println("calc 2 cancelled"))
//    ).parMapN(_ + _) >>= IO.println

//    IO.race(calc1, calc2.handleErrorWith(_ => IO.never)) >>= IO.println

//    IO.race(calc1, IO.sleep(1.seconds)) >>= IO.println // run something and cancel it if it does not finish in time

    calc1.timeout(4.second) >>= IO.println
