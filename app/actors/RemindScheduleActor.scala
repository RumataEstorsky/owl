package actors

import akka.actor._

object RemindScheduleActor {
  def props = Props[RemindScheduleActor]
}

class RemindScheduleActor extends Actor {

  def receive = {
    case "go" => println("go!!!")
  }

}
