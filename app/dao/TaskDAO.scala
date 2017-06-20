package dao

import javax.inject.Inject

import models.{DaysProductivityView, Task, TaskStatView}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import slick.driver.JdbcProfile

import scala.concurrent.Future

class TaskDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._

  private val Tasks = TableQuery[TasksTable]

  def activeList(): Future[Seq[Task]] = db.run(Tasks.filter(_.isFrozen === false).sortBy(_.name).result)

  def activeTaskStatView(): Future[Seq[TaskStatView]] = {
    val q = sql"""SELECT * FROM tasks_stat ORDER BY ago DESC""".as[TaskStatView]
    db.run(q)
  }

  def setFreeze(taskId: Long, isFreeze: Boolean): Future[Unit] = {
    val q = for { t <- Tasks if t.id === taskId } yield t.isFrozen
    db.run(q.update(isFreeze)).map { _ => () }
  }

//  def page(pageIndex: Int, pageSize: Int = 10): Future[Seq[Task]] =
//    db.run(Tasks.sortBy(_.name).drop(math.abs(pageIndex) * pageSize).take(pageSize).result)

  def findById(taskId: Long) = db.run(Tasks.filter(t => t.id === taskId).result.headOption)

  // NULL without records!!
  def getScore(date: String) = {
    val q = sql"""SELECT SUM(score) FROM execs WHERE date_trunc('day', ended_at) = DATE '#$date'""".as[Int].head
    db.run(q)
  }

  def maxScoreDay = db.run(sql"SELECT MAX(total_score) FROM days_productivity".as[Int].head)

  def remainsScoreToday = {
    val q = sql"SELECT (select MAX(total_score) from days_productivity) - (select total_score from days_productivity WHERE day = DATE 'today') AS remains".as[Int].head
    db.run(q)
  }

  // TODO remove plain sql where it possible
  def annualStatistics = {
    val q = sql"SELECT * FROM days_productivity WHERE day > (now() - INTERVAL '1 year') ORDER BY day".as[DaysProductivityView]
    db.run(q)
  }

  def annualStatisticsByTask(taskId: Long) = {
    val q =
      sql"""SELECT * FROM (
              SELECT date_trunc('day', ended_at)::date as day, sum(score) AS total_score, sum(count) AS exec_count, null
              FROM execs
              GROUP BY task_id, day
              HAVING task_id = #$taskId
              ORDER BY day) a WHERE  day > (now() - INTERVAL '1 year')
            ORDER BY day""".as[DaysProductivityView]
    db.run(q)
  }


//  def insert(task: Task): Future[Task] = {
//    val insertQuery = Tasks returning Tasks.map(_.id) into ((task, id) => task.copy(id = Some(id)))
//    db.run(insertQuery += task)
//  }

//  def update(task: Task): Future[Unit] = db.run(Tasks.filter(p => p.id === task.id).update(task)).map { _ => () }

//  def remove(taskId: Long) = db.run(Tasks.filter(p => p.id === taskId).delete).map(_ => ())


  class TasksTable(tag: Tag) extends Table[Task](tag, "tasks") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def name = column[String]("name")

    def isFrozen = column[Boolean]("is_frozen")

    def cost = column[Double]("cost")

    def * = (id.?, name, isFrozen, cost) <>((Task.apply _).tupled, Task.unapply _)
  }

}

