package http

import io.IO
import org.asynchttpclient.*
import org.asynchttpclient.Dsl.*

import scala.util.{Success, Try}

object HttpClient:
  val client = asyncHttpClient()

  def getIO(url: String): IO[Response] =
    IO.async { (callback, ec) =>
      val eventualResponse = client.prepareGet(url).setFollowRedirect(true).execute()
      // using a Try as the computation might fail
      eventualResponse.addListener(() => callback(Try(eventualResponse.get())), r => ec.execute(r))
    }

  def getCatsIO(url: String): cats.effect.IO[Response] =
    cats.effect.IO.executionContext.flatMap { ec =>
      cats.effect.IO.async_ { callback =>
        val eventualResponse = client.prepareGet(url).setFollowRedirect(true).execute()
        eventualResponse.addListener(() => callback(Try(eventualResponse.get()).toEither), r => ec.execute(r))
      }
    }

  def shutdown() = client.close()

case class BadResponse(statusCode: Int) extends Exception
