package streams

import cats.effect.{IO, IOApp}
import com.comcast.ip4s.{ipv4, port}
import doobie.*
import doobie.implicits.*
import fs2.Stream
import org.http4s.HttpRoutes
import org.http4s.dsl.io.*
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits.*
import sql.DoobieApp

import scala.concurrent.duration.{DurationInt, FiniteDuration}

case class City(name: String, country: String, population: Int)

object Fs204HttpWithDoobie extends IOApp.Simple:
  val allCities =
    sql"""
         SELECT name, countrycode, population
         FROM city
    """
      .query[City]
      .stream
      .transact(DoobieApp.dbTransactor)

  val count: Stream[IO, FiniteDuration] =
    Stream
      .awakeEvery[IO](100.millis)

  val routes = HttpRoutes.of[IO]:
    case GET -> Root / "counter" =>
      Ok(count.map(_.toString + "\n").take(10))
    case GET -> Root / "cities" =>
      Ok(allCities.map(_.toString + "\n"))
    case GET -> Root / "combined" =>
      val output = (count zip allCities).map:
        case (elapsedTime, city) => s"$elapsedTime: $city\n"

      Ok(output)

  val httpApp = routes.orNotFound

  val serverBuilder =
    EmberServerBuilder
      .default[IO]
      .withHost(ipv4"0.0.0.0")
      .withPort(port"8080")
      .withHttpApp(httpApp)
      .build

  def run: IO[Unit] = serverBuilder.use(_ => IO.never)
