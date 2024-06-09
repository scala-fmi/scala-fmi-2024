package fmi

import fmi.shopping.{Order, ShoppingCart}
import fmi.user.authentication.AuthenticatedUser
import fmi.user.{RegistrationFormError, UserLogin, UserRegistrationForm}
import sttp.model.StatusCode.{BadRequest, Conflict, Forbidden, Unauthorized}
import sttp.tapir.*
import sttp.tapir.json.circe.*
import io.circe.Codec

sealed trait HttpError
sealed trait AuthenticationError extends HttpError
case class UnauthorizedAccess(message: String) extends AuthenticationError derives Codec, Schema
case class ForbiddenResource(message: String) extends AuthenticationError derives Codec, Schema
case class ResourceNotFound(message: String) extends HttpError derives Codec, Schema
case class BadRequestDescription[E](error: E) extends HttpError
case class ConflictDescription(message: String) extends HttpError derives Codec, Schema

object BadRequestDescription:
  given [E : Codec]: Codec[BadRequestDescription[E]] = Codec.derived
  given [E : Schema]: Schema[BadRequestDescription[E]] = Schema.derived

object ShoppingAppEndpoints:
  val baseEndpoint: PublicEndpoint[Unit, Unit, Unit, Any] = endpoint
  val v1BaseEndpoint: Endpoint[Unit, Unit, Unit, Unit, Any] = baseEndpoint.in("v1")

  extension [I, O, R](endpoint: PublicEndpoint[I, Unit, O, R])
    def secure: Endpoint[String, I, AuthenticationError, O, R] =
      endpoint
        .securityIn(cookie[String]("loggedUser"))
        .errorOut(
          oneOf[AuthenticationError](
            oneOfVariant(statusCode(Unauthorized).and(jsonBody[UnauthorizedAccess].description("unauthorized access"))),
            oneOfVariant(statusCode(Forbidden).and(jsonBody[ForbiddenResource].description("resource forbidden")))
          )
        )
