package http

import cats.effect.{IO, IOApp}
import cats.syntax.all.*
import com.comcast.ip4s.{ipv4, port}
import org.http4s.HttpRoutes
import org.http4s.dsl.io.*
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits.*
import util.Utils

object IOWebServer extends IOApp.Simple:
  def doWork: IO[Int] = IO:
    Utils.doWork
    Utils.doWork

    42

  val doWorkRoutes = HttpRoutes.of[IO]:
    case GET -> Root / "do-work" =>
      Ok(doWork.map(_.toString))

  val ioWebApp = doWorkRoutes.orNotFound

  val server =
    EmberServerBuilder
      .default[IO]
      .withHost(ipv4"0.0.0.0")
      .withPort(port"8080")
      .withHttpApp(ioWebApp)
      .build

  def run: IO[Unit] =
    server
      .use(_ => IO.never)
      .onCancel(IO.println("Bye, see you again \uD83D\uDE0A"))
