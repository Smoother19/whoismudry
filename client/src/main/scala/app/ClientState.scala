package app

sealed trait ClientState

object ClientState {

  case object Playing extends ClientState
  case class Voting(var timeRemaining: Float) extends ClientState

  sealed trait TaskType
  case object AdminCard extends TaskType

  case class DoingTask(task: TaskType) extends ClientState
}
