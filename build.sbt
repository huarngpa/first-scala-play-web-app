name := """scala-web-project"""
version := "1.0-SNAPSHOT"
scalaVersion := "2.13.1"

lazy val root = (project in file(".")).enablePlugins(PlayScala)
pipelineStages := Seq(digest)

libraryDependencies ++= Seq(
  jdbc,
  caffeine,
  ws,
  "com.softwaremill.macwire" %% "macros" % "2.3.3" % "provided",
  "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % "test",
  "org.mockito" % "mockito-core" % "2.7.22" % "test",
  "org.postgresql" % "postgresql" % "42.2.9",
  "com.typesafe.play" %% "play-slick" % "5.0.0",
  "com.typesafe.play" %% "play-slick-evolutions" % "5.0.0",
  "de.svenkubiak" % "jBCrypt" % "0.4.1"
)

resolvers += "sonatype-snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
