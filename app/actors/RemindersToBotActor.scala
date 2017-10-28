package actors


import actors.RemindersToBotActor.SendReminredToBot
import akka.actor.{Actor, Props}
import bot.OwlTelegramBot
import com.google.inject.Inject
import core.OwlConfiguration
import models.Reminder
import play.api.Logger

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.io.Source
import scala.util.{Failure, Success, Try}

object RemindersToBotActor {
  def props = Props[RemindersToBotActor]

  // step 0: reread file with tasks (on demand or bot.reminders.refresh.period)
  case object RefreshReminders
  // step 1: - send reminder to user, put to controlJobs
  case class SendReminredToBot(reminder: Reminder)
  // step 2: check execution (record about execution if not found - send request to user
  case class CheckExecutionJob(reminder: Reminder)
}

class RemindersToBotActor @Inject()(bot: OwlTelegramBot, owlConf: OwlConfiguration) extends Actor {
  val logger = Logger(this.getClass)

  override def receive = {
    case SendReminredToBot(reminder) => sendReminredToBot(reminder)
  }


  override def preStart() = {
    findRemindersFromFile
  }

  def readReminders = Try(Source.fromFile(owlConf.workspaceDir + "/reminders/periodic.txt").getLines.flatMap {
    case l if !l.trim.startsWith("#") && l.count(_ == '|') >= 2 => {
      import cronish.dsl._
      val Array(schedule, _, message) = l.split('|')
      Some(Reminder(schedule.cron, message, None))
    }
    case l => None
  }.toList)


  def findRemindersFromFile = readReminders match {
    case Success(raw) => raw.map { reminder =>
        //println(s"$message: next start at ${cron.nextTime} remains ${remains / 1000 / 60} minutes")
        context.system.scheduler.scheduleOnce(reminder.remains.milliseconds, self, SendReminredToBot(reminder))
        Logger.info(s"added cron-reminder [${reminder.cron}] ${reminder.message}")
      }
      bot.sendSimpleMessage(s"The reminders have been refreshed, loaded ${raw.size} item(s).")

    case Failure(ex) => bot.sendSimpleMessage("Something went wrong when i read file " + ex.getMessage)
  }

  def sendReminredToBot(reminder: Reminder) = {
    //sending
    bot.sendSimpleMessage(reminder.message)
    // planning next sending
    context.system.scheduler.scheduleOnce(reminder.remains.milliseconds, self, SendReminredToBot(reminder))
    // TODO here needs a checking of readiness (!)
  }

}
