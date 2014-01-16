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
    ),

    // Publishing stuff
    publishMavenStyle := true,
    publishTo := {
      val nexus = "https://oss.sonatype.org/"
      if (isSnapshot.value)
        Some("snapshots" at nexus + "content/repositories/snapshots")
      else
        Some("releases"  at nexus + "service/local/staging/deploy/maven2")
    },
    pomIncludeRepository := { _ => false },
    pomExtra := (
      <url>http://github.com/klasu/simple-dynamodb</url>
        <licenses>
          <license>
            <name>MIT License</name>
            <url>http://opensource.org/licenses/MIT</url>
            <distribution>repo</distribution>
          </license>
        </licenses>
        <scm>
          <url>git@github.com:klasu/simple-dynamodb.git</url>
          <connection>scm:git:git@github.com:klasu/simple-dynamodb.git</connection>
        </scm>
        <developers>
          <developer>
            <id>klasu</id>
            <name>Klaus Ihlberg</name>
            <url>http://github.com/klasu</url>
          </developer>
        </developers>)
  )
}
