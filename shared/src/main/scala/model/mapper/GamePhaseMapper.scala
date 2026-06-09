package model.mapper

import model.{GamePhase, PlayerId}
import whoismudry.proto.common.GamePhaseType.GAME_PHASE_TYPE_LOBBY
import whoismudry.proto.common.{GamePhaseType, GamePhase => ProtoPhase, Vote => ProtoVote}

object GamePhaseMapper {

  def toProto(phase: GamePhase): ProtoPhase = phase match {
    case GamePhase.Playing =>
      ProtoPhase(GamePhaseType.GAME_PHASE_TYPE_PLAYING, 0f, Seq.empty)

    case GamePhase.Discussion(remaining) =>
      ProtoPhase(GamePhaseType.GAME_PHASE_TYPE_DISCUSSION, remaining, Seq.empty)

    case GamePhase.Voting(remaining, votes) =>
      val protoVotes = votes.toSeq.map { case (voter, target) =>
        ProtoVote(voter.value, target.value)
      }
      ProtoPhase(GamePhaseType.GAME_PHASE_TYPE_VOTING, remaining, protoVotes)

    case GamePhase.Lobby(remaining) =>
      ProtoPhase(GamePhaseType.GAME_PHASE_TYPE_LOBBY, remaining, Seq.empty)

    case GamePhase.GameOver(_) =>
      ProtoPhase(GamePhaseType.GAME_PHASE_TYPE_GAME_OVER, 0f, Seq.empty)
  }




  def fromProto(p: ProtoPhase): GamePhase = p.phaseType match {
    case GamePhaseType.GAME_PHASE_TYPE_DISCUSSION =>
      GamePhase.Discussion(p.remainingTime)

    case GamePhaseType.GAME_PHASE_TYPE_VOTING =>
      val votes = p.votes.map { v => PlayerId(v.voterId) -> PlayerId(v.targetId) }.toMap
      GamePhase.Voting(p.remainingTime, votes)

    case GAME_PHASE_TYPE_LOBBY =>
      GamePhase.Lobby(p.remainingTime)

    case GamePhaseType.GAME_PHASE_TYPE_GAME_OVER =>
      GamePhase.GameOver("Fin de la partie !")

    case _ =>
      GamePhase.Playing
  }
}