package modules

import javax.inject._

import actors.RemindScheduleActor
import com.google.inject.AbstractModule
import play.api.libs.concurrent.AkkaGuiceSupport


/**
 * Created by rumata on 04.04.16.
 */
class OnStartupModule @Inject()extends AbstractModule with AkkaGuiceSupport {

  override def configure = {
    bindActor[RemindScheduleActor]("remind-schedule-actor")
//    val remind = system.actorOf(RemindScheduleActor.props, "remind-schedule-actor")
//    system.scheduler.schedule(0 milliseconds, 5 seconds, remind, "go")
  }

}
