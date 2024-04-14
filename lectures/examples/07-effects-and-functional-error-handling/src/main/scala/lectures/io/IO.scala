package lectures.io

import scala.io.StdIn

sealed trait IO[+A]:
//  def map[B](f: A => B): IO[B] = Map(this, f)
  def map[B](f: A => B): IO[B] = flatMap(a => IO.of(f(a)))
  def flatMap[B](f: A => IO[B]): IO[B] = FlatMap(this, f)

  def unsafeRun(): A = this match
    case Pure(value) => value
    case Println(message) => println(message)
    case Readln => StdIn.readLine()
//    case Map(ioA, f) => f(ioA.unsafeRun())
    case FlatMap(ioO, f) => f(ioO.unsafeRun()).unsafeRun()
    case Delay(delayedValue) => delayedValue()

case class Pure[A](value: A) extends IO[A]
//case class Map[A, B](ioA: IO[A], f: A => B) extends IO[B]
case class FlatMap[A, B](ioA: IO[A], f: A => IO[B]) extends IO[B]
case class Println(message: String) extends IO[Unit]
case object Readln extends IO[String]
case class Delay[A](delayedValue: () => A) extends IO[A]

object IO:
  def apply[A](delayedValue: => A): IO[A] = Delay(() => delayedValue)

  def of[A](value: A): IO[A] = Pure(value)

  def println(message: String): IO[Unit] = Println(message)
  def readln: IO[String] = Readln

@main
def testIO(): Unit =
  val askForName = IO.println("What's your name?")

  val program1 = for
    _ <- askForName
    name <- IO.readln
    _ <- IO.println(s"Hello $name!!!")
  yield ()

  val program2 = for
    lines <- FileIO.readFileLines("build.sbt")
    numberOfLines = lines.size
    _ <- IO.println(s"Lines: $numberOfLines")
  yield ()

  program1.unsafeRun()
  program2.unsafeRun()

// Advantages of IO
//
// * It's a value- can combine, optimize, etc.
// * Asynchronicity
// * Cancellation
// * Scheduling and retrying
// * Test and production instances
// * Different frontends