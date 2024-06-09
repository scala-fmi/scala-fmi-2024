package fmi.inventory

import fmi.{AuthenticationError, ConflictDescription, ResourceNotFound, ShoppingAppEndpoints}
import sttp.model.StatusCode.{Conflict, NotFound}
import sttp.tapir.*
import sttp.tapir.json.circe.*

object InventoryEndpoints:
  import ShoppingAppEndpoints.*

  val productsBaseEndpoint = v1BaseEndpoint.in("products")
  val stockBaseEndpoint = v1BaseEndpoint.in("stock")

  val getProductEndpoint = productsBaseEndpoint
    .in(path[ProductId].name("product-id"))
    .out(jsonBody[Product])
    .errorOut(statusCode(NotFound).and(jsonBody[ResourceNotFound]))
    .get

  val putProductEndpoint =
    productsBaseEndpoint.secure
      .in(jsonBody[Product])
      .post

  val getAllStockEndpoint = stockBaseEndpoint
    .out(jsonBody[List[ProductStock]])
    .get

  val adjustStockEndpoint: Endpoint[String, InventoryAdjustment, AuthenticationError | ConflictDescription, Unit, Any] =
    stockBaseEndpoint.secure
      .in(jsonBody[InventoryAdjustment])
      .errorOutVariantPrepend[AuthenticationError | ConflictDescription](
        oneOfVariant(statusCode(Conflict).and(jsonBody[ConflictDescription]))
      )
      .post
