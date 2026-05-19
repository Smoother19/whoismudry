package app

import model.{World, PlayerId, PlayerInput}
import config.GameplayConfig

class GameLogicThread(initialWorld: World, playerId: PlayerId) extends Thread{

  @volatile var world: World = initialWorld
  @volatile var currentInput: PlayerInput = PlayerInput.none
  @volatile var running: Boolean = true

  override def run(): Unit = {
    while (running){
      val inputs = Map(playerId -> currentInput)
      world = world.step(inputs, GameplayConfig.TickDuration)
      Thread.sleep((GameplayConfig.TickDuration * 1000).toLong)
    }
  }
}
