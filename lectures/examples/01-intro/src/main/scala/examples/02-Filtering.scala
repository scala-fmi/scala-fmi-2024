package examples

case class Competitor(name: String, age: Int, score: Int)

def findTop100AdultsScoreSum(competitors: List[Competitor]): Int =
  competitors
    .filter(_.age >= 18)
    .map(_.score)
    .sorted(Ordering[Int].reverse)
    .take(100)
    .sum