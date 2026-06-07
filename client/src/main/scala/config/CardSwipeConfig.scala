package config

object CardSwipeConfig {
  val TrackStartX: Float = RenderConfig.WindowWidth / 2f - 250f
  val TrackEndX: Float   = RenderConfig.WindowWidth / 2f + 250f
  val TrackY: Float      = RenderConfig.WindowHeight / 2f

  val CardWidth: Float  = 100f
  val CardHeight: Float = 140f

  val GrabRadius: Float = 60f
  val SuccessThreshold: Float = 0.95f
}