package fmi.user

import cats.effect.IO
import cats.effect.kernel.Resource
import fmi.infrastructure.TokenSignatureService
import fmi.infrastructure.db.DoobieDatabase.DbTransactor
import fmi.user.authentication.{AuthenticatedUser, AuthenticationService}
import org.http4s.server.AuthMiddleware
import org.http4s.{AuthedRoutes, HttpRoutes}
import sttp.tapir.server.ServerEndpoint

case class UsersModule(
  usersDao: UsersDao,
  usersService: UsersService,
  authenticationService: AuthenticationService,
  endpoints: List[ServerEndpoint[Any, IO]]
)

object UsersModule:
  def apply(dbTransactor: DbTransactor, tokenSignatureService: TokenSignatureService): Resource[IO, UsersModule] =
    val usersDao = new UsersDao(dbTransactor)
    val usersService = new UsersService(usersDao)
    val authenticationService = new AuthenticationService(usersDao, tokenSignatureService)
    val usersController = new UsersController(usersService)(authenticationService)

    Resource.pure(
      UsersModule(
        usersDao,
        usersService,
        authenticationService,
        usersController.endpoints
      )
    )
