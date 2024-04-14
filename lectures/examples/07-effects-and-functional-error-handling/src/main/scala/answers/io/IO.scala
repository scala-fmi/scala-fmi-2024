package answers.io

import scala.annotation.tailrec
import scala.io.StdIn

sealed trait IO[+A]:
  def map[B](f: A => B): IO[B] = flatMap(a => IO.of(f(a)))
  def flatMap[B](f: A => IO[B]): IO[B] = FlatMap(this, f)

  def >>=[B](f: A => IO[B]): IO[B] = flatMap(f)
  def >>[B](nextIO: IO[B]): IO[B] = flatMap(_ => nextIO)

  def unsafeRun(): A = this match
    case Pure(value) => value
    case Delay(delayedValue) => delayedValue()
    case FlatMap(io, f) => f(io.unsafeRun()).unsafeRun()

  // (a + b) + c == a + (b + c)
  //
  // FlatMap(FlatMap(io, f), g)
  // ==
  // FlatMap(io, a => FlatMap(f(a), g))

  // Option(x).flatMap(f).flatMap(g)
  // ==
  // Option(x).flatMap(x => f(x).flatMap(g))

  @tailrec
  final def unsafeRunStackSafe(): A = this match
    case Pure(value) => value
    case Delay(delayedValue) => delayedValue()
    case FlatMap(Pure(value), f) => f(value).unsafeRunStackSafe()
    case FlatMap(Delay(delayedValue), f) => f(delayedValue()).unsafeRunStackSafe()
    case FlatMap(FlatMap(ioInner, f), g) => ioInner.flatMap(value => f(value).flatMap(g)).unsafeRunStackSafe()


case class Pure[A](value: A) extends IO[A]
case class Delay[A](delayedValue: () => A) extends IO[A]
case class FlatMap[A, B](ioA: IO[A], f: A => IO[B]) extends IO[B]

object IO:
  def apply[A](delayedValue: => A): IO[A] = Delay(() => delayedValue)
  def of[A](value: A): IO[A] = Pure(value)

  def println(str: String): IO[Unit] = IO(Predef.println(str))
  def readln: IO[String] = IO(StdIn.readLine())

@main
def testIO(): Unit =
  val program = for
    lines <- FileIO.readFileLines("build.sbt")
    numberOfLines = lines.size
    _ <- IO.println(s"Lines: $numberOfLines")
  yield ()

  program.unsafeRun()

  def loop(i: Int): IO[Unit] = for
    _ <- IO.println(i.toString)
    _ <- loop(i + 1)
  yield ()

  loop(0).unsafeRun()

// Advantages of IO
//
// * It's a value- can combine, optimize, etc.
// * Asynchronicity
// * Cancellation
// * Scheduling and retrying
// * Test and production instances
// * Different frontends
