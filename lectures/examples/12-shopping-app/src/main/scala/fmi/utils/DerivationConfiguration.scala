package fmi.utils

import io.circe.derivation.Configuration
import io.circe.{Codec, Decoder, Encoder}
import sttp.tapir.generic.Configuration as TapirConfiguration

object DerivationConfiguration:
  given Configuration = Configuration.default.withDiscriminator("type")
  given TapirConfiguration = TapirConfiguration.default.withDiscriminator("type")

  given [T : Codec]: Codec[List[T]] = Codec.from(Decoder.decodeList[T], Encoder.encodeList[T])
