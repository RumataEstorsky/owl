package bot

import javax.inject._

import dao.TaskDAO
import info.mukel.telegrambot4s.Implicits._
import info.mukel.telegrambot4s._
import info.mukel.telegrambot4s.api._
import info.mukel.telegrambot4s.methods._
import info.mukel.telegrambot4s.models._
import play.api.Logger

@Singleton
class OwlTelegramBot @Inject()(conf: play.api.Configuration, taskDAO: TaskDAO)  extends TelegramBot with Polling with Commands {
  def token = conf.getString("bot.token").getOrElse{ Logger.warn("Bot token not found!"); ""}
  lazy val userId = conf.getLong("bot.userId").getOrElse(-1L)

  on("/start") { implicit msg => _ =>
      reply(
        s"""OwlTelegramBot.
         |Bot for you
         | /add
       """.stripMargin
    )
  }


  override def onMessage(message: Message): Unit =  {
    Logger.info("User: " + message.chat.id)
    for(text <- message.text)
      request(SendMessage(message.chat.id, text.reverse))
    //super.onMessage(message)
  }

  def sendSimpleMessage(message: String): Unit = {
    request(SendMessage(userId, message))
    //taskDAO.activeList.map { list => }
  }
}