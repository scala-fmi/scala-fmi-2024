package math

trait Monoid[M] extends Semigroup[M] {
  def op(a: M, b: M): M // no need to repeat this one from Semigroup, it's here just for completeness
  def identity: M
}

object Monoid {
  def apply[A](implicit m: Monoid[A]): Monoid[A] = m

  object syntax {
    implicit class MonoidSyntax[A](val a: A)(implicit m: Monoid[A]) {
      def |+|(b: A): A = m.op(a, b)
    }
  }

  implicit val intAdditiveMonoid: Monoid[Int] = new Monoid[Int] {
    def op(a: Int, b: Int): Int = a + b
    val identity: Int = 0
  }

  val intMultiplicativeMonoid: Monoid[Int] = new Monoid[Int] {
    def op(a: Int, b: Int): Int = a * b
    val identity: Int = 1
  }

  implicit val stringMonoid: Monoid[String] = new Monoid[String] {
    def op(a: String, b: String): String = a + b
    val identity: String = ""
  }

  implicit def optionMonoid[A : Monoid]: Monoid[Option[A]] = new Monoid[Option[A]] {
    import syntax._

    def op(a:  Option[A], b:  Option[A]): Option[A] = (a, b) match {
      case (Some(n), Some(m)) => Some(n |+| m)
      case (Some(_), _) => a
      case (_, Some(_)) => b
      case _ => None
    }

    def identity: Option[A] = None
  }

  implicit def pairMonoid[A : Monoid, B : Monoid]: Monoid[(A, B)] = new Monoid[(A, B)] {
    import syntax._

    def op(a: (A, B), b: (A, B)): (A, B) = (a, b) match {
      case ((a1, a2), (b1, b2)) => (a1 |+| b1, a2 |+| b2)
    }

    def identity: (A, B) = (Monoid[A].identity, Monoid[B].identity)
  }

  implicit def mapMonoid[K, V : Monoid]: Monoid[Map[K, V]] = new Monoid[Map[K, V]] {
    import syntax._

    def op(a: Map[K, V], b: Map[K, V]): Map[K, V] = {
      val vIdentity = Monoid[V].identity

      (a.keySet ++ b.keySet).foldLeft(identity) { (acc, key) =>
        acc + (key -> (a.getOrElse(key, vIdentity) |+| b.getOrElse(key, vIdentity)))
      }
    }

    def identity: Map[K, V] = Map.empty[K, V]
  }
}
