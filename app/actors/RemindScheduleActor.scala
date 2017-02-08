package actors

import javax.inject.Inject

import actors.RemindScheduleActor.{GetClinetToken, SaySomething, SendTimeToClient}
import akka.actor._
import bot.OwlTelegramBot
import play.api.Logger
import play.api.cache.CacheApi
import play.api.libs.json.Json
import play.api.libs.ws.WSClient
import play.api.libs.ws.ahc.AhcCurlRequestLogger

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._


object RemindScheduleActor {
  val CacheKey = "RegIds"

  def props = Props[RemindScheduleActor]

  case object SendTimeToClient

  case object SaySomething

  case class GetClinetToken(token: String)

}

class RemindScheduleActor @Inject()(ws: WSClient, conf: play.api.Configuration, cache: CacheApi, bot: OwlTelegramBot) extends Actor {

  import RemindScheduleActor.CacheKey

  type RegIdsType = Set[String]

  def receive = {
    case SendTimeToClient => sendAndroid()
    case SaySomething => bot.sendHi
    case GetClinetToken(token) => {
      val regIds = cache.get[RegIdsType](CacheKey).getOrElse(Set[String]())
      cache.set(CacheKey, regIds + token)
      Logger.info("Added Token from device: " + token)
    }

  }

  override def preStart() = {
    context.system.scheduler.schedule(0 milliseconds, 60 minutes, self, SendTimeToClient)
    context.system.scheduler.schedule(0 milliseconds, 5 second, self, SaySomething)
  }


  private def sendAndroid() = {
    val GcmUrl = conf.getString("gcm.url").getOrElse("")
    val ApiKey = conf.getString("gcm.apiKey").getOrElse("")
    val regIds: RegIdsType = cache.get[RegIdsType](CacheKey).getOrElse(Set[String]())

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
