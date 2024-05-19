package math

trait Monoid[M]:
  extension (a: M) def |+|(b: M): M

  def identity: M

object Monoid:
  def apply[M](using m: Monoid[M]): Monoid[M] = m

  given Monoid[Int] with
    extension (a: Int) def |+|(b: Int): Int = a + b
    def identity: Int = 0

  given Monoid[String] with
    extension (a: String) def |+|(b: String): String = a + b

    def identity: String = ""

  given [A : Monoid]: Monoid[Option[A]] with
    extension (maybeA: Option[A])
      def |+|(maybeB: Option[A]): Option[A] = (maybeA, maybeB) match
        case (Some(a), Some(b)) => Some(a |+| b)
        case (None, None) => identity
        case (_, None) => maybeA
        case (None, _) => maybeB

    def identity: Option[A] = Some(Monoid[A].identity)

  given [A : Monoid, B : Monoid]: Monoid[(A, B)] with
    extension (p1: (A, B))
      def |+|(p2: (A, B)): (A, B) = (p1, p2) match
        case ((a1, b1), (a2, b2)) => (a1 |+| a2, b1 |+| b2)

    def identity: (A, B) = (Monoid[A].identity, Monoid[B].identity)

  given [K, V : Monoid]: Monoid[Map[K, V]] with
    extension (a: Map[K, V])
      def |+|(b: Map[K, V]): Map[K, V] =
        val vIdentity = Monoid[V].identity

        (a.keySet ++ b.keySet).foldLeft(identity): (acc, key) =>
          acc.updated(key, a.getOrElse(key, vIdentity) |+| b.getOrElse(key, vIdentity))

    def identity: Map[K, V] = Map.empty[K, V]
