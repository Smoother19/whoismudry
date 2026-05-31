package app

import app.ClientState._
import model._

object GameManager {

  private var world: World = _
  private var localPlayerInput: PlayerInput = PlayerInput.none
  private var logicThread: Thread = _
  private var logicRunnable: GameLogicRunnable = _
  private var localPlayerId: PlayerId = _

  private var votingTimer: Float = 0f
  var isVotingPhase: Boolean = false
  val emergencyButtonPos = Vec2(1600f, 1800f) // regarder ou placer les boutons
  val cardTaskPos = Vec2(1100f, 1800f) // 1600f, 1800f
  private val interactionRadius = 50f

  var currentState: ClientState = Playing

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

  def completeTask(): Unit = {
    currentState = Playing
    println("Tache terminer")
  }

  def cancelTask(): Unit = {
    currentState = Playing
  }


  def tick(deltaTime: Float): Unit = {
    currentState match {
      case Playing =>
        val inputs = Map(localPlayerId -> localPlayerInput)
        world = world.step(inputs, deltaTime)

        if (localPlayerInput.interact) {
          val localPlayer = world.players(localPlayerId)

          if (isCloseToButton(localPlayer.position, emergencyButtonPos, interactionRadius)) {
            currentState = Voting(30f)
            println("Emergency Meeting")
          }
          else if (isCloseToButton(localPlayer.position, cardTaskPos, interactionRadius)) {
            currentState = DoingTask(AdminCard)
          }
        }

      case v: Voting =>
        v.timeRemaining -= deltaTime
        if (v.timeRemaining <= 0f) {
          currentState = Playing
          println("Fin du vote ! Reprise de la partie.")
        }

      case t: DoingTask =>
    }
  }


  private def isCloseToButton(playerPos: Vec2, buttonPos: Vec2, radius: Float): Boolean = {
    val dx = playerPos.x - buttonPos.x
    val dy = playerPos.y - buttonPos.y
    math.sqrt(dx * dx + dy * dy) <= radius
  }
}