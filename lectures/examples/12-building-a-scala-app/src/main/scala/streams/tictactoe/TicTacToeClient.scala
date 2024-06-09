package streams.tictactoe

import cats.effect.{ExitCode, IO, IOApp, Resource}
import fs2.{Pipe, Stream}
import streams.tictactoe.TicTacToeWebSocketData.NewState
import sttp.capabilities
import sttp.capabilities.fs2.Fs2Streams
import sttp.client3.httpclient.fs2.HttpClientFs2Backend
import sttp.client3.*
import sttp.tapir.client.sttp.SttpClientInterpreter
import sttp.tapir.client.sttp.ws.fs2.*

case class GameDetails(roomId: String, player: Player)

object TicTacToeClient extends IOApp.Simple:
  import TicTacToeConsole.*
  import ConsoleUtils.*

  type StreamingSttpBackend = SttpBackend[IO, Fs2Streams[IO] & capabilities.WebSockets]

  val enterGameDetails: IO[GameDetails] =
    for
      roomId <- prompt("Enter room id:")
      player <- promptForPlayer("Which player are you – X or O:")
    yield GameDetails(roomId, player)

  def playTicTacToe(backend: StreamingSttpBackend)(gameDetails: GameDetails): Stream[IO, Unit] =
    // TODO: Add client UI logic
    Stream
      .eval(connectToGame(backend)(gameDetails.roomId))
      .flatMap: pipe =>
        pipe(Stream.empty).foreach:
          case NewState(board) => TicTacToeConsole.printBoard(board)
          case _ => ???

  def connectToGame(backend: StreamingSttpBackend)(roomId: String): IO[Pipe[IO, Move, TicTacToeWebSocketData]] =
    SttpClientInterpreter()
      .toClientThrowErrors(TicTacToeEndpoints.ticTacToeWsEndpoint, Some(uri"ws://localhost:8080"), backend)
      .apply(roomId)

  def ticTacToeGame(backend: StreamingSttpBackend): IO[Unit] =
    for
      gameDetails <- enterGameDetails
      _ <- playTicTacToe(backend)(gameDetails).compile.drain
    yield ()

  def run: IO[Unit] =
    val app =
      for
        sttpBackend <- HttpClientFs2Backend.resource[IO]()
        result <- Resource.liftK(ticTacToeGame(sttpBackend))
      yield result

    app.use(_ => IO.unit)
