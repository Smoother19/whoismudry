package app.tasks

import model.TaskType
import model.tasks.{AdminCard, PrierMudry, Question}
import view.{CardSwipeRenderer, PrierMudryRenderer, TaskRenderer, QuestionRenderer}

object TaskFactory {
  def create(taskType: TaskType): (TaskGame, TaskRenderer) = taskType match {
    case AdminCard => (new CardSwipeTask(), new CardSwipeRenderer())
    case PrierMudry   => (new PrierMudryTask(), new PrierMudryRenderer())
    case Question  => (new QuestionTask(), new QuestionRenderer())
  }
}