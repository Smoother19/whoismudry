package config

object RenderConfig {
  // Window
  val WindowWidth: Int = 1920
  val WindowHeight: Int = 1080
  val Fps: Int = 60

  // Player sprite
  val PlayerSpriteWidth: Int = 64
  val PlayerSpriteHeight: Int = 64
  val PlayerAnimationFrameDuration: Float = 0.1f
  val PlayerAnimationFrameCount: Int = 9

  // Vision mask
  val VisionMaskTextureSize: Int = 2000
  val MaxDarkness: Float = 0.98f

  // Assets Path
  val MapAssetPath: String = "assets/maps/map.tmx"
  val WallLayerName: String = "Walls"
  val CrewmateTexturePath: String = "assets/spritesheet/crewmate.png"
}