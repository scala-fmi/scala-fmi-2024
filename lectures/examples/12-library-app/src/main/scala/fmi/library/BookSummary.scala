package fmi.library

import io.circe.Codec

object BookSummary:
  def apply(book: Book): BookSummary = BookSummary(book.id, book.name)

case class BookSummary(id: BookId, name: String) derives Codec
