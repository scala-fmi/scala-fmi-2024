package fmi.client

import cats.data.OptionT
import cats.effect.IO
import cats.syntax.all.*
import fmi.BookWithAuthors
import fmi.library.*
import sttp.client3.SttpBackend
import sttp.model.Uri
import sttp.tapir.client.sttp.SttpClientInterpreter

class LibraryApi(base: Uri, sttpBackend: SttpBackend[IO, Any]):
  private val interpreter = SttpClientInterpreter()

  def listBooks: IO[List[BookSummary]] =
    interpreter
      .toClientThrowErrors(LibraryEndpoints.retrieveBooksEndpoint, Some(base), sttpBackend)
      .apply(())

  def retrieveBook(bookId: BookId): IO[Option[Book]] =
    interpreter
      .toClientThrowDecodeFailures(LibraryEndpoints.retrieveBookEndpoint, Some(base), sttpBackend)
      .apply(bookId)
      .map(_.toOption)

  def retrieveAuthor(authorId: AuthorId): IO[Author] =
    interpreter
      .toClientThrowErrors(LibraryEndpoints.retrieveAuthorEndpoint, Some(base), sttpBackend)
      .apply(authorId)

  def retrieveBookWithAuthors(bookId: BookId): IO[Option[BookWithAuthors]] = (for
    book <- OptionT(retrieveBook(bookId))
    authors <- OptionT.liftF(book.authors.parTraverse(authorId => retrieveAuthor(authorId)))
  yield BookWithAuthors(book, authors)).value
