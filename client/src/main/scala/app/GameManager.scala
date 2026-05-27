package app

import model._

object GameManager {

  private var world: World = _
  private var localPlayerInput: PlayerInput = PlayerInput.none


  private var logicThread: Thread = _
  private var logicRunnable: GameLogicRunnable = _

  private var localPlayerId: PlayerId = _

  def start(tileMap: TileMap, localId: PlayerId): Unit = {
    val startPos = Vec2(tileMap.pixelWidth / 2, tileMap.pixelHeight / 2)
    val initialPlayer = PlayerState(localId, startPos, Direction.Down, isMoving = false)

    world = World(tileMap, Map(localId -> initialPlayer))
    localPlayerId = localId

    logicRunnable = new GameLogicRunnable()
    logicThread = new Thread(logicRunnable, "game-logic")
    logicThread.start()
  }

  def stop(): Unit = {
    if (logicRunnable != null) {
      logicRunnable.stop()
    }
    if (logicThread != null) {
      logicThread.join()
    }
  }

  def currentWorld: World = world

  def updateLocalInput(input: PlayerInput): Unit = {
    localPlayerInput = input
  }

  def tick(deltaTime: Float): Unit = {
    val inputs = Map(localPlayerId -> localPlayerInput)
    world = world.step(inputs, deltaTime)
  }
}