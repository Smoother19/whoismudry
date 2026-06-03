package model

case class PlayerState(playerId: PlayerId, username: String,  position: Vec2, facing: Direction, isMoving: Boolean)