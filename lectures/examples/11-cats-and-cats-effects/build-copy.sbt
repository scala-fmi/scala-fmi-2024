name := "cats-and-cats-effects"
version := "0.1"

scalaVersion := "3.4.1"

scalacOptions ++= Seq(
  "-new-syntax",
  "-Xfatal-warnings",
  "-deprecation"
)

libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-core" % "2.10.0",
  "org.typelevel" %% "cats-effect" % "3.5.4",
  "org.scalatest" %% "scalatest" % "3.2.18" % Test,
  "org.typelevel" %% "cats-laws" % "2.10.0" % Test,
  "org.typelevel" %% "discipline-scalatest" % "2.2.0" % Test,
  "org.typelevel" %% "cats-effect-testing-scalatest" % "1.4.0" % Test
)
