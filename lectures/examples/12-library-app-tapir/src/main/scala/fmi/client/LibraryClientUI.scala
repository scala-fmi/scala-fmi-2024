package fmi.client

import cats.effect.IO
import cats.syntax.all.*
import fmi.BookWithAuthors
import fmi.library.{BookId, BookSummary}

import scala.concurrent.duration.*
import scala.util.control.NonFatal

class LibraryClientUI(libraryApi: LibraryApi):
  val libraryApp: IO[Unit] = selectBook.recoverWith:
    case NonFatal(_) =>
      IO.println("Something went wrong, retrying...") >> IO.sleep(1.second) >> libraryApp

  def selectBook: IO[Unit] = for
    books <- libraryApi.listBooks
    _ <- IO.println("Available books: ")
    _ <- displayBooks(books)

    bookId <- promptInput("Select book id:").map(BookId.apply)
    _ <- retrieveAndDisplayBook(bookId)

    anotherBookInput <- promptInput("Select another book?")
    _ <-
      if anotherBookInput == "y" then selectBook
      else IO.unit
  yield ()

  def displayBooks(books: List[BookSummary]): IO[Unit] =
    // No need for parallelism here so we can use traverse instead of parTraverse
    // Otherwise books will be printed in unpredictable order
    books
      .map(b => s"ID: ${b.id.id}, name: ${b.name}")
      .traverse(IO.println)
      .void // void turns the result into IO[Unit]

  def retrieveAndDisplayBook(bookId: BookId): IO[Unit] =
    for
      maybeBookWithAuthors <- libraryApi.retrieveBookWithAuthors(bookId)
      _ <- maybeBookWithAuthors.fold(IO.println("Book not found"))(displayBook)
    yield ()

  def displayBook(bookWithAuthours: BookWithAuthors) = for
    _ <- IO.println("Information about selected book:")

    BookWithAuthors(book, authors) = bookWithAuthours

    _ <- IO.println(s"Name: ${book.name}")
    _ <- IO.println(s"Genre: ${book.genre}")
    _ <- IO.println(s"Authors: ${authors.map(_.name).mkString(", ")}")
  yield ()

  def promptInput(prompt: String): IO[String] = for
    _ <- IO.println(prompt)
    input <- IO.readLine
  yield input
