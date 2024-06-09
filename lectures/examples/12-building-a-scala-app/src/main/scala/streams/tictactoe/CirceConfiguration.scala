package streams.tictactoe

import io.circe.{Codec, Decoder, Encoder}
import io.circe.derivation.Configuration

object CirceConfiguration:
  given Configuration = Configuration.default.withDiscriminator("type")

  given [T : Codec]: Codec[List[T]] = Codec.from(Decoder.decodeList[T], Encoder.encodeList[T])
