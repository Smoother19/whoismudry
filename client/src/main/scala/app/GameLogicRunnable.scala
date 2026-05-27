package app

import config.GameplayConfig

class GameLogicRunnable extends Runnable {

  private var isRunning: Boolean = true

  override def run(): Unit = {
    while (isRunning) {
      GameManager.tick(GameplayConfig.TickDuration)
      Thread.sleep((GameplayConfig.TickDuration * 1000).toLong)
    }
  }

  def stop(): Unit = {
    isRunning = false
  }
}