package fmi

import cats.effect.{IO, IOApp}
import cats.syntax.all.*
import fmi.client.{LibraryApi, LibraryClientUI}
import fmi.library.{Author, Book}
import sttp.client3.*
import sttp.client3.httpclient.fs2.HttpClientFs2Backend

case class BookWithAuthors(book: Book, authors: List[Author])

object LibraryClient extends IOApp.Simple:
  val app = for
    sttpBackend <- HttpClientFs2Backend.resource[IO]()

    libraryApi = new LibraryApi(uri"http://localhost:8080", sttpBackend)
    libraryClientUI = new LibraryClientUI(libraryApi)
  yield libraryClientUI

  def run: IO[Unit] =
    app
      .use(ui => ui.libraryApp)
      .guarantee(IO.println("Thank you for browsing our library :)!"))
