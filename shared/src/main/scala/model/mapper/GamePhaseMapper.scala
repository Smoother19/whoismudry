package model.mapper

import model.{GamePhase, PlayerId}
import whoismudry.proto.common.{GamePhase => ProtoPhase, GamePhaseType, Vote => ProtoVote}

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
  }

  def fromProto(p: ProtoPhase): GamePhase = p.phaseType match {
    case GamePhaseType.GAME_PHASE_TYPE_DISCUSSION =>
      GamePhase.Discussion(p.remainingTime)

    case GamePhaseType.GAME_PHASE_TYPE_VOTING =>
      val votes = p.votes.map { v => PlayerId(v.voterId) -> PlayerId(v.targetId) }.toMap
      GamePhase.Voting(p.remainingTime, votes)

    case _ =>
      GamePhase.Playing
  }
}