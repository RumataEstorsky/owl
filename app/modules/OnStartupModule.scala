package modules

import javax.inject._

import actors.{RemindScheduleActor, RemindersToBotActor}
import com.google.inject.AbstractModule
import play.api.libs.concurrent.AkkaGuiceSupport


/**
  * Created by rumata on 04.04.16.
  */
class OnStartupModule @Inject() extends AbstractModule with AkkaGuiceSupport {

  override def configure = {
    bindActor[RemindScheduleActor]("remind-schedule-actor")
    bindActor[RemindersToBotActor]("reminders-to-bot-actor")
  }

}
