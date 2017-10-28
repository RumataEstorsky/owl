package bot

import javax.inject._

import core.OwlConfiguration
import dao.TaskDAO
import info.mukel.telegrambot4s.Implicits._
import info.mukel.telegrambot4s.api._
import info.mukel.telegrambot4s.api.declarative.Commands
import info.mukel.telegrambot4s.methods._
import info.mukel.telegrambot4s.models._
import play.api.Logger

@Singleton
class OwlTelegramBot @Inject()(conf: play.api.Configuration,
                               taskDAO: TaskDAO,
                               owlConf: OwlConfiguration
                              ) extends TelegramBot with Polling with Commands {
  def token = conf.getString("bot.token").getOrElse{ Logger.warn("Bot token not found!"); ""}
  lazy val userId = conf.getLong("bot.userId").getOrElse(-1L)



  override def receiveMessage(msg: Message): Unit = {
    Logger.info("User: " + msg.chat.id)
    super.receiveMessage(msg)
  }

  def sendSimpleMessage(message: String): Unit = {
    request(SendMessage(userId, message))
  }
}