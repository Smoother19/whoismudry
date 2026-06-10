package app.tasks

import input.MouseState

class PrierMudryTask extends TaskGame{
  private var started = false
  private var startTime: Long = 0
  private val durationMs = 10000L

  override def update(mouse: MouseState): Unit = {
    if (!started) {
      started = true
      startTime = System.currentTimeMillis()
    }
  }

  def remainingSeconds: Int = {
    if (!started) 10
    else {
      val elapsed = System.currentTimeMillis() - startTime
      val remaining = durationMs - elapsed
      if (remaining < 0) 0 else (remaining / 1000).toInt
    }
  }

  override def isComplete: Boolean = {
    started && (System.currentTimeMillis() - startTime >= durationMs)
  }
}
