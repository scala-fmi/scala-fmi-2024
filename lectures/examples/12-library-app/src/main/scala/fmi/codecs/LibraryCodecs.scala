package fmi.codecs

import fmi.library.{Author, AuthorId, Book, BookId, BookSummary}
import io.circe.Codec
import io.circe.Encoder
import io.circe.Decoder

object LibraryCodecs:
  def unwrappedCodec[W, U : Encoder : Decoder](wrap: U => W)(unwrap: W => U): Codec[W] =
    Codec.from(Decoder[U], Encoder[U]).iemap(u => Right(wrap(u)))(unwrap)
