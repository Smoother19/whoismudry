package app

import app.ClientState._
import model._
import network.GameClient
import java.net.URI

object GameManager {

  private var world: World = _
  private var localPlayerInput: PlayerInput = PlayerInput.none
  private var phase: GamePhase = GamePhase.Playing

  var tasks: Seq[model.Task] = Seq.empty

  private var localPlayerId: PlayerId = _
  def setLocalId(id: PlayerId): Unit = { localPlayerId = id }
  def currentLocalId: PlayerId = localPlayerId
  private var client: GameClient = _

  def start(tileMap: TileMap, username: String): Unit = {
    world = World(tileMap, Map.empty)

    client = new GameClient(new URI("ws://localhost:8080"), username)
    client.connect()
  }

  def stop(): Unit = {
    if (client != null) client.close()
  }

  def updateWorld(players: Seq[PlayerState]): Unit = {
    val playerMap = players.map(p => p.playerId -> p).toMap
    world = world.copy(players = playerMap)
  }

  def updateLocalInput(input: PlayerInput): Unit = {
    localPlayerInput = input
    if (client != null) client.sendInput(input.dx, input.dy)
  }

  def setTasks(newTasks: Seq[model.Task]): Unit = {
    tasks = newTasks
  }

  def currentWorld: World = world

  def taskNearLocalPlayer(playerCenter: Vec2): Option[Task] = {
    tasks.find { task =>
      val dx = task.position.x - playerCenter.x
      val dy = task.position.y - playerCenter.y
      val distance = Math.sqrt(dx * dx + dy * dy)
      distance < config.GameplayConfig.InteractionRadius
    }
  }

  def currentLocalInput: PlayerInput = localPlayerInput

  def updatePhase(newPhase: GamePhase): Unit = { phase = newPhase }

  def currentPhase: GamePhase = phase

  def callMeeting(): Unit = { if (client != null) client.callMeeting() }

  def submitVote(targetId: PlayerId): Unit = { if (client != null) client.submitVote(targetId.value) }
}