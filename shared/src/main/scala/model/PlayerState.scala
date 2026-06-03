package model

case class PlayerState(playerId: PlayerId, position: Vec2, facing: Direction, isMoving: Boolean)