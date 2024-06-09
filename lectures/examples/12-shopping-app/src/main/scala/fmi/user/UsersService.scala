package fmi.user

import cats.data.{EitherT, NonEmptyChain}
import cats.effect.IO
import cats.syntax.all.*
import fmi.utils.DerivationConfiguration.given
import io.circe.Codec
import io.circe.derivation.ConfiguredCodec
import sttp.tapir.Schema
import sttp.tapir.integ.cats.codec.*

class UsersService(usersDao: UsersDao):
  def registerUser(registrationForm: UserRegistrationForm): IO[Either[RegistrationError, User]] = (for
    user <- EitherT.fromEither[IO](
      UserRegistrationForm
        .validate(registrationForm)
        .leftMap(UserValidationError.apply)
    )
    _ <- EitherT(usersDao.registerUser(user))
  yield user).value

  def login(userLogin: UserLogin): IO[Option[User]] =
    usersDao.retrieveUser(userLogin.id).map {
      case Some(user) =>
        if PasswordUtils.checkPasswords(userLogin.password, user.passwordHash) then Some(user)
        else None
      case _ => None
    }

sealed trait RegistrationError derives ConfiguredCodec, Schema
case class UserValidationError(registrationErrors: NonEmptyChain[RegistrationFormError]) extends RegistrationError
case class UserAlreadyExists(email: UserId) extends RegistrationError

case class UserLogin(id: UserId, password: String) derives Codec, Schema
