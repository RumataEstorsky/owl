package controllers

import core.AppInfo
import play.api.Logger
import play.api.mvc._

class MainController extends Controller {
  val logger = Logger(this.getClass)

  def version = Action {
    Ok(AppInfo.buildInfoJson)
  }

}
