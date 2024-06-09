package fmi.inventory

import cats.effect.IO
import cats.syntax.all.*
import doobie.*
import doobie.implicits.*
import fmi.infrastructure.db.DoobieDatabase.DbTransactor
import fmi.utils.DerivationConfiguration.given
import io.circe.derivation.ConfiguredCodec
import sttp.tapir.Schema

class ProductStockDao(dbTransactor: DbTransactor):
  def retrieveAllAvailableStock: IO[List[ProductStock]] =
    sql"""
      SELECT product_id, quantity
      FROM product_stock
      WHERE quantity > 0
    """
      .query[ProductStock]
      .to[List]
      .transact(dbTransactor)

  def retrieveStockForProduct(productId: ProductId): IO[Option[ProductStock]] =
    sql"""
      SELECT product_id, quantity
      FROM product_stock
      WHERE product_id = $productId
    """
      .query[ProductStock]
      .option
      .transact(dbTransactor)

  def applyInventoryAdjustment(inventoryAdjustment: InventoryAdjustment): IO[AdjustmentResult] =
    applyInventoryAdjustmentAction(inventoryAdjustment)
      .transact(dbTransactor)
      .as(SuccessfulAdjustment: AdjustmentResult)
      .recover { case NotEnoughStockAvailableException =>
        NotEnoughStockAvailable
      }

  def applyInventoryAdjustmentAction(inventoryAdjustment: InventoryAdjustment): ConnectionIO[Unit] =
    // TODO: Replace by a single query
    inventoryAdjustment.adjustments
      // Sorting allows us to avoid deadlock
      .sortBy(_.product.id)
      .map(adjustStockForProduct)
      .sequence
      .void

  private def adjustStockForProduct(adjustment: ProductStockAdjustment): ConnectionIO[Unit] =
    val addQuery = sql"""
      INSERT INTO product_stock as ps (product_id, quantity)
      VALUES (${adjustment.product}, ${adjustment.adjustmentQuantity})
      ON CONFLICT (product_id) DO UPDATE SET
      quantity = ps.quantity + EXCLUDED.quantity
    """
    val substractQuery = sql"""
      UPDATE product_stock
      SET quantity = quantity + ${adjustment.adjustmentQuantity}
      WHERE product_id = ${adjustment.product} AND quantity + ${adjustment.adjustmentQuantity} >= 0
    """

    val query = if adjustment.adjustmentQuantity >= 0 then addQuery else substractQuery

    query.update.run
      .map(updatedRows => updatedRows == 1)
      .ifM[Unit](().pure[ConnectionIO], NotEnoughStockAvailableException.raiseError[ConnectionIO, Unit])

case object NotEnoughStockAvailableException extends Exception

sealed trait AdjustmentResult derives ConfiguredCodec, Schema
case object SuccessfulAdjustment extends AdjustmentResult
case object NotEnoughStockAvailable extends AdjustmentResult
