package models

import play.api.libs.json.{Format, Json}
import slick.jdbc.GetResult

case class DaysProductivityView(day: String, totalScore: Double, execCount: Int, typesTasksCount: Int)

object  DaysProductivityView {
  implicit val getDaysProductivityViewResult = GetResult(r => DaysProductivityView(r.<<, r.<<, r.<<, r.<<))
  implicit val daysProductivityView: Format[DaysProductivityView] = Json.format[DaysProductivityView]
}

case class TaskStatView(id: Long, name: String, totalScore: Double, totalEl: Int, times: Int, avgTimes: Double, daysAgo: Int)
object  TaskStatView {
  implicit val getTaskStatViewResult = GetResult(r => TaskStatView(r.<<, r.<<, r.<<, r.<<, r.<<, r.<<, r.<<))
  implicit val taskStatView: Format[TaskStatView] = Json.format[TaskStatView]
}


case class MyDailyAchievementsView(id: Long, name: String, total: Int, day: String)
object  MyDailyAchievementsView {
  implicit val getTaskStatViewResult = GetResult(r => MyDailyAchievementsView(r.<<, r.<<, r.<<, r.<<))
  implicit val taskStatView: Format[MyDailyAchievementsView] = Json.format[MyDailyAchievementsView]
}