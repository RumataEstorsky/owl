package core

import play.api.libs.json.Json

object AppInfo {
  val buildInfoJson = Json.obj(
    "project.artifactId" -> BuildInfo.name,
    "project.version" -> BuildInfo.version,
    "project.name" -> BuildInfo.name,
    "build.time" -> BuildInfo.builtAtString,
    "git.revision" -> BuildInfo.gitRevision,
    "project.scalaVersion" -> BuildInfo.scalaVersion
  )
}