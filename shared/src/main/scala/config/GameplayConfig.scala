package config

import model.Hitbox

object GameplayConfig {
  val TileSize: Int = 32
  val PlayerSpeed: Float = 150f
  val PlayerHitbox: Hitbox = Hitbox(offsetX = 20f, offsetY = 0f, width = 24f, height = 32f)
  val VisionRadius: Float = 175f
  val TickDuration: Float = 1f / 60f
  val WallLayerName: String = "Walls"
  val MapAssetPath: String = "/maps/map.tmx"
  val InteractionRadius = 50f
  val DiscussionDuration: Float = 15f
  val VotingDuration: Float = 30f
  val MeetingCooldown: Float = 60f
  val MinPlayersToStart: Int = 2
  val LobbyCountdown: Float = 10f
  val KillCooldown: Long = 15000L
}