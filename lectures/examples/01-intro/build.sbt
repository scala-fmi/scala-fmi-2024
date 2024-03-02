name := "scala-examples"
version := "0.1"

scalaVersion := "3.4.0"

libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-core" % "2.10.0",
  "org.typelevel" %% "cats-effect" % "3.5.3",
  "co.fs2" %% "fs2-core" % "3.9.4",
  "org.http4s" %% "http4s-dsl" % "0.23.25",
  "org.http4s" %% "http4s-ember-client" % "0.23.11",
  "org.http4s" %% "http4s-ember-server" % "0.23.11",
  "ch.qos.logback" % "logback-classic" % "1.2.12"
)
