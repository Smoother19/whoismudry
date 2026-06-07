package app.tasks

import config.CardSwipeConfig

class CardSwipeTask {
  private var cardProgress: Float = 0f
  private var success: Boolean = false

  def isComplete: Boolean = success
  def progress: Float = cardProgress

  def update(newProgress: Float): Unit = {
    if (success) return
    cardProgress = math.min(1f, math.max(0f, newProgress))
    if (cardProgress >= CardSwipeConfig.SuccessThreshold) success = true
  }
}