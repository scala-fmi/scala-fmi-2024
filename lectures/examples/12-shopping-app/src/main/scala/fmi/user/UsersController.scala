package fmi.user

import cats.effect.IO
import fmi.user.authentication.{AuthenticatedUser, AuthenticationService}
import org.http4s.dsl.io.*
import org.http4s.{AuthedRoutes, HttpRoutes}
import cats.syntax.all.*
import fmi.UnauthorizedAccess
import sttp.model.headers.CookieValueWithMeta
import sttp.tapir.server.ServerEndpoint

class UsersController(
  usersService: UsersService
)(
  authenticationService: AuthenticationService
):
  import authenticationService.authenticate

  def registerUser = UsersEndpoints.registerUserEndpoint.serverLogic: userRegistrationForm =>
    usersService.registerUser(userRegistrationForm).map(_.void)

  def getAuthenticatedUser = UsersEndpoints.getAuthenticatedUserEndpoint
    .authenticate()
    .serverLogicSuccess(user => _ => user.pure[IO])

  def loginUser = UsersEndpoints.loginUserEndpoint.serverLogic: userLogin =>
    usersService
      .login(userLogin)
      .flatMap:
        case Some(user) => authenticationService.sessionWithUser(user.id).map(_.asRight)
        case None => UnauthorizedAccess("Invalid credentials").asLeft.pure[IO]

  def logoutUser =
    UsersEndpoints.logoutUserEndpoint.serverLogicSuccess(_ => authenticationService.clearSession)

  val endpoints: List[ServerEndpoint[Any, IO]] = List(
    registerUser,
    getAuthenticatedUser,
    loginUser,
    logoutUser
  )
