package streams

import cats.effect.std.Console
import cats.effect.{Concurrent, IO, IOApp, Temporal}
import com.comcast.ip4s.*
import fs2.io.net.{ConnectException, Network, Socket}
import fs2.{Stream, text}

import scala.concurrent.duration.*

def connect[F[_] : Temporal : Network](address: SocketAddress[Host]): Stream[F, Socket[F]] =
  Stream
    .resource(Network[F].client(address))
    .handleErrorWith { case _: ConnectException =>
      connect(address).delayBy(5.seconds)
    }

def client[F[_] : Temporal : Console : Network]: Stream[F, Unit] =
  connect(SocketAddress(host"localhost", port"5555")).flatMap: serverSocket =>
    val writing =
      Stream("Hello, world!")
        .interleave(Stream.constant("\n"))
        .through(text.utf8.encode)
        .through(serverSocket.writes)

    val reading =
      serverSocket.reads
        .through(text.utf8.decode)
        .through(text.lines)
        .foreach: response =>
          Console[F].println(s"Response: $response")

    writing.concurrently(reading)

object TCPClient extends IOApp.Simple:
  def run: IO[Unit] = client[IO].compile.drain

def echoServer[F[_] : Concurrent : Network]: F[Unit] =
  Network[F]
    .server(port = Some(port"5555"))
    .map: clientSocket =>
      clientSocket.reads
        .through(text.utf8.decode)
        .through(text.lines)
        .interleave(Stream.constant("\n"))
        .through(text.utf8.encode)
        .through(clientSocket.writes)
        .handleErrorWith(_ => Stream.empty) // handle errors of client sockets
    .parJoin(2)
    .compile
    .drain

object EchoServer extends IOApp.Simple:
  def run: IO[Unit] = echoServer[IO]
