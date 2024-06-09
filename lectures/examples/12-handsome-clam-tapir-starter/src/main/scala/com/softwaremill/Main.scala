package com.softwaremill

import cats.effect.{ExitCode, IO, IOApp}
import com.comcast.ip4s.{Port, ipv4, port}
import com.softwaremill.LibraryController.{booksListingServerEndpoint, helloServerEndpoint}
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Router
import sttp.tapir.server.ServerEndpoint
import sttp.tapir.server.http4s.{Http4sServerInterpreter, Http4sServerOptions}
import sttp.tapir.server.metrics.prometheus.PrometheusMetrics
import sttp.tapir.swagger.bundle.SwaggerInterpreter

object Main extends IOApp:
  def appEndpoints: List[ServerEndpoint[Any, IO]] =
    val apiEndpoints: List[ServerEndpoint[Any, IO]] = List(helloServerEndpoint, booksListingServerEndpoint)

    val docEndpoints: List[ServerEndpoint[Any, IO]] = SwaggerInterpreter()
      .fromServerEndpoints[IO](apiEndpoints, "handsome-clam", "1.0.0")

    apiEndpoints ++ docEndpoints

  override def run(args: List[String]): IO[ExitCode] =
    val prometheusMetrics: PrometheusMetrics[IO] = PrometheusMetrics.default[IO]()

    val serverOptions: Http4sServerOptions[IO] =
      Http4sServerOptions
        .customiseInterceptors[IO]
        .metricsInterceptor(prometheusMetrics.metricsInterceptor())
        .options

    val routes = Http4sServerInterpreter[IO](serverOptions).toRoutes(appEndpoints :+ prometheusMetrics.metricsEndpoint)

    EmberServerBuilder
      .default[IO]
      .withHost(ipv4"0.0.0.0")
      .withPort(port"8080")
      .withHttpApp(Router("/" -> routes).orNotFound)
      .build
      .use { server =>
        IO.println(s"Go to http://localhost:${server.address.getPort}/docs to open SwaggerUI. Press ENTER key to exit.")
          >> IO.readLine
      }
      .as(ExitCode.Success)
