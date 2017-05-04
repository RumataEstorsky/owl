package actors

import javax.inject.Inject

import actors.ProductivityNotificationsActor.SendNotificationsAboutForgottenAffairs
import akka.actor.{Actor, Props}
import bot.OwlTelegramBot
import dao.TaskDAO
import models.TaskStatView
import play.api.{Configuration, Logger}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.util.Random

/**
  * Created by rumata on 11/04/2017.
  */
object ProductivityNotificationsActor {
  case object SendNotificationsAboutForgottenAffairs
  def props = Props[ProductivityNotificationsActor]
}

class ProductivityNotificationsActor @Inject()(conf: Configuration, taskDAO: TaskDAO, bot: OwlTelegramBot) extends Actor {
  val logger = Logger(this.getClass)

  val ProbabilityTrigger = 0
  val WorkHours = 16

  val refreshTime = conf.getMilliseconds("bot.periodic.productivity.notifications.refresh.time").getOrElse(10 * 60 * 1000L).milliseconds
  val ticksInDay = WorkHours * 60 / refreshTime.toMinutes

  override def receive = {
    case SendNotificationsAboutForgottenAffairs => sendNotificationsAboutForgottenAffairs()
  }

  override def preStart() = {
    context.system.scheduler.schedule(0 millis, refreshTime, self, SendNotificationsAboutForgottenAffairs)
  }

  def sendNotificationsAboutForgottenAffairs() = taskDAO.activeTaskStatView().map { tasks =>
      tasks.filter(doItNeedToSendNotification).foreach{ tv =>
        bot.sendSimpleMessage(s"Have you forgotten about ${tv.name} already ${tv.daysAgo} days?")
      }
    }

  def doItNeedToSendNotification(ts: TaskStatView) = {
    val fac: Int = Math.max((ticksInDay / ts.daysAgo).toInt, 1)
    println(s"task=${ts.name}, ago=${ts.daysAgo}, factor=$fac")
    Random.nextInt(fac) == ProbabilityTrigger
  }

}