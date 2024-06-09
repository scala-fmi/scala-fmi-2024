package streams.tictactoe

import cats.effect.IO
import cats.syntax.all.*

object ConsoleUtils:
  def prompt(promptMessage: String): IO[String] = for
    _ <- IO.println(promptMessage)
    input <- IO.readLine
  yield input

  def promptForBoolean(promptMessage: String): IO[Boolean] =
    ConsoleUtils
      .prompt(promptMessage)
      .flatMap:
        case "y" | "yes" => true.pure
        case "n" | "no" => true.pure
        case _ =>
          IO.println("Unexpected input. Please answer 'yes' or 'no'") >>
            ConsoleUtils.promptForBoolean(promptMessage)
