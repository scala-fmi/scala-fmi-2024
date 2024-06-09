package fmi.user

import cats.data.EitherNec
import cats.syntax.either.*
import cats.syntax.parallel.*
import cats.syntax.traverse.*
import io.circe.Codec
import io.circe.derivation.ConfiguredCodec
import sttp.tapir.Schema
import fmi.utils.DerivationConfiguration.given

case class UserRegistrationForm(email: String, password: String, name: String, age: Option[Int]) derives Codec, Schema

sealed trait RegistrationFormError derives ConfiguredCodec, Schema
case class InvalidEmail(email: String) extends RegistrationFormError
case object NameIsEmpty extends RegistrationFormError
case class InvalidAge(age: Int) extends RegistrationFormError

object UserRegistrationForm:
  def validate(userRegistrationForm: UserRegistrationForm): EitherNec[RegistrationFormError, User] =
    (
      validateEmail(userRegistrationForm.email),
      PasswordUtils.hash(userRegistrationForm.password).rightNec,
      UserRole.NormalUser.rightNec,
      validateName(userRegistrationForm.name),
      validateAge(userRegistrationForm.age)
    ).parMapN(User.apply)

  def validateEmail(email: String): EitherNec[RegistrationFormError, UserId] =
    if email.count(_ == '@') == 1 then UserId(email).rightNec
    else InvalidEmail(email).leftNec

  def validateName(name: String): EitherNec[RegistrationFormError, String] =
    if name.nonEmpty then name.rightNec
    else NameIsEmpty.leftNec

  def validateAge(maybeAge: Option[Int]): EitherNec[RegistrationFormError, Option[Int]] = maybeAge.map { age =>
    if age > 0 then age.rightNec
    else InvalidAge(age).leftNec
  }.sequence
