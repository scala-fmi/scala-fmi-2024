name := "library-app-tapir"
version := "0.1"

scalaVersion := "3.4.2"

scalacOptions ++= Seq(
  "-new-syntax",
  "-Xfatal-warnings",
  "-deprecation"
)

libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-core" % "2.7.0",
  "org.typelevel" %% "cats-effect" % "3.3.12",

  "org.http4s" %% "http4s-dsl" % "0.23.26",
  "org.http4s" %% "http4s-ember-server" % "0.23.26",
  "org.http4s" %% "http4s-circe" % "0.23.26",

  "com.softwaremill.sttp.tapir" %% "tapir-http4s-server" % "1.10.6",
  "com.softwaremill.sttp.tapir" %% "tapir-sttp-client" % "1.10.8",
  "com.softwaremill.sttp.tapir" %% "tapir-prometheus-metrics" % "1.10.6",
  "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-bundle" % "1.10.6",
  "com.softwaremill.sttp.tapir" %% "tapir-json-circe" % "1.10.6",

  "com.softwaremill.sttp.client3" %% "core" % "3.9.7",
  "com.softwaremill.sttp.client3" %% "fs2" % "3.9.7",

  "io.circe" %% "circe-core" % "0.14.7",
  "io.circe" %% "circe-generic" % "0.14.7",

  "ch.qos.logback" % "logback-classic" % "1.5.6",
  "org.fusesource.jansi" % "jansi" % "1.18",

  "org.scalatest" %% "scalatest" % "3.2.12" % Test
)
