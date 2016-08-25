name := """owl"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.0-RC1" % Test,
  "joda-time" % "joda-time" % "2.9.1",
  "com.github.tototoshi" %% "slick-joda-mapper" % "2.1.0",
  "org.postgresql" % "postgresql" % "9.4.1209.jre7",
  "org.slf4j" % "slf4j-nop" % "1.6.4",
  "com.typesafe.play" %% "play-slick" % "2.0.0"
)

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"
