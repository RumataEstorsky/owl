package modules

import javax.inject._

import actors.RemindScheduleActor
import akka.actor._
import com.google.inject.AbstractModule
import play.api.Logger
import play.api.libs.concurrent.AkkaGuiceSupport

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

/**
 * Created by rumata on 04.04.16.
 */
class OnStartupModule @Inject()(system: ActorSystem) extends AbstractModule with AkkaGuiceSupport {

  override def configure = {
    Logger.info("App!!!")

    val remind = system.actorOf(RemindScheduleActor.props, "remind-schedule-actor")
    system.scheduler.schedule(0 milliseconds, 5 seconds, remind, "go")
  }

}
