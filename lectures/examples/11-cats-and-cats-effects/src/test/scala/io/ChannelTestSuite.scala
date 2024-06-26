package io

import cats.effect.*
import cats.effect.testing.scalatest.AsyncIOSpec
import cats.implicits.*
import org.scalatest.freespec.AsyncFreeSpec
import org.scalatest.matchers.should.Matchers

import scala.concurrent.TimeoutException
import scala.concurrent.duration.DurationInt

class ChannelTestSuite extends AsyncFreeSpec with AsyncIOSpec with Matchers:
  "Single put take passes" in {
    val result = for
      chan <- Channel[Int]()
      _ <- chan.put(1)
      v <- chan.take
    yield v

    result.asserting(_ shouldBe 1)
  }

  "Test appending of multiple values" in {
    val result = for
      chan <- Channel[Int]()
      _ <- chan.append(List.range(0, 25))
      _ <- chan.take.replicateA(15)
      len <- chan.length
    yield len

    result.asserting(_ shouldBe 10)
  }

  "We gonna block dawg" in {
    val result = for
      chan <- Channel[Int]()
      _ <- chan.append(List.range(0, 5))
      maybeError <- chan.take
        .replicateA(6)
        .timeout(500.millis)
    yield maybeError

    result.assertThrows[TimeoutException]
  }
