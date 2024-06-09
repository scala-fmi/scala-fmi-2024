package fmi.library

import fmi.library.LibraryCodecs.given
import sttp.model.StatusCode.NotFound
import sttp.tapir.json.circe.*
import sttp.tapir.*

object LibraryEndpoints:
  val baseEndpoint: PublicEndpoint[Unit, Unit, Unit, Any] = endpoint
  val v1BaseEndpoint: Endpoint[Unit, Unit, Unit, Unit, Any] = baseEndpoint.in("v1")

  val booksRootEndpoint: Endpoint[Unit, Unit, Unit, Unit, Any] = v1BaseEndpoint.in("books")

  val retrieveBooksEndpoint: Endpoint[Unit, Unit, Unit, List[BookSummary], Any] =
    booksRootEndpoint
      .out(jsonBody[List[BookSummary]])
      .get

  val retrieveBookEndpoint: Endpoint[Unit, BookId, String, Book, Any] =
    booksRootEndpoint
      .in(path[BookId].name("book-id"))
      .out(jsonBody[Book])
      .errorOut(statusCode(NotFound).and(jsonBody[String]))
      .get

  val authorsRootEndpoint: Endpoint[Unit, Unit, Unit, Unit, Any] = v1BaseEndpoint.in("authors")

  val retrieveAuthorEndpoint: Endpoint[Unit, AuthorId, String, Author, Any] =
    authorsRootEndpoint
      .in(path[AuthorId].name("auth-id"))
      .out(jsonBody[Author])
      .errorOut(statusCode(NotFound).and(jsonBody[String]))
      .get
