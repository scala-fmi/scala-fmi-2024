package streams.tictactoe

import cats.effect.{IO, IOApp}
import streams.tictactoe.TicTacToeOutcome.{Tie, Winner}
import cats.syntax.all.*

import scala.util.Try

object IsInteger:
  def unapply(numberString: String): Option[Int] =
    Try(numberString.toInt).toOption

object TicTacToeConsole:
  def promptForPlayer(promptMessage: String): IO[Player] =
    ConsoleUtils
      .prompt(promptMessage)
      .flatMap:
        case "X" | "x" => Player.X.pure
        case "O" | "o" => Player.O.pure
        case _ => IO.println("Unexpected input. Please answer 'X' or 'O'") >> promptForPlayer(promptMessage)

  def printBoard(ticTacToe: TicTacToe): IO[Unit] =
    val statePerCellVisualisation = ticTacToe.board.view
      .mapValues(_.toString)
      .toMap
      .withDefaultValue(" ")

    val rowsVisualisation = Cell.rows.map: row =>
      row
        .map(statePerCellVisualisation)
        .mkString("|")

    val boardVisualisation = rowsVisualisation.map(_ + "\n").mkString("-+-+-\n")

    IO.println(boardVisualisation)

  def parseCell(cellString: String): Either[String, Cell] = cellString match
    case s"${IsInteger(x)}, ${IsInteger(y)}" =>
      Cell.applyOption(x, y).toRight(left = "Coordinates out of range")
    case input => Left(s"$input is invalid. You should enter coordinates in format 'x, y'")

  def enterMove(ticTacToe: TicTacToe): IO[Move] =
    for
      _ <- IO.println("Enter cell:")
      cellString <- IO.readLine
      result <- parseCell(cellString) match
        case Right(cell) => Move(ticTacToe.currentPlayer, cell).pure[IO]
        case Left(error) => IO.println(error) >> enterMove(ticTacToe)
    yield result

  def ticTacToeMoveErrorDescription(error: TicTacToeMoveError): String = error match
    case TicTacToeMoveError.CellIsAlreadyTaken(cell, takenBy) =>
      s"Cell $cell is already taken by player $takenBy"
    case TicTacToeMoveError.PlayerNotInTurn(attemptedToMove, currentPlayer) =>
      s"It's not $attemptedToMove's turn. Current player is $currentPlayer'"
    case TicTacToeMoveError.GameFinished =>
      s"Cannot play more moves. Game is already finished"

  def printOutcome(outcome: TicTacToeOutcome): IO[Unit] =
    outcome match
      case Tie => IO.println("It's a tie!")
      case Winner(winningPlayer) =>
        IO.println(s"Player $winningPlayer won! Congratulations!")

object TicTacToeApp extends IOApp.Simple:
  import TicTacToeConsole.*

  def makeAMove(ticTacToe: TicTacToe): IO[TicTacToe] =
    for
      _ <- IO.println(s"Current player: ${ticTacToe.currentPlayer}. Please make a move")
      move <- enterMove(ticTacToe)
      result <- ticTacToe.makeMove(move) match
        case Right(updatedTicTacToe) => updatedTicTacToe.pure[IO]
        case Left(error) =>
          IO.println(s"Illegal move: ${ticTacToeMoveErrorDescription(error)}")
            >> makeAMove(ticTacToe)
    yield result

  def playATicTacToeRound(ticTacToe: TicTacToe): IO[(TicTacToe, TicTacToeOutcome)] =
    for
      _ <- printBoard(ticTacToe)
      updatedTicTacToe <- makeAMove(ticTacToe)
      result <- updatedTicTacToe.outcome match
        case Some(outcome) => (updatedTicTacToe, outcome).pure[IO]
        case None => playATicTacToeRound(updatedTicTacToe)
    yield result

  def askForAnotherGame: IO[Boolean] = ConsoleUtils.promptForBoolean("Another game?")

  def ticTacToeGame: IO[Unit] = for
    (finalBoard, outcome) <- playATicTacToeRound(TicTacToe.initial)
    _ <- printBoard(finalBoard)
    _ <- printOutcome(outcome)
    shouldPlayAnotherGame <- askForAnotherGame
    _ <- if shouldPlayAnotherGame then ticTacToeGame else ().pure[IO]
  yield ()

  def run: IO[Unit] = ticTacToeGame
