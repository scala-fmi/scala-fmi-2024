package fmi.shopping

import fmi.inventory.InventoryAdjustment
import io.circe.Codec
import sttp.tapir.Schema

case class ShoppingCart(orderLines: List[OrderLine] = List.empty) derives Codec, Schema:
  def add(orderLine: OrderLine): ShoppingCart =
    ShoppingCart(orderLine :: orderLines)

  def toInventoryAdjustment: InventoryAdjustment =
    InventoryAdjustment(orderLines.map(_.toProductStockAdjustment))
