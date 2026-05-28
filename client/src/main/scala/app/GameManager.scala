package app

import model._

object GameManager {

  private var world: World = _
  private var localPlayerInput: PlayerInput = PlayerInput.none


  private var logicThread: Thread = _
  private var logicRunnable: GameLogicRunnable = _

  private var localPlayerId: PlayerId = _

  private var votingTimer: Float = 0f
  var isVotingPhase: Boolean = false
  val emergencyButtonPos = Vec2(1600f, 1800f)
  private val interactionRadius = 50f

  def remainingVotingTime: Float = votingTimer

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
    if (isVotingPhase) {
      votingTimer -= deltaTime

      if (votingTimer <= 0f) {
        isVotingPhase = false
        println("Fin du vote")
      }
      return
    }

    val inputs = Map(localPlayerId -> localPlayerInput)
    world = world.step(inputs, deltaTime)

    if (localPlayerInput.interact) {
      val localPlayer = world.players(localPlayerId)
      if (isCloseToButton(localPlayer.position, emergencyButtonPos, interactionRadius)) {
        triggerEmergencyMeeting()
      }
    }
  }


  private def isCloseToButton(playerPos: Vec2, buttonPos: Vec2, radius: Float): Boolean = {
    val dx = playerPos.x - buttonPos.x
    val dy = playerPos.y - buttonPos.y
    math.sqrt(dx * dx + dy * dy) <= radius
  }

  private def triggerEmergencyMeeting(): Unit = {
    isVotingPhase = true
    votingTimer = 30f
  }
}