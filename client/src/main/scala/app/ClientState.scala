package app

import model.TaskType

trait ClientState

object ClientState {

  case class MainMenu(userName: String) extends ClientState
  case object FreeRoam extends ClientState

  case class DoingTask(task: TaskType) extends ClientState
}
