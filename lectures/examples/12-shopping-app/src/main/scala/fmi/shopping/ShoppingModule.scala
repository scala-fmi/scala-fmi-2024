package fmi.shopping

import cats.effect.IO
import cats.effect.kernel.Resource
import fmi.infrastructure.db.DoobieDatabase.DbTransactor
import fmi.inventory.ProductStockDao
import fmi.user.authentication.{AuthenticatedUser, AuthenticationService}
import org.http4s.AuthedRoutes
import sttp.tapir.server.ServerEndpoint

case class ShoppingModule(
  orderDao: OrderDao,
  orderService: OrderService,
  endpoints: List[ServerEndpoint[Any, IO]]
)

object ShoppingModule:
  def apply(
    dbTransactor: DbTransactor,
    authenticationService: AuthenticationService,
    productStockDao: ProductStockDao
  ): Resource[IO, ShoppingModule] =
    val orderDao = new OrderDao(dbTransactor)
    val orderService = new OrderService(dbTransactor)(productStockDao, orderDao)
    val shippingController = new ShoppingController(orderService)(authenticationService)

    Resource.pure(
      ShoppingModule(orderDao, orderService, shippingController.endpoints)
    )
