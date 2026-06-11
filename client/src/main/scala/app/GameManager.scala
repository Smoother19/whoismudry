package app

import app.ClientState._
import model._
import network.GameClient
import java.net.URI

object GameManager {

  private var world: World = _
  private var localPlayerInput: PlayerInput = PlayerInput.none
  private var phase: GamePhase = GamePhase.Playing
  private var localRole: Role = Role.Students

  var tasks: Seq[model.Task] = Seq.empty

  private var localPlayerId: PlayerId = _
  def setLocalId(id: PlayerId): Unit = { localPlayerId = id }
  def currentLocalId: PlayerId = localPlayerId
  private var client: GameClient = _

  def start(tileMap: TileMap, username: String): Unit = {
    world = World(tileMap, Map.empty)

    client = new GameClient(new URI("ws://whoismudry.apco-technologie.ovh"), username)
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
    if (localRole == Role.Mudry) {
      return None
    }
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

  def setLocalRole(r: Role): Unit = {
    localRole = r
    println("Role : " + r)
  }

  def currentLocalRole: Role = localRole

  def attemptKill(): Unit = {
    if (world == null || currentLocalId == null){}
    val localPlayer = world.players(currentLocalId)

    val killRadius = config.GameplayConfig.InteractionRadius
    val target = world.players.values
      .filter(p => p.playerId != currentLocalId && !p.isDead)
      .map { p =>
        val dx = p.position.x - localPlayer.position.x
        val dy = p.position.y - localPlayer.position.y
        (p, Math.sqrt(dx * dx + dy * dy))
      }
      .minByOption(_._2)

    target match {
      case Some((p, dist)) if dist <= killRadius =>
        println(s"kill attempt on ${p.playerId}")
        if (client != null) client.sendKill(p.playerId.value)
      case _ =>
        println("no target close enough")
    }
  }
}