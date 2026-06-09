package model

case class PlayerState(playerId: PlayerId, username: String,  position: Vec2, facing: Direction, isMoving: Boolean, role: Role = Role.Students, isDead: Boolean = false, lastKillTime: Long = 0L)