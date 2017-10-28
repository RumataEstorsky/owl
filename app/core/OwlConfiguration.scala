package core

import java.io.File
import javax.inject.{Inject, Singleton}

import play.api.{Configuration, Logger}

@Singleton
class OwlConfiguration @Inject()(conf: Configuration) {
  val logger = Logger(this.getClass)
  val workspaceDir = new File(conf.getString("workspace.dir").getOrElse("."))
  def inWorkspace(path: String) = new File(workspaceDir + File.separator + path)

}
