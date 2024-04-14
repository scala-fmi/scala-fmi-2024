
import lectures.io.IO
import scala.io.StdIn

//val run = IO.println("Hello World")
//val list1 = List(run, run)
//
//val list2 = List(IO.println("Hello World"), IO.println("Hello World"))

val program = IO
  .println("What's your name?")
  .flatMap(_ => IO.readln)
  .map(name => s"Hello $name!!!")
  .flatMap(IO.println)

val askForName = IO.println("What's your name?")

val program2 = for
  _ <- askForName
  _ <- askForName
  name <- IO.readln
  _ <- IO.println(s"Hello $name!!!")
yield ()

program2.unsafeRun()
