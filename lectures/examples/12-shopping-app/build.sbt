name := "shopping-app"
version := "0.1"

scalaVersion := "3.4.2"

scalacOptions ++= Seq(
  "-new-syntax",
  "-Xfatal-warnings",
  "-deprecation"
)

libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-core" % "2.12.0",
  "org.typelevel" %% "cats-effect" % "3.5.4",

  "org.http4s" %% "http4s-dsl" % "0.23.26",
  "org.http4s" %% "http4s-ember-server" % "0.23.26",
  "org.http4s" %% "http4s-circe" % "0.23.26",

  "com.softwaremill.sttp.tapir" %% "tapir-http4s-server" % "1.10.8",
  "com.softwaremill.sttp.tapir" %% "tapir-http4s-client" % "1.10.8",
  "com.softwaremill.sttp.tapir" %% "tapir-sttp-client" % "1.10.8",
  "com.softwaremill.sttp.tapir" %% "tapir-prometheus-metrics" % "1.10.8",
  "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-bundle" % "1.10.8",
  "com.softwaremill.sttp.tapir" %% "tapir-json-circe" % "1.10.8",
  "com.softwaremill.sttp.tapir" %% "tapir-cats" % "1.10.8",

  "org.flywaydb" % "flyway-core" % "8.5.12",

  "org.tpolecat" %% "doobie-core" % "1.0.0-RC5",
  "org.tpolecat" %% "doobie-hikari" % "1.0.0-RC5",
  "org.tpolecat" %% "doobie-postgres" % "1.0.0-RC5",

  "com.typesafe" % "config" % "1.4.3",

  "io.circe" %% "circe-core" % "0.14.7",
  "io.circe" %% "circe-generic" % "0.14.7",

  "org.mindrot" % "jbcrypt" % "0.4",

  ("org.reactormonk" %% "cryptobits" % "1.3").cross(CrossVersion.for3Use2_13),

  "ch.qos.logback" % "logback-classic" % "1.5.6",
  "org.fusesource.jansi" % "jansi" % "1.18",

  "org.scalatest" %% "scalatest" % "3.2.12" % Test,
  "org.typelevel" %% "cats-laws" % "2.12.0" % Test,
  "org.typelevel" %% "discipline-scalatest" % "2.3.0" % Test,
  "org.typelevel" %% "cats-effect-testing-scalatest" % "1.5.0" % Test
)

fork := true
