package model

// com.badlogic.gdx.math.Vector2 not used because it's  mutable and depends on libgdx not my own
case class Vec2(x: Float, y: Float) {
  def + (other: Vec2): Vec2 = {
    Vec2(this.x + other.x, this.y + other.y)
  }

  def * (scalar: Float): Vec2 ={
    Vec2(this.x * scalar, this.y * scalar)
  }
}
