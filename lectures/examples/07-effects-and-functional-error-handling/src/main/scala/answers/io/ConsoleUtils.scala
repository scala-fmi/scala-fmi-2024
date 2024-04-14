package answers.io

object ConsoleUtils:
  def prompt(promptMessage: String): IO[String] = for
    _ <- IO.println(promptMessage)
    input <- IO.readln
  yield input

  def promptForBoolean(promptMessage: String): IO[Boolean] =
    ConsoleUtils
      .prompt(promptMessage)
      .flatMap:
        case "y" | "yes" => IO.of(true)
        case "n" | "no" => IO.of(false)
        case _ =>
          IO.println("Unexpected input. Please answer 'yes' or 'no'") >>
            ConsoleUtils.promptForBoolean(promptMessage)
