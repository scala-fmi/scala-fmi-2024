package fmi

import cats.effect.{IO, IOApp}
import com.comcast.ip4s.{ipv4, port}
import fmi.server.LibraryHttpApp
import org.http4s.ember.server.EmberServerBuilder

object LibraryServer extends IOApp.Simple:
  val server =
    EmberServerBuilder
      .default[IO]
      .withHost(ipv4"0.0.0.0")
      .withPort(port"8080")
      .withHttpApp(LibraryHttpApp.libraryApp)
      .build

  def run: IO[Unit] =
    server
      .use(_ => IO.never)
      .onCancel(IO.println("Bye, see you again \uD83D\uDE0A"))
