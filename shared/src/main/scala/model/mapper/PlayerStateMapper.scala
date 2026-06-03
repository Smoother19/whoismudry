package model.mapper

import model.Vec2
import model.{PlayerState, PlayerId}
import whoismudry.proto.common.{PlayerState => ProtoPlayerState}

object PlayerStateMapper {
  def toProto(s: PlayerState): ProtoPlayerState =
    ProtoPlayerState(
      s.id.value,
      Some(Vec2Mapper.toProto(s.position)),
      DirectionMapper.toProto(s.facing),
      s.isMoving
    )

  def fromProto(p: ProtoPlayerState): PlayerState =
    PlayerState(
      PlayerId(p.id),
      p.position.map(Vec2Mapper.fromProto).getOrElse(Vec2(0f, 0f)),
      DirectionMapper.fromProto(p.facing),
      p.isMoving
    )
}