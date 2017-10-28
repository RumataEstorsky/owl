name := """owl"""

version := "0.1.12"

lazy val root = (project in file(".")).
  enablePlugins(BuildInfoPlugin).
  settings(
    buildInfoKeys := Seq[BuildInfoKey](
      name, version, scalaVersion, sbtVersion,
      "gitRevision" -> "git rev-parse HEAD".!!.trim
    ),
    buildInfoPackage := "core"
  )
  .enablePlugins(PlayScala)

buildInfoOptions += BuildInfoOption.BuildTime


scalaVersion := "2.11.11"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.0" % "test",
  "joda-time" % "joda-time" % "2.9.1",
  "com.github.tototoshi" %% "slick-joda-mapper" % "2.1.0",
  "org.postgresql" % "postgresql" % "9.4.1209.jre7",
  "org.slf4j" % "slf4j-nop" % "1.6.4",
  "com.typesafe.play" %% "play-slick" % "2.0.0",
  "org.flywaydb" %% "flyway-play" % "3.0.1",
  "info.mukel" %% "telegrambot4s" % "3.0.0-SNAPSHOT",
  "com.github.philcali" %% "cronish" % "0.1.3"
)

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"
resolvers += Resolver.sonatypeRepo("snapshots")