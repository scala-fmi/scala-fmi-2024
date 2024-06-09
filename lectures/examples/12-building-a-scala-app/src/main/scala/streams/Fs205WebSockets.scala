package streams

import cats.effect.*
import cats.syntax.all.*
import com.comcast.ip4s.{ipv4, port}
import fs2.{Pipe, Stream}
import org.http4s.*
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits.*
import org.http4s.server.websocket.WebSocketBuilder2
import sttp.capabilities
import sttp.capabilities.WebSockets
import sttp.capabilities.fs2.Fs2Streams
import sttp.tapir.*
import sttp.tapir.server.ServerEndpoint
import sttp.tapir.server.http4s.{Http4sServerInterpreter, serverSentEventsBody}
import scala.concurrent.duration.*

object Fs205WebSocket extends IOApp.Simple:
  val echoWsEndpoint: Endpoint[Unit, Unit, Unit, Pipe[IO, String, String], Fs2Streams[IO] & capabilities.WebSockets] =
    endpoint
      .in("echo-ws")
      .out(webSocketBody[String, CodecFormat.TextPlain, String, CodecFormat.TextPlain](Fs2Streams[IO]))
      .get

  val echoWs: ServerEndpoint[Fs2Streams[IO] & WebSockets, IO] = echoWsEndpoint.serverLogicSuccess: _ =>
    val wsStream: Pipe[IO, String, String] = inputStream =>
      for
        input <- inputStream
        output <- Stream(
          s"You sent the server: $input.",
          "Yay :)"
        )
      yield output

    wsStream.pure[IO]

  val countToTen: Stream[IO, String] =
    Stream
      .awakeEvery[IO](1.second)
      .map(_.toString + "\n")
      .take(10)

  import org.http4s.dsl.io.*
  val counterRoutes = HttpRoutes.of[IO]:
    case GET -> Root / "counter" =>
      Ok(countToTen)

  val routes = Http4sServerInterpreter[IO]().toWebSocketRoutes(echoWs)

  def httpApp(wsBuilder: WebSocketBuilder2[IO]): HttpApp[IO] = (routes(wsBuilder) <+> counterRoutes).orNotFound

  val httpServerResource = EmberServerBuilder
    .default[IO]
    .withHost(ipv4"0.0.0.0")
    .withPort(port"8080")
    .withHttpWebSocketApp(httpApp)
    .build

  def run: IO[Unit] = httpServerResource.use(_ => IO.never)
