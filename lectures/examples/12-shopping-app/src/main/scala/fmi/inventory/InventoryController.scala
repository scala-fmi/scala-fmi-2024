package fmi.inventory
import cats.effect.IO
import cats.syntax.all.*
import fmi.user.authentication.AuthenticationService
import fmi.{ConflictDescription, ResourceNotFound}
import sttp.tapir.server.ServerEndpoint

class InventoryController(
  productDao: ProductDao,
  productStockDao: ProductStockDao
)(
  authenticationService: AuthenticationService
):
  import authenticationService.authenticateAdmin

  val getProduct = InventoryEndpoints.getProductEndpoint.serverLogic: productId =>
    productDao
      .retrieveProduct(productId)
      .map(_.toRight(ResourceNotFound(s"Product $productId was not found")))

  val putProduct = InventoryEndpoints.putProductEndpoint.authenticateAdmin
    .serverLogicSuccess(user => productDao.addProduct)

  val getAllStock = InventoryEndpoints.getAllStockEndpoint.serverLogicSuccess: _ =>
    productStockDao.retrieveAllAvailableStock

  val adjustStock = InventoryEndpoints.adjustStockEndpoint.authenticateAdmin
    .serverLogic { user => adjustment =>
      productStockDao
        .applyInventoryAdjustment(adjustment)
        .map:
          case SuccessfulAdjustment => ().asRight
          case NotEnoughStockAvailable => ConflictDescription("Not enough stock available").asLeft
    }

  val endpoints: List[ServerEndpoint[Any, IO]] = List(
    getProduct,
    putProduct,
    getAllStock,
    adjustStock
  )
