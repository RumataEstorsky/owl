package modules

import javax.inject._

import actors.{ProductivityNotificationsActor, RemindScheduleActor, RemindersToBotActor}
import com.google.inject.AbstractModule
import play.api.libs.concurrent.AkkaGuiceSupport


class OnStartupModule @Inject() extends AbstractModule with AkkaGuiceSupport {

  override def configure = {
    bindActor[RemindScheduleActor]("remind-schedule-actor")
    bindActor[RemindersToBotActor]("reminders-to-bot-actor")
    bindActor[ProductivityNotificationsActor]("productivity-notifications-actor")
  }

}
