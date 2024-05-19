package http

import cats.effect.{IO, IOApp}
import cats.syntax.all.*

import scala.util.control.NonFatal

case class Author(name: String)
case class Book(name: String, authors: List[Author], genre: String)

case class BookSummary(id: String, name: String)

object LibraryApi:
  private def retrieve(path: String) =
    HttpClient.getCatsIO(s"http://localhost:8080/$path").flatMap { response =>
      if response.getStatusCode == 200 then IO.pure(response.getResponseBody)
      else IO.raiseError(BadResponse(response.getStatusCode))
    }

  def retrieveBookSummary(bookId: String): IO[BookSummary] =
    retrieve(s"books/$bookId/name")
      .map(BookSummary(bookId, _))

  def listBooks: IO[List[BookSummary]] = for
    bookIds <- retrieve("books").map(asList)
    books <- bookIds.map(retrieveBookSummary).parSequence
  yield books

  def retrieveAuthor(authorId: String): IO[Author] =
    for name <- retrieve(s"authors/$authorId/name")
    yield Author(name)

  def retrieveBook(bookId: String): IO[Book] =
    for
      (name, authorIds, genre) <- (
        retrieve(s"books/$bookId/name"),
        retrieve(s"books/$bookId/authors"),
        retrieve(s"books/$bookId/genre")
      ).parTupled
      authors <- asList(authorIds).map(retrieveAuthor).parSequence
    yield Book(name, authors, genre)

  private def asList(elementsString: String): List[String] = elementsString.split(",").toList

object LibraryClient extends IOApp.Simple:
  val selectBook: IO[Unit] = for
    books <- LibraryApi.listBooks
    _ <- IO.println("Available books: ")
    _ <- displayBooks(books)

    bookId <- promptInput("Select book id:")
    _ <- retrieveAndDisplayBook(bookId)

    anotherBookInput <- promptInput("Select another book?")
    _ <-
      if anotherBookInput == "y" then selectBook
      else IO.println("Goodbye :)!")
  yield ()

  def displayBooks(books: List[BookSummary]): IO[Unit] =
    books
      .map(b => s"ID: ${b.id}, name: ${b.name}")
      .map(IO.println)
      .sequence
      .void

  def retrieveAndDisplayBook(bookId: String): IO[Unit] = (for
    book <- LibraryApi.retrieveBook(bookId)
    _ <- displayBook(book)
  yield ()).recoverWith:
    case BadResponse(404) => IO.println("The book you selected was not found, please try again")

  def displayBook(book: Book) = for
    _ <- IO.println("Information about selected book:")
    _ <- IO.println(s"Name: ${book.name}")
    _ <- IO.println(s"Genre: ${book.genre}")
    _ <- IO.println(s"Authors: ${book.authors.map(_.name).mkString(", ")}")
  yield ()

  def promptInput(prompt: String): IO[String] = for
    _ <- IO.println(prompt)
    input <- IO.readLine
  yield input

  val libraryApp: IO[Unit] = selectBook.recoverWith:
    case NonFatal(_) =>
      IO.println("Something went wrong, retrying...") >> libraryApp

  def run: IO[Unit] =
    libraryApp >> IO(HttpClient.shutdown())
