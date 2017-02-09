package actors

/**
  * Created by rumata on 09/02/2017.
  */

import actors.RemindersToBotActor.SendReminredToBot
import akka.actor.{Actor, Props}
import bot.OwlTelegramBot
import com.google.inject.Inject

import scala.io.Source
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

object RemindersToBotActor {
  def props = Props[RemindersToBotActor]

  // step 0: reread file with tasks (on demand or bot.reminders.refresh.period)
  case object RefreshReminders
  // step 1: - send reminder to user, put to controlJobs
  case class SendReminredToBot(message: String)
  // step 2: check execution (record about execution if not found - send request to user
  case class CheckExecutionJob(taskId: Long)
}

class RemindersToBotActor @Inject()( bot: OwlTelegramBot) extends Actor {
  override def receive = {
    case SendReminredToBot(message) => bot.sendSimpleMessage(message)
  }


  override def preStart() = {
    findRemindersFromFile
  }

  def readReminders = Source.fromURL(getClass.getResource("/reminders.txt")).getLines.flatMap {
    case l if !l.trim.startsWith("#") && l.contains("|") => {
      val Array(cron, message) = l.split('|')
      Some(cron.trim -> message.trim)
    }
    case l => None
  }


  def findRemindersFromFile = {
    import cronish.dsl._
    import scalendar._
    val now = Scalendar.now
    readReminders.toList.map { case (time, message) =>
      val cron = time.cron
      val remains = cron.nextFrom(now)
      //println(s"$message: next start at ${cron.nextTime} remains ${remains / 1000 / 60} minutes")
      val cancellable = context.system.scheduler.scheduleOnce(remains milliseconds, self, SendReminredToBot(message))
      context.system.scheduler.maxFrequency
    }
  }
}
