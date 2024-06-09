package fmi.library

import io.circe.{Codec, Decoder, Encoder}

object LibraryCodecs:
  def unwrappedCodec[W, U : Encoder : Decoder](wrap: U => W)(unwrap: W => U): Codec[W] =
    Codec.from(Decoder[U], Encoder[U]).iemap(u => Right(wrap(u)))(unwrap)
