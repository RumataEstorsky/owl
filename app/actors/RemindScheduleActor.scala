package actors

import javax.inject.Inject

import akka.actor._
import play.api.libs.json.Json
import play.api.libs.ws.WSClient
import play.api.libs.ws.ahc.AhcCurlRequestLogger

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._



object RemindScheduleActor {
  def props = Props[RemindScheduleActor]
}

class RemindScheduleActor @Inject()(ws: WSClient, conf: play.api.Configuration) extends Actor {

  def receive = {
    case "go" => println("go!!!")
    case "and" => sendAndroid()

  }

  override def preStart(): Unit = {
    println(context.self.path)
      context.system.scheduler.schedule(0 milliseconds, 5 seconds, self, "go")

  }



  private def sendAndroid() = {
    val GcmUrl = conf.getString("gcm.url").getOrElse("")
    val ApiKey = conf.getString("gcm.apiKey").getOrElse("")
    import scala.collection.JavaConversions._
    val RegIds = conf.getStringList("gcm.regIds").map(_.toList)

    val data = Json.obj(
      "registration_ids" -> RegIds,
      "data" -> Json.obj("message" -> System.currentTimeMillis())
    )

    ws.url(GcmUrl)
      .withRequestFilter(AhcCurlRequestLogger())
      .withHeaders(
        "Authorization" -> s"key=$ApiKey"
      ).post(data)
  }

}
