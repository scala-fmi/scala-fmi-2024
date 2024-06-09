package fmi.inventory

import fmi.utils.CirceUtils
import io.circe.Codec
import sttp.tapir
import sttp.tapir.{CodecFormat, Schema, SchemaType}

case class ProductId(id: String)

object ProductId:
  given Codec[ProductId] = CirceUtils.unwrappedCodec(ProductId.apply)(_.id)
  given Schema[ProductId] = Schema(SchemaType.SString())
  given tapir.Codec[String, ProductId, CodecFormat.TextPlain] = tapir.Codec.string.map(ProductId.apply)(_.id)

case class Product(id: ProductId, name: String, description: String, weightInGrams: Int) derives Codec, Schema
