package services

import models.Task


/**
  * Created by rumata on 04/11/16.
  */
class AchievementService {

  def getScope(task: Task, count: Int) = count * task.cost

}
