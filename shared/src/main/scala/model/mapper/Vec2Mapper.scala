package model.mapper

import model.Vec2
import whoismudry.proto.common.{Vec2 => ProtoVec2}

object Vec2Mapper {
  def toProto(v: Vec2): ProtoVec2 = ProtoVec2(v.x, v.y)
  def fromProto(v: ProtoVec2): Vec2 = Vec2(v.x, v.y)
}