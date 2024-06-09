val tapirVersion = "1.10.6"

lazy val rootProject = (project in file(".")).settings(
  Seq(
    name := "handsome-clam",
    version := "0.1.0-SNAPSHOT",
    organization := "com.softwaremill",
    scalaVersion := "3.3.3",
    libraryDependencies ++= Seq(
      "com.softwaremill.sttp.tapir" %% "tapir-http4s-server" % tapirVersion,
      "org.http4s" %% "http4s-ember-server" % "0.23.27",
      "com.softwaremill.sttp.tapir" %% "tapir-prometheus-metrics" % tapirVersion,
      "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-bundle" % tapirVersion,
      "com.softwaremill.sttp.tapir" %% "tapir-json-circe" % tapirVersion,
      "ch.qos.logback" % "logback-classic" % "1.5.6",
      "com.softwaremill.sttp.tapir" %% "tapir-sttp-stub-server" % tapirVersion % Test,
      "org.scalatest" %% "scalatest" % "3.2.18" % Test,
      "com.softwaremill.sttp.client3" %% "circe" % "3.9.6" % Test
    )
  )
)
