package app

import app.ClientState._
import model._
import network.GameClient
import java.net.URI

object GameManager {

  private var world: World = _
  private var localPlayerInput: PlayerInput = PlayerInput.none

  private var localPlayerId: PlayerId = _
  def setLocalId(id: PlayerId): Unit = { localPlayerId = id }
  def currentLocalId: PlayerId = localPlayerId
  private var client: GameClient = _

  def start(tileMap: TileMap, username: String): Unit = {
    world = World(tileMap, Map.empty)

    client = new GameClient(new URI("ws://192.168.0.100:8080"), username)
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

  def currentWorld: World = world
}