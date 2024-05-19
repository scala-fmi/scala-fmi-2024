import scala.collection.Seq

name := "type-classes"
version := "0.1"

scalaVersion := "3.4.1"

scalacOptions ++= Seq(
  "-new-syntax",
  "-Xfatal-warnings",
  "-deprecation"
)

libraryDependencies ++= Seq(
  "org.scala-lang.modules" %% "scala-parallel-collections" % "1.0.4",
  "org.typelevel" %% "cats-core" % "2.10.0",
  "org.typelevel" %% "spire" % "0.18.0",
  "org.scalatest" %% "scalatest" % "3.2.18" % Test
)
