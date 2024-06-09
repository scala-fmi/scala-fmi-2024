package fmi.inventory

import cats.effect.IO
import cats.effect.kernel.Resource
import fmi.infrastructure.db.DoobieDatabase.DbTransactor
import fmi.user.authentication.{AuthenticatedUser, AuthenticationService}
import org.http4s.{AuthedRoutes, HttpRoutes}
import sttp.tapir.server.ServerEndpoint

case class InventoryModule(
  productDao: ProductDao,
  productStockDao: ProductStockDao,
  endpoints: List[ServerEndpoint[Any, IO]]
)

object InventoryModule:
  def apply(dbTransactor: DbTransactor, authenticationService: AuthenticationService): Resource[IO, InventoryModule] =
    val productDao = new ProductDao(dbTransactor)
    val productStockDao = new ProductStockDao(dbTransactor)
    val inventoryController = new InventoryController(productDao, productStockDao)(authenticationService)

    Resource.pure(
      InventoryModule(
        productDao,
        productStockDao,
        inventoryController.endpoints
      )
    )
