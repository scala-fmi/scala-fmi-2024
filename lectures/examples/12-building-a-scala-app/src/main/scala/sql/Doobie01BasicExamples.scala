package sql

import cats.syntax.applicative.*
import cats.syntax.apply.*
import doobie.*
import doobie.implicits.*

object Doobie01BasicExamples:
  val fortyTwo = "42f"
  val ex1 = 42.pure[ConnectionIO].map(_ + 1)
  val ex2 = sql"SELECT $fortyTwo".query[String].to[List]

  val randomSelect = sql"SELECT random()".query[Double].unique
  val ex3 = (ex2, randomSelect).tupled
  val ex4 = randomSelect.replicateA(10)
