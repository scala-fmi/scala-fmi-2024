package fmi.inventory

import io.circe.Codec
import sttp.tapir.Schema

case class ProductStock(product: ProductId, quantity: Int) derives Codec, Schema

case class ProductStockAdjustment(product: ProductId, adjustmentQuantity: Int) derives Codec, Schema
case class InventoryAdjustment(adjustments: List[ProductStockAdjustment]) derives Codec, Schema
