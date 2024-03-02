package com.scalafmi

def example: Int =
  // with lazy val any order works
  //
  // with just val we will get compile error if something is not right
  // (it won't succeed silently like in imperative programs)

  lazy val z = y * x * x

  lazy val c = 50
  lazy val y = b * 40

  lazy val b = 40

  lazy val x = a + c
  lazy val a = 10

  z
