package streams.tictactoe

import cats.effect.{IO, IOApp, Resource}
import com.comcast.ip4s.{ipv4, port}
import org.http4s.ember.server.EmberServerBuilder
import sttp.tapir.server.http4s.Http4sServerInterpreter

object TicTacToeServer extends IOApp.Simple:
  def ticTacToeApp =
    for
      ticTacToeService <- Resource.liftK(TicTacToeService())
      ticTacToeController = TicTacToeController(ticTacToeService)

      routes = Http4sServerInterpreter[IO]().toWebSocketRoutes(ticTacToeController.ticTacToeWs)

      server <- EmberServerBuilder
        .default[IO]
        .withHost(ipv4"0.0.0.0")
        .withPort(port"8080")
        .withHttpWebSocketApp(wsBuilder => routes(wsBuilder).orNotFound)
        .build
    yield server

  def run: IO[Unit] = ticTacToeApp.use(_ => IO.never)
