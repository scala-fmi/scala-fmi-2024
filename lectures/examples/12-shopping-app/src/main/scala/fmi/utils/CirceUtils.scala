package fmi.utils

import io.circe.{Codec, Decoder, Encoder}

object CirceUtils:
  val stringCodec = Codec.from(Decoder[String], Encoder[String])

  def unwrappedCodec[W, U : Encoder : Decoder](wrap: U => W)(unwrap: W => U): Codec[W] =
    Codec.from(Decoder[U], Encoder[U]).iemap(u => Right(wrap(u)))(unwrap)
