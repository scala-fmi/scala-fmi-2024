package com.scalafmi

def solveQuadraticEquation(a: Double, b: Double, c: Double): (Double, Double) =
  def squared(n: Double) = n * n

  val discriminantSqrt =
    val discriminant = squared(b) - 4 * a * c
    math.sqrt(discriminant)

  val firstSolution = (-b - discriminantSqrt) / (2 * a)
  val secondSolution = (-b + discriminantSqrt) / (2 * a)

  (firstSolution, secondSolution) // Наредена двойка. Повече за тях по-късно в курса :)
end solveQuadraticEquation

@main
def testQuadratic() =
  val result = solveQuadraticEquation(-10, -2, 4) match
    case (x, y) => s"firstResult: $x, secondResult: $y"

  val result2 =
    val (x, y) = solveQuadraticEquation(-10, -2, 4)
    s"firstResult: $x, secondResult: $y"

  println(result)

def sum(xs: Seq[Int]): Int =
  if xs.isEmpty then 0
  else xs.head + sum(xs.tail)

@main
def testSum() = println:
  sum(1 to 10)

// "(1 + 2) * 3 * ((1 - 2)) - 4
def balanced(e: List[Char]): Boolean =
  def weight(char: Char) = char match
    case '(' => 1
    case ')' => -1
    case _ => 0

  def loop(e: List[Char], balance: Int): Boolean =
    if balance < 0 then false
    else if e.isEmpty then balance == 0
    else loop(e.tail, balance + weight(e.head))

  loop(e, 0)

@main
def balancedTest() = println:
  balanced("(1 + 2) * 3 *(1 - 2)) - 4 (".toList)

// abcd -> abcd, abc, ab, a, bcd, bc, b, cd, c, d
def substrings(str: String): Seq[String] =
  val substringWithDuplications = for
    beginning <- 0 until str.size
    length <- 1 to str.size - beginning
  yield str.drop(beginning).take(length)

  substringWithDuplications.distinct

@main
def substringsTest() = println:
  substrings("ababcd")
