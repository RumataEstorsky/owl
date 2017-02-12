package actors

/**
  * Created by rumata on 09/02/2017.
  */

import actors.RemindersToBotActor.SendReminredToBot
import akka.actor.{Actor, Props}
import bot.OwlTelegramBot
import com.google.inject.Inject
import models.Reminder

import scala.io.Source
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Try

object RemindersToBotActor {
  def props = Props[RemindersToBotActor]

  // step 0: reread file with tasks (on demand or bot.reminders.refresh.period)
  case object RefreshReminders
  // step 1: - send reminder to user, put to controlJobs
  case class SendReminredToBot(reminder: Reminder)
  // step 2: check execution (record about execution if not found - send request to user
  case class CheckExecutionJob(reminder: Reminder)
}

class RemindersToBotActor @Inject()( bot: OwlTelegramBot) extends Actor {
  override def receive = {
    case SendReminredToBot(reminder) => sendReminredToBot(reminder)
  }


  override def preStart() = {
    findRemindersFromFile
  }

  def readReminders = Try(Source.fromURL(getClass.getResource("/reminders.txt")).getLines.flatMap {
    case l if !l.trim.startsWith("#") && l.count(_ == '|') >= 2 => {
      val Array(cron, taskId, message) = l.split('|')
      Some(cron.trim, taskId, message.trim)
    }
    case l => None
  }.toList)


  def findRemindersFromFile = {
    import cronish.dsl._
    val raw = readReminders.getOrElse(List[(String, String, String)]())
    val cancellables = raw.map { case (time, taskId, message) =>
      val reminder = new Reminder(time.cron, message, None)
      //println(s"$message: next start at ${cron.nextTime} remains ${remains / 1000 / 60} minutes")
      context.system.scheduler.scheduleOnce(reminder.remains.milliseconds, self, SendReminredToBot(reminder))
    }
    bot.sendSimpleMessage(s"The reminders have been refreshed, loaded ${cancellables.size} item(s).")
  }

  def sendReminredToBot(reminder: Reminder) = {
    //sending
    bot.sendSimpleMessage(reminder.message)
    // planning next sending
    context.system.scheduler.scheduleOnce(reminder.remains.milliseconds, self, SendReminredToBot(reminder))
    // TODO here needs a checking of readiness (!)
  }

}
