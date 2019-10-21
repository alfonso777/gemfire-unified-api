name := "Gemfire-Unified-API"
version  := "0.1.0"
scalaVersion := "2.11.12"

libraryDependencies ++= Seq(
  "com.gemstone.gemfire" % "gemfire" % "8.2.0",
  "com.ning" % "async-http-client" % "1.7.19",
  "org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.2",
  "org.scalatest" %% "scalatest" % "3.0.0" % "test",
  "com.typesafe" % "config" % "1.2.1"
)

resolvers ++= Seq(
  "SpringSource Repository" at "http://repo.spring.io/plugins-release/",
  Resolver.sonatypeRepo("public")
)
