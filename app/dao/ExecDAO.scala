package dao

import javax.inject.Inject

import models.Exec
import org.joda.time.{DateTime, LocalDate}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent.Future
import com.github.tototoshi.slick.PostgresJodaSupport._


class ExecDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._

  private val Execs = TableQuery[ExecsTable]

  def forTask(taskId: Long/*, pageIndex: Int, pageSize: Int = 10*/)/*: Future[Seq[Exec]]*/ = {
    val execList = Execs
      .sortBy(_.endedAt)
//      .drop(math.abs(pageIndex) * pageSize).take(pageSize)
      .filter(c => c.taskId === taskId)
      .result
    db.run(execList)
  }

  // TODO pagination
  def forDay(day: LocalDate) = {
    val start = day.toDateTimeAtStartOfDay
    val end = day.plusDays(1).toDateTimeAtStartOfDay
    val execList = Execs.filter(c => c.endedAt >= start && c.endedAt < end).sortBy(_.endedAt).result
    db.run(execList)
  }

  def createNew(taskId: Long, count: Int, cost: Double): Future[Exec] = {
    val execToInsert = Exec(taskId = taskId, score = count * cost, count = count, endedAt = new DateTime())
    val insertQuery = Execs returning Execs.map(_.id) into ((execToInsert, id) => execToInsert.copy(id = Some(id)))
    db.run(insertQuery += execToInsert)
  }

  def remove(taskId: Long, execId: Long) = db.run(Execs.filter(c => c.id === execId && c.taskId === taskId).delete).map(_ => ())

  class ExecsTable(tag: Tag) extends Table[Exec](tag, "execs") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def taskId = column[Long]("task_id")

    def score = column[Double]("score")

    def count = column[Int]("count")

    def endedAt = column[DateTime]("ended_at")

    def * = (id.?, taskId, score, count, endedAt) <>((Exec.apply _).tupled, Exec.unapply _)
  }
}
