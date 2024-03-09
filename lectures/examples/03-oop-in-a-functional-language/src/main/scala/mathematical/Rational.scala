package mathematical

import scala.language.implicitConversions

trait Ordered[A]:
  def compare(that: A): Int

  def <(that: A): Boolean = compare(that) < 0
  def <=(that: A): Boolean = compare(that) <= 0
  def >(that: A): Boolean = compare(that) > 0
  def >=(that: A): Boolean = compare(that) >= 0

class Rational(n: Int, d: Int) extends Ordered[Rational]:
  require(d != 0, "Denominator must not be zero")

  // We can put it in some math utility
  private def gcd(a: Int, b: Int): Int =
    def nonNegativeGcd(a: Int, b: Int): Int =
      if b == 0 then a else nonNegativeGcd(b, a % b)

    nonNegativeGcd(a.abs, b.abs)

  def compare(that: Rational): Int = (this - that).numer

  val (numer, denom) =
    val div = gcd(n, d)
    ((n / div) * d.sign, (d / div).abs)

  def unary_- = Rational(-numer, denom)

  def unary_~ = Rational(denom, numer)

  def +(that: Rational): Rational = Rational(
    numer * that.denom + that.numer * denom,
    denom * that.denom
  )

  def -(that: Rational) = this + (-that)

  def *(that: Rational) = Rational(numer * that.numer, denom * that.denom)

  def /(that: Rational) = this * (~that)

  override def hashCode(): Int = (numer, denom).##

  override def equals(obj: Any): Boolean = obj match
    case that: Rational => numer == that.numer && denom == that.denom
    case _ => false

  override def toString: String = s"$numer/$denom"

object Rational:
  val Zero = Rational(0) // използва apply, дефиниран долу

  def apply(n: Int, d: Int = 1) = new Rational(n, d)

  implicit def intToRational(n: Int): Rational = Rational(n)

  def sum(rationals: Rational*): Rational =
    if rationals.isEmpty then Zero
    else rationals.head + sum(rationals.tail*)

  extension (xs: List[Rational])
    def total: Rational =
      if xs.isEmpty then 0
      else xs.head + xs.tail.total

    def avg: Rational = xs.total / xs.size

@main def testRational = println:
  Rational.Zero + 1 + Rational(1, 2)

  Rational(2, -4) + 1
  1 + Rational(2, -4)

  Rational(2, -4) <= Rational(2, 5)
