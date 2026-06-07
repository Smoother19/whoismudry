package model

trait GamePhase
object GamePhase {
  case class Lobby(remainingTime: Float) extends GamePhase
  case object Playing extends GamePhase
  case class Discussion(remainingTime: Float) extends GamePhase
  case class Voting(remainingTime: Float, votes: Map[PlayerId, PlayerId]) extends GamePhase
}