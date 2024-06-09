package fmi.server

import cats.effect.IO
import cats.syntax.all.*
import fmi.library.{BookSummary, Library, LibraryEndpoints}

class LibraryController(library: Library):

  val retrieveBooks = LibraryEndpoints.retrieveBooksEndpoint.serverLogicSuccess: _ =>
    library.allBooks.nested.map(BookSummary.apply).value

  val retrieveBook = LibraryEndpoints.retrieveBookEndpoint.serverLogic: bookId =>
    library.findBook(bookId).map(_.toRight(s"Book $bookId not found"))

  val retrieveAuthors = LibraryEndpoints.retrieveAuthorEndpoint.serverLogic: authorId =>
    library.findAuthor(authorId).map(_.toRight(s"Author $authorId not found)"))

  val endpoints = List(retrieveBook, retrieveBooks, retrieveAuthors)
