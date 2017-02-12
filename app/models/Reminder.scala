package models

import cronish.Cron
import scalendar.Scalendar

case class Reminder(cron: Cron, message: String, task: Option[Task]) {
  def remains = cron.nextFrom(Scalendar.now)
}
