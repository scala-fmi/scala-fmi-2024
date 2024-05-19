package http

import util.HttpServiceUrls
import io.{IO, IORuntime}

@main def runHttpRequestsExample =
//  val myIp = HttpClient
//    .getIO("http://icanhazip.com")
//    .map(_.getResponseBody)

//  val app = myIp >>= IO.println

  val randomNumber = HttpClient
    .getIO(HttpServiceUrls.randomNumberUpTo(256))
    .map(_.getResponseBody)

  val app = (randomNumber zip randomNumber).map(_.toString) >>= IO.println

  app.unsafeRunSync(IORuntime.default)
//
//  val example = HttpClient
//    .getIO("http://example.org")
//    .map(_.getResponseBody)
//
//  val endResult = for
//    (ipResult, exampleResult) <- myIp zip example
//    _ <- IO.println(ipResult)
//    _ <- IO.println(exampleResult)
//    r <- HttpClient.getIO(HttpServiceUrls.randomNumberUpTo(ipResult.length + exampleResult.length))
//  yield r.getResponseBody

//  (endResult >>= IO.println).unsafeRunSync(IORuntime.default)

  // We will talk about how to manage resource later. Now we need to shut them down manually
  HttpClient.shutdown()
  IORuntime.default.shutdown()
