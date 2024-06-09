package com.softwaremill

import cats.effect.IO
import com.softwaremill.Endpoints.{booksListing, helloEndpoint}
import sttp.tapir.server.ServerEndpoint

object LibraryController:
  val helloServerEndpoint: ServerEndpoint[Any, IO] = helloEndpoint.serverLogicSuccess(user => IO.pure(s"Hello ${user.name}"))
  val booksListingServerEndpoint: ServerEndpoint[Any, IO] = booksListing.serverLogicSuccess(_ => IO.pure(Library.books))
