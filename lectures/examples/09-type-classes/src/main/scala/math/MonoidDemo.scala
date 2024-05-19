package math

def sum[A : Monoid](as: List[A]): A =
  as.fold(Monoid[A].identity)(_ |+| _)

@main def runMonoidDemo(): Unit =
  println:
    sum(List(1, 3, 4))
  println:
    sum(List("a", "b", "c"))

  println:
    sum(List(Rational(1, 2)))

  println:
    sum(List.empty[Rational])

  {
    given Monoid[Rational] = Rational.multiplicationRationalMonoid

    println:
      sum(List(Rational(1, 2), Rational(3, 4)))
  }

//  List("1", "2", "3").sum
  
  import Monoid.given
  1 |+| 2
  "1" |+| "2"
  Rational(1, 2) |+| Rational(3, 4)

  println:
    sum(
      List(
        Some(Rational(1)),
        None,
        Some(Rational(1, 2)),
        Some(Rational(3, 8)),
        None
      )
    ) // Some(15/8)

  // Pairs
  (2, 3) |+| (4, 5) // (6, 8)

  val map1 = Map(1 -> (2, Rational(3, 2)), 2 -> (3, Rational(4)))
  val map2 = Map(2 -> (5, Rational(6)), 3 -> (7, Rational(8, 3)))

  sum(List(map1, map2))

  // Composes Monoid[Int] and Monoid[Rational] into a pair monoid Monoid[(Int, Rational)]
  // and then composes the pair monoid into Monoid[Map[Int, (Int, Rational)]
  println(map1 |+| map2)
