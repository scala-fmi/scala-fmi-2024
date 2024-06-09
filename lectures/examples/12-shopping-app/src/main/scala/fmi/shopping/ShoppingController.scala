package fmi.shopping

import cats.effect.IO
import cats.syntax.all.*
import fmi.ConflictDescription
import fmi.inventory.ProductId
import fmi.user.UserId
import fmi.user.authentication.{AuthenticatedUser, AuthenticationService}
import fmi.utils.CirceUtils
import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec
import org.http4s.AuthedRoutes
import org.http4s.dsl.io.*
import sttp.tapir.server.ServerEndpoint

class ShoppingController(orderService: OrderService)(authenticationService: AuthenticationService):
  import authenticationService.authenticate

  val placeOrders = ShoppingEndpoints.placeOrderEndpoint
    .authenticate()
    .serverLogic { user => shoppingCart =>
      orderService
        .placeOrder(user.id, shoppingCart)
        .map(_.leftMap(_ => ConflictDescription("Not enough stock available")))
    }

  val endpoints: List[ServerEndpoint[Any, IO]] = List(placeOrders)
