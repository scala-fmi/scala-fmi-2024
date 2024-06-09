package fmi.shopping

import fmi.{AuthenticationError, ConflictDescription, ResourceNotFound, ShoppingAppEndpoints}
import sttp.model.StatusCode.{Conflict, NotFound}
import sttp.tapir.*
import sttp.tapir.json.circe.*

object ShoppingEndpoints:
  import ShoppingAppEndpoints.*

  val ordersBaseEndpoint = v1BaseEndpoint.in("orders")

  val placeOrderEndpoint = ordersBaseEndpoint.secure
    .in(jsonBody[ShoppingCart])
    .out(jsonBody[Order])
    .errorOutVariantPrepend[AuthenticationError | ConflictDescription](
      oneOfVariant(statusCode(Conflict).and(jsonBody[ConflictDescription]))
    )
