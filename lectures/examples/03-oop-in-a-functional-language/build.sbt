name := "oop-in-a-functional-language"
version := "0.1"

scalaVersion := "3.4.0"

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.2.18" % Test
)

scalacOptions ++= Seq(
  "-new-syntax"
)
