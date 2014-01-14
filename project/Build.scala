import sbt._
import sbt.Keys._

object ApplicationBuild extends Build {

  val appName         = "simple-dynamodb"

  val main = Project("simple-dynamodb", file("."), settings = Defaults.defaultSettings).settings(
    name := appName,
    version := "0.1-SNAPSHOT",
    organization := "com.github.klasu",
    scalaVersion := "2.10.1",
    libraryDependencies ++= Seq(
      "com.amazonaws" % "aws-java-sdk" % "1.6.7"
    )
  )
}
