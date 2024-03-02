name := "hello-world"
version := "0.1"

scalaVersion := "3.4.0"

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.2.18" % Test
)

scalacOptions ++= Seq(
  "-new-syntax",
)

// Добавете това и променете .scalafmt.conf, ако желаете да върнете блоковете чрез къдрави скоби
//scalacOptions ++= Seq(
//  "-no-indent",
//  "-rewrite"
//)
