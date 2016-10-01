package actors

import javax.inject.Inject

import actors.RemindScheduleActor.{GetClinetToken, SaySomething, SendTimeToClient}
import akka.actor._
import play.api.cache.{Cache, CacheApi}
import play.api.libs.json.Json
import play.api.libs.ws.WSClient
import play.api.libs.ws.ahc.AhcCurlRequestLogger

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._



object RemindScheduleActor {
  val CacheKey = "RegIds"
  def props = Props[RemindScheduleActor]
  case class SendTimeToClient()
  case class SaySomething()
  case class GetClinetToken(token: String)
}

class RemindScheduleActor @Inject()(ws: WSClient, conf: play.api.Configuration, cache: CacheApi) extends Actor {
  import RemindScheduleActor.CacheKey
  type RegIdsType = Set[String]

  def receive = {
    //case SaySomething => println("go!!!")
    case SendTimeToClient => sendAndroid()
    case GetClinetToken(token) => {
      val regIds = cache.get[RegIdsType](CacheKey).getOrElse(Set[String]())
      cache.set(CacheKey, regIds + token)
    }

  }

  override def preStart(): Unit = {
    println(context.self.path)
      context.system.scheduler.schedule(0 milliseconds, 5 seconds, self, SaySomething)

  }



  private def sendAndroid() = {
    val GcmUrl = conf.getString("gcm.url").getOrElse("")
    val ApiKey = conf.getString("gcm.apiKey").getOrElse("")
    val regIds: RegIdsType = cache.get[RegIdsType](CacheKey).getOrElse(Set[String]())
//    import scala.collection.JavaConversions._
//    val RegIds = conf.getStringList("gcm.regIds").map(_.toList)

    val data = Json.obj(
      "registration_ids" -> regIds.toList,
      "data" -> Json.obj("message" -> System.currentTimeMillis())
    )

    ws.url(GcmUrl)
      .withRequestFilter(AhcCurlRequestLogger())
      .withHeaders(
        "Authorization" -> s"key=$ApiKey"
      ).post(data)
  }

}
