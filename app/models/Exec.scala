package models

import org.joda.time.DateTime
import play.api.libs.json._

case class Exec(id: Option[Long] = None, taskId: Long, score: Double, count: Int, endedAt: DateTime)

object Exec {
  implicit val execFormat = Json.format[Exec]
}

