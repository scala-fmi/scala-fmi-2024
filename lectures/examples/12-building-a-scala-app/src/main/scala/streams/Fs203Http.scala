package streams

import cats.effect.{IO, IOApp}
import com.comcast.ip4s.{ipv4, port}
import fs2.Stream
import org.http4s.HttpRoutes
import org.http4s.dsl.io.*
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits.*

import scala.concurrent.duration.DurationInt

object Fs203Http extends IOApp.Simple:
  val countToTen: Stream[IO, String] =
    Stream
      .awakeEvery[IO](1.second)
      .map(_.toString + "\n")
      .take(10)

  val counterRoutes = HttpRoutes.of[IO]:
    case GET -> Root / "counter" =>
      Ok(countToTen)

  val httpApp = counterRoutes.orNotFound

  val serverBuilder =
    EmberServerBuilder
      .default[IO]
      .withHost(ipv4"0.0.0.0")
      .withPort(port"8080")
      .withHttpApp(httpApp)
      .build

  def run: IO[Unit] = serverBuilder.use(_ => IO.never)
