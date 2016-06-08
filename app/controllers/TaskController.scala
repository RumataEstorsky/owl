package controllers

import javax.inject.Inject

import org.joda.time.{DateTime, LocalDate}
import dao.{ExecDAO, TaskDAO}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.Json.toJson
import play.api.libs.json._
import play.api.mvc.{Action, Controller}

import scala.concurrent.Future

// TODO add module and integration tests
class TaskController @Inject()(taskDao: TaskDAO, execDao: ExecDAO) extends Controller {
  val InvalidJsonFuture = Future.successful(BadRequest(Json.obj("result" -> "Invalid JSON")))

  private def ItemNotFoundFuture(id: Long) = Future.successful(NotFound(Json.obj("result" -> "error", "message" -> JsString("Not found post with ID = " + id))))

  def getTasks() = Action.async {
    taskDao.activeList().map { tasks => Ok(toJson(tasks)) }
  }

  def freeze(id: Long) = Action.async {
    taskDao.setFreeze(id, true).map(_ => Accepted(id.toString))
  }

  def unfreeze(id: Long) = Action.async {
    taskDao.setFreeze(id, false).map(_ => Accepted(id.toString))
  }

  def addExec(taskId: Long, count: Int) = Action.async {
    taskDao.findById(taskId).flatMap { maybeTask =>
      maybeTask.fold {
        ItemNotFoundFuture(taskId)
      } { task =>
        execDao.createNew(taskId, count, task.cost).map(inserted => Created(toJson(inserted)))
      }
    }
  }

  def getScore(when: String) = Action.async {
    val res = when match {
      case const if Array("today", "yesterday").contains(const) => taskDao.getScore(const)
      case "max" => taskDao.maxScoreDay
      case "remains" => taskDao.remainsScoreToday
      // TODO make protection again sql-injection
      case date: String => taskDao.getScore(date)
    }
    res.map(e => Ok(e.toString))
  }

  // TODO make pagination
  def statisticsDays() = Action.async {
    taskDao.statisticsByDays.map(items => Ok(toJson(items)))
  }

  def statisticsDay(day: String) = Action.async {
    execDao.forDay(LocalDate.parse(day)).map(execs => Ok(toJson(execs)))
  }

  // TODO make real show with fields of task!
  def show(taskId: Long) = Action.async {
    execDao.forTask(taskId).map(execs => Ok(toJson(execs)))
  }

  def dayStatistics(id: Long) = TODO


  def setCost(id: Long, cost: Double) = TODO

  def deleteExec(id: Long, execId: Long) = TODO

}
