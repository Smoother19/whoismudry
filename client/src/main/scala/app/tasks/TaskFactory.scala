package app.tasks

import model.TaskType
import model.tasks.AdminCard
import view.{TaskRenderer, CardSwipeRenderer}

object TaskFactory {
  def create(taskType: TaskType): (TaskGame, TaskRenderer) = taskType match {
    case AdminCard => (new CardSwipeTask(), new CardSwipeRenderer())
    // case Wires  => (new WiresTask(), new WiresRenderer())
  }
}