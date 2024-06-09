package fmi.server

import cats.data.Kleisli
import cats.effect.IO
import cats.syntax.all.*
import fmi.library.Library.TheGreatLibrary
import fmi.library.{AuthorId, BookId, BookSummary}
import org.http4s.{HttpRoutes, Request, Response}
import org.http4s.dsl.io.*
import org.http4s.implicits.*

object LibraryHttpApp:
  import org.http4s.circe.CirceEntityCodec.given

  val bookRoutes: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case GET -> Root / "books" =>
      val books = TheGreatLibrary.allBooks.nested.map(BookSummary(_)).value

      Ok(books)
    case GET -> Root / "books" / bookIdSegment =>
      val bookId = BookId(bookIdSegment)

      TheGreatLibrary
        .findBook(bookId)
        .flatMap(
          _.fold(NotFound())(book => Ok(book))
        )
  }

  val authorRoutes: HttpRoutes[IO] = HttpRoutes.of[IO] { case GET -> Root / "authors" / authorIdSegment =>
    val authorId = AuthorId(authorIdSegment)

    TheGreatLibrary
      .findAuthor(authorId)
      .flatMap(
        _.fold(NotFound())(author => Ok(author))
      )
  }

  val libraryApp: Kleisli[IO, Request[IO], Response[IO]] =
    (bookRoutes <+> authorRoutes).orNotFound
