package fmi

import cats.effect.kernel.Resource
import cats.effect.{IO, IOApp}
import cats.syntax.all.*
import fmi.client.{LibraryApi, LibraryClientUI}
import fmi.library.{Author, Book}
import org.http4s.ember.client.EmberClientBuilder

case class BookWithAuthors(book: Book, authors: List[Author])

object LibraryClient extends IOApp.Simple:
  val app = for
    client <- EmberClientBuilder.default[IO].build

    libraryApi = new LibraryApi(client)
    libraryClientUI = new LibraryClientUI(libraryApi)
  yield libraryClientUI

  def run: IO[Unit] =
    app
      .use(ui => ui.selectBook)
      .guarantee(IO.println("Thank you for browsing our library :)!"))
