package config

import model.Hitbox

object GameplayConfig {
  val TileSize: Int = 32
  val PlayerSpeed: Float = 150f
  val PlayerHitbox: Hitbox = Hitbox(offsetX = 20f, offsetY = 0f, width = 24f, height = 32f)
  val VisionRadius: Float = 175f
}