package app.tasks

import config.CardSwipeConfig
import input.MouseState

class CardSwipeTask extends TaskGame {
  private var cardProgress: Float = 0f
  private var success: Boolean = false

  def progress: Float = cardProgress
  def isComplete: Boolean = success

  def update(mouse: MouseState): Unit = {
    if (success) return
    if (mouse.isTouched &&
      math.abs(mouse.y - CardSwipeConfig.TrackY) < CardSwipeConfig.GrabRadius) {
      val p = (mouse.x - CardSwipeConfig.TrackStartX) /
        (CardSwipeConfig.TrackEndX - CardSwipeConfig.TrackStartX)
      cardProgress = math.min(1f, math.max(0f, p))
      if (cardProgress >= CardSwipeConfig.SuccessThreshold) success = true
    }
  }
}