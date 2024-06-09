package streams

import cats.effect.{IO, IOApp, Resource}
import cats.syntax.all.*
import com.comcast.ip4s.{ipv4, port}
import fs2.Pipe
import fs2.concurrent.Topic
import io.circe.Codec
import org.http4s.ember.server.EmberServerBuilder
import sttp.capabilities.fs2.Fs2Streams
import sttp.tapir.Codec.textWebSocketFrame
import sttp.tapir.json.circe.*
import sttp.tapir.server.http4s.Http4sServerInterpreter
import sttp.tapir.*

case class Message(fromUser: String, message: String) derives Schema, Codec

object Fs207ChatServer extends IOApp.Simple:
  val chatWsEndpoint =
    endpoint
      .in("chat-ws")
      .out(webSocketBody[Message, CodecFormat.Json, Message, CodecFormat.Json](Fs2Streams[IO]))
      .get

  def chatWsLogic(chatTopic: Topic[IO, Message]) =
    chatWsEndpoint.serverLogicSuccess: _ =>
      val wsStream: Pipe[IO, Message, Message] = inputStream =>
        val publishMessageStream = inputStream.through(chatTopic.publish)
        val receiveMessageStream = chatTopic.subscribe(100)

        receiveMessageStream.mergeHaltBoth(publishMessageStream)

      wsStream.pure[IO]

  def chatApp =
    for
      chatTopic <- Resource.liftK(Topic[IO, Message])

      chatRoute = chatWsLogic(chatTopic)

      routes = Http4sServerInterpreter[IO]().toWebSocketRoutes(chatRoute)

      server <- EmberServerBuilder
        .default[IO]
        .withHost(ipv4"0.0.0.0")
        .withPort(port"8080")
        .withHttpWebSocketApp(wsBuilder => routes(wsBuilder).orNotFound)
        .build
    yield server

  def run: IO[Unit] = chatApp.use(_ => IO.never)
