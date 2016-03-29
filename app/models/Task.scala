package models

import play.api.libs.json._

case class Task(id: Option[Long] = None, name: String, isFrozen: Boolean, cost: Double)

object Task {
  implicit val taskFormat: Format[Task] = Json.format[Task]
}
