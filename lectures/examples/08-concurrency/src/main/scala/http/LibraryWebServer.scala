package http

import cats.effect.{IO, IOApp}
import cats.syntax.all.*
import com.comcast.ip4s.{ipv4, port}
import library.{AuthorId, BookId, Library}
import org.http4s.HttpRoutes
import org.http4s.dsl.io.*
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits.*

object LibraryWebServer extends IOApp.Simple:
  val library = Library.TheGreatLibrary

  val booksRoutes = HttpRoutes.of[IO]:
    case GET -> Root / "books" =>
      // comma-separated list of all book ids
//      val responseString =
//        for booksIds <- library.allBooks
//        yield booksIds.map(_.id).mkString

      val responseString = library.allBooks
        .map(booksIds => booksIds.map(_.id))
        .map(_.mkString(","))

      Ok(responseString)

    case GET -> Root / "books" / bookIdSegment / "name" =>
      library
        .findBook(BookId(bookIdSegment))
        .flatMap(_.fold(NotFound(s"Book $bookIdSegment not found"))(book => Ok(book.name)))
//          case Some(book) => Ok(book.name)
//          case None => NotFound(s"Book $bookIdSegment not found")

    case GET -> Root / "books" / bookIdSegment / "genre" =>
      library
        .findBook(BookId(bookIdSegment))
        .flatMap(_.fold(NotFound(s"Book $bookIdSegment not found"))(book => Ok(book.genre)))
    case GET -> Root / "books" / bookIdSegment / "authors" =>
      library
        .findBook(BookId(bookIdSegment))
        .flatMap:
          case Some(book) => Ok(book.authors.map(_.id).mkString(","))
          case None => NotFound(s"Book $bookIdSegment not found")

  val authorRoutes = HttpRoutes.of[IO]:
    case GET -> Root / "authors" / authorIdSegment / "name" =>
      library
        .findAuthor(AuthorId(authorIdSegment))
        .flatMap:
          case Some(author) => Ok(author.name)
          case None => NotFound(s"Author $authorIdSegment not found")

  val libraryApp = (booksRoutes <+> authorRoutes).orNotFound

  val server =
    EmberServerBuilder
      .default[IO]
      .withHost(ipv4"0.0.0.0")
      .withPort(port"8080")
      .withHttpApp(libraryApp)
      .build

  def run: IO[Unit] =
    server
      .use(_ => IO.never)
      .onCancel(IO.println("Bye, see you again \uD83D\uDE0A"))
