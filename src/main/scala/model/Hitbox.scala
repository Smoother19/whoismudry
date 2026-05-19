package model

case class Hitbox(offsetX: Float, offsetY: Float, width: Float, height: Float) {
  def corners(pos: Vec2): Seq[Vec2] = {
    val left   = pos.x + offsetX
    val right  = left + width
    val bottom = pos.y + offsetY
    val top    = bottom + height

    Seq(
      Vec2(left, top),
      Vec2(right, top),
      Vec2(left, bottom),
      Vec2(right, bottom)
    )
  }
}
