package services

import models.Task


class AchievementService {

  def getScope(task: Task, count: Int) = count * task.cost

}
