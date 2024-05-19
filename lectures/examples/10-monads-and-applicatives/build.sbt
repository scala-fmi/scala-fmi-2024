name := "monads-and-applicatives"
version := "0.1"

scalaVersion := "3.4.2"

scalacOptions ++= Seq(
  "-new-syntax",
  "-Xfatal-warnings",
  "-deprecation"
)

libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-core" % "2.10.0"
)
