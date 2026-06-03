package model.mapper

import model.Direction
import whoismudry.proto.common.{Direction => ProtoDirection}

object DirectionMapper {
  def toProto(d: Direction): ProtoDirection = {
    val protoDir = d match {
      case Direction.Up    => ProtoDirection.DIRECTION_UP
      case Direction.Down  => ProtoDirection.DIRECTION_DOWN
      case Direction.Left  => ProtoDirection.DIRECTION_LEFT
      case Direction.Right => ProtoDirection.DIRECTION_RIGHT
    }
    protoDir
  }

  def fromProto(d: ProtoDirection): Direction = {
    val modelDir = d match {
      case ProtoDirection.DIRECTION_UP  => Direction.Up
      case ProtoDirection.DIRECTION_LEFT  => Direction.Left
      case ProtoDirection.DIRECTION_RIGHT  => Direction.Right
      case ProtoDirection.DIRECTION_DOWN => Direction.Down
      case _ => Direction.Down
    }
    modelDir
  }
}
