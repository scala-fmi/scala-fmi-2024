package fmi.user

import fmi.user.authentication.AuthenticatedUser
import fmi.{ShoppingAppEndpoints, UnauthorizedAccess}
import sttp.model.StatusCode.{BadRequest, Unauthorized}
import sttp.tapir.*
import sttp.tapir.json.circe.*

object UsersEndpoints:
  import ShoppingAppEndpoints.*

  val usersBaseEndpoint = v1BaseEndpoint.in("users")

  val registerUserEndpoint = usersBaseEndpoint
    .in(jsonBody[UserRegistrationForm])
    .errorOut(
      statusCode(BadRequest).and(jsonBody[RegistrationError])
    )
    .post

  val getAuthenticatedUserEndpoint = usersBaseEndpoint.secure
    .in("current")
    .out(jsonBody[AuthenticatedUser])
    .get

  val loginUserEndpoint = usersBaseEndpoint
    .in("login")
    .in(jsonBody[UserLogin])
    .out(setCookie("loggedUser"))
    .errorOut(
      statusCode(Unauthorized).and(jsonBody[UnauthorizedAccess])
    )
    .post

  val logoutUserEndpoint = usersBaseEndpoint
    .in("logout")
    .out(setCookie("loggedUser"))
    .post
