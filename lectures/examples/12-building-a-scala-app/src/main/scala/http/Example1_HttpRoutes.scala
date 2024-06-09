package http

import cats.data.Kleisli
import cats.effect.{ExitCode, IO, IOApp}
import org.http4s.{HttpApp, HttpRoutes, Request, Response, Status}
import org.http4s.dsl.io.*
import org.http4s.implicits.*
import cats.syntax.semigroupk.*
import com.comcast.ip4s.{ipv4, port}
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Router

import scala.concurrent.ExecutionContext.global

object Example1_HttpRoutes:

  val mostSimpleRoute = HttpRoutes.of[IO]:
    case _ => IO(Response(Status.Ok))

  val helloRoutes = HttpRoutes.of[IO]:
    case GET -> Root / "hello" / name => //   GET example.com/hello/zdravko
      Ok(s"Hello, $name.")
    case GET -> Root / "hola" / name =>
      Ok(s"¡Hola, $name!")

  val combined = helloRoutes <+> mostSimpleRoute

  // Or define them under different path prefixes like that
  val combinedWithRouter = Router("/" -> helloRoutes, "/simple" -> mostSimpleRoute)

  val httpApp: HttpApp[IO] = combinedWithRouter.orNotFound

object HelloWorldApp extends IOApp:
  def run(args: List[String]): IO[ExitCode] =
    EmberServerBuilder
      .default[IO]
      .withHost(ipv4"0.0.0.0")
      .withPort(port"8080")
      .withHttpApp(Example1_HttpRoutes.httpApp)
      .build
      .use(_ => IO.never)
      .as(ExitCode.Success)
