package math

trait Semigroup[M] {
  def op(a: M, b: M): M
}

object Semigroup {
  def apply[A](implicit m: Semigroup[A]): Semigroup[A] = m

  object syntax {
    implicit class SemigroupSyntax[A](val a: A)(implicit m: Semigroup[A]) {
      def |+|(b: A): A = m.op(a, b)
    }
  }
}
