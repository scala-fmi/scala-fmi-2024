name := "library-app"
version := "0.1"

scalaVersion := "3.4.2"

libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-core" % "2.7.0",
  "org.typelevel" %% "cats-effect" % "3.3.12",

  "org.http4s" %% "http4s-dsl" % "0.23.26",
  "org.http4s" %% "http4s-ember-server" % "0.23.26",
  "org.http4s" %% "http4s-ember-client" % "0.23.26",
  "org.http4s" %% "http4s-circe" % "0.23.26",
//  "org.http4s" %% "http4s-dsl" % "0.23.11",
//  "org.http4s" %% "http4s-blaze-server" % "0.23.11",
//  "org.http4s" %% "http4s-blaze-client" % "0.23.11",
//  "org.http4s" %% "http4s-circe" % "0.23.11",

  "io.circe" %% "circe-core" % "0.14.7",
  "io.circe" %% "circe-generic" % "0.14.7",

  "ch.qos.logback" % "logback-classic" % "1.5.6",
  "org.fusesource.jansi" % "jansi" % "1.18",

  "org.scalatest" %% "scalatest" % "3.2.12" % Test
)
