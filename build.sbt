name := """scala-web-project"""
version := "1.0-SNAPSHOT"
scalaVersion := "2.12.8"

lazy val root = (project in file(".")).enablePlugins(PlayScala)
pipelineStages := Seq(digest)

libraryDependencies ++= Seq(
  jdbc,
  caffeine,
  ws,
  "com.softwaremill.macwire" %% "macros" % "2.3.0" % "provided",
  "org.scalatestplus.play" %% "scalatestplus-play" % "4.0.1" % "test",
  "org.mockito" % "mockito-core" % "2.7.22" % "test"
)

resolvers += "sonatype-snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
