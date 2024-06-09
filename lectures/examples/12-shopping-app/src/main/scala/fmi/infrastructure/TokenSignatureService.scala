package fmi.infrastructure

import cats.effect.IO
import org.reactormonk.{CryptoBits, PrivateKey}

class TokenSignatureService(privateKey: String):
  private val key = PrivateKey(scala.io.Codec.toUTF8(privateKey))
  private val crypto = CryptoBits(key)

  def sign(str: String): IO[String] = IO.randomUUID.map: randomUUID =>
    crypto.signToken(str, randomUUID.toString.replaceAll("-", ""))

  def validateAndRetrieve(str: String): Option[String] =
    crypto.validateSignedToken(str)
