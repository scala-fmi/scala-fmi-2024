package fmi.shopping

import cats.syntax.all.*
import doobie.*
import doobie.implicits.*
import doobie.postgres.implicits.*
import fmi.infrastructure.db.DoobieDatabase.DbTransactor

class OrderDao(dbTransactor: DbTransactor):
  def placeOrder(order: Order): ConnectionIO[Order] =
    val insertOrder = sql"""
      INSERT INTO "order" (id, user_id, placing_timestamp)
      VALUES (${order.orderId}, ${order.user}, ${order.placingTimestamp})
    """

    val insertOrderLine =
      val orderLinesInsert = """
        INSERT INTO order_line(order_id, sku, quantity)
        VALUES(?, ?, ?)
      """

      Update[(OrderId, OrderLine)](orderLinesInsert)
        .updateMany(order.orderLines.map(ol => (order.orderId, ol)))

    (insertOrder.update.run *> insertOrderLine).as(order)
