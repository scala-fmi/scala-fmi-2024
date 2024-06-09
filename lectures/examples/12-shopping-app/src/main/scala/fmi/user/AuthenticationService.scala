package fmi.user.authentication

import cats.data.EitherT
import cats.effect.IO
import fmi.{AuthenticationError, ForbiddenResource, UnauthorizedAccess}
import fmi.user.{UserId, UserRole, UsersDao}
import sttp.tapir.{Endpoint, Schema}
import cats.syntax.all.*
import fmi.infrastructure.TokenSignatureService
import io.circe.Codec
import sttp.model.headers.CookieValueWithMeta
import sttp.tapir.server.PartialServerEndpoint

case class AuthenticatedUser(id: UserId, role: UserRole) derives Codec, Schema

class AuthenticationService(usersDao: UsersDao, tokenSignatureService: TokenSignatureService):
  extension [I, E >: AuthenticationError, O, R](securityEndpoint: Endpoint[String, I, E, O, R])
    def authenticate(maybeRole: Option[UserRole] = None)
      : PartialServerEndpoint[String, AuthenticatedUser, I, E, O, R, IO] =
      securityEndpoint.serverSecurityLogic: loggedUserCookie =>
        (for
          email <- EitherT.fromEither[IO](
            tokenSignatureService.validateAndRetrieve(loggedUserCookie).toRight(UnauthorizedAccess("User must login"))
          )
          userId = UserId(email)
          user <- EitherT(usersDao.retrieveUser(userId).map(_.toRight(UnauthorizedAccess("User must login"))))
          _ <- EitherT.fromEither[IO](
            if maybeRole.forall(_ == user.role) then ().asRight
            else ForbiddenResource(s"User $email does not have rights to access this resource").asLeft
          )
        yield AuthenticatedUser(userId, user.role)).value

    def authenticateAdmin: PartialServerEndpoint[String, AuthenticatedUser, I, E, O, R, IO] =
      securityEndpoint.authenticate(Some(UserRole.Admin))

  def sessionWithUser(userId: UserId): IO[CookieValueWithMeta] =
    tokenSignatureService.sign(userId.email).map(CookieValueWithMeta.unsafeApply(_, path = Some("/")))

  val clearSession: IO[CookieValueWithMeta] = CookieValueWithMeta.unsafeApply("", path = Some("/")).pure[IO]
