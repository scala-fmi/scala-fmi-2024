package fmi

import cats.effect.{IO, IOApp, Resource}
import com.comcast.ip4s.{ipv4, port}
import fmi.library.Library
import fmi.server.LibraryController
import org.http4s.HttpRoutes
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Server
import sttp.tapir.server.http4s.{Http4sServerInterpreter, Http4sServerOptions}
import sttp.tapir.server.metrics.prometheus.PrometheusMetrics
import sttp.tapir.swagger.bundle.SwaggerInterpreter

object LibraryServer extends IOApp.Simple:
  def libraryAppRoutes: HttpRoutes[IO] =
    val library = Library.TheGreatLibrary
    val libraryController = LibraryController(library)
    val prometheusMetrics = PrometheusMetrics.default[IO]()

    val apiEndpoints = libraryController.endpoints
    val docEndpoints = SwaggerInterpreter().fromServerEndpoints[IO](apiEndpoints, "library-app", "1.0.0")
    val prometheusEndpoint = prometheusMetrics.metricsEndpoint

    val serverOptions =
      Http4sServerOptions
        .customiseInterceptors[IO]
        .metricsInterceptor(prometheusMetrics.metricsInterceptor())
        .options

    Http4sServerInterpreter[IO](serverOptions).toRoutes(prometheusEndpoint +: apiEndpoints ::: docEndpoints)

  val server: Resource[IO, Server] =
    EmberServerBuilder
      .default[IO]
      .withHost(ipv4"0.0.0.0")
      .withPort(port"8080")
      .withHttpApp(libraryAppRoutes.orNotFound)
      .build

  def run: IO[Unit] =
    server
      .use(_ => IO.never)
      .onCancel(IO.println("Bye, see you again \uD83D\uDE0A"))
