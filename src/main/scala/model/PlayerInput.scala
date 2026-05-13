package model

case class PlayerInput(dx: Float, dy: Float) {
  def isMoving: Boolean = {dx != 0f || dy != 0f}
}

// Companion of case class PlayerInput
object PlayerInput {
  val none: PlayerInput = PlayerInput(0f, 0f)
}