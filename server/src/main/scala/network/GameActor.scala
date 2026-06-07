package network

import config.GameplayConfig
import loader.TmxLoader
import model.mapper._
import model._
import org.apache.pekko.actor.typed.Behavior
import org.apache.pekko.actor.typed.scaladsl.Behaviors
import whoismudry.proto.common.WorldSnapshot
import whoismudry.proto.server.ServerToClient
import network.command._

import scala.concurrent.duration._

object GameActor {

  private case object Tick extends Command

  def apply(broadcast: Array[Byte] => Unit): Behavior[Command] =
    Behaviors.withTimers { timers =>
      timers.startTimerAtFixedRate(Tick, 30.milliseconds)

      val serverMap = TmxLoader.load(GameplayConfig.MapAssetPath, GameplayConfig.WallLayerName)
      val initialWorld = World(serverMap, Map.empty, GamePhase.Lobby(GameplayConfig.LobbyCountdown))

      active(initialWorld, Map.empty, broadcast)
    }

  private def active(world: World, inputs: Map[PlayerId, PlayerInput], broadcast: Array[Byte] => Unit ): Behavior[Command] = Behaviors.receiveMessage {

    case Join(idStr, username) =>
      val playerId = PlayerId(idStr)
      val startPos = Vec2(world.tileMap.pixelWidth / 2f, world.tileMap.pixelHeight / 2f)
      val playerState = PlayerState(playerId, username,  startPos, Direction.Down, isMoving = false)
      val newWorld = world.copy(players = world.players + (playerId -> playerState))
      val newInputs = inputs + (playerId -> PlayerInput.none)
      active(newWorld, newInputs, broadcast)

    case UpdateInput(idStr, dx, dy) =>
      val playerId = PlayerId(idStr)
      val playerInput = PlayerInput(dx, dy)
      val newInputs = inputs + (playerId -> playerInput)
      //println(s"input from $idStr : dx=$dx dy=$dy")
      active(world, newInputs, broadcast)

    case Leave(idStr) =>
      val playerId = PlayerId(idStr)
      val newWorld = world.copy(players = world.players - playerId)
      val newInputs = inputs - playerId
      active(newWorld, newInputs, broadcast)

    case Tick =>
      val newWorld = world.step(inputs, 0.030f)

      val protoPlayers = newWorld.players.values.map(PlayerStateMapper.toProto).toSeq
      val protoPhase = GamePhaseMapper.toProto(newWorld.phase)

      val snapshot = ServerToClient(ServerToClient.Payload.WorldSnapshot(WorldSnapshot(protoPlayers, Some(protoPhase))))

      broadcast(snapshot.toByteArray)

      active(newWorld, inputs, broadcast)

    case CallMeeting(_) =>
      val newWorld = world.startMeeting()
      active(newWorld, inputs, broadcast)

    case SubmitVote(voterId, targetId) =>
      val newWorld = world.registerVote(PlayerId(voterId), PlayerId(targetId))
      active(newWorld, inputs, broadcast)
  }

}