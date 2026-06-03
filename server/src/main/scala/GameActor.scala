package server

import org.apache.pekko.actor.typed.Behavior
import org.apache.pekko.actor.typed.scaladsl.Behaviors
import scala.concurrent.duration._
import model.{PlayerId, PlayerInput, PlayerState, Direction, Vec2, World, TileMap}
import whoismudry.proto.common.{WorldSnapshot, PlayerState => ProtoPlayerState, Vec2 => ProtoVec2, Direction => ProtoDirection}
import whoismudry.proto.server.ServerToClient
import model.mapper.PlayerStateMapper
import loader.TmxLoader
import config.GameplayConfig

object GameActor {

  sealed trait Command
  case class Join(playerId: String) extends Command
  case class UpdateInput(playerId: String, dx: Float, dy: Float) extends Command
  case class Leave(playerId: String) extends Command
  private case object Tick extends Command

  def apply(broadcast: Array[Byte] => Unit): Behavior[Command] =
    Behaviors.withTimers { timers =>
      timers.startTimerAtFixedRate(Tick, 30.milliseconds)

      //val emptyWalls = Array.fill(100, 100)(false)
      val serverMap = TmxLoader.load(GameplayConfig.MapAssetPath, GameplayConfig.WallLayerName)
      val initialWorld = World(serverMap, Map.empty)

      active(initialWorld, Map.empty, broadcast)
    }

  private def active(world: World, inputs: Map[PlayerId, PlayerInput], broadcast: Array[Byte] => Unit ): Behavior[Command] = Behaviors.receiveMessage {

    case Join(idStr) =>
      val playerId = PlayerId(idStr)
      val startPos = Vec2(world.tileMap.pixelWidth / 2f, world.tileMap.pixelHeight / 2f)
      val playerState = PlayerState(playerId, startPos, Direction.Down, false)
      val newWorld = world.copy(players = world.players + (playerId -> playerState))
      val newInputs = inputs + (playerId -> PlayerInput.none)
      active(newWorld, newInputs, broadcast)

    case UpdateInput(idStr, dx, dy) =>
      val playerId = PlayerId(idStr)
      val playerInput = PlayerInput(dx, dy)
      val newInputs = inputs + (playerId -> playerInput)
      println(s"input from $idStr : dx=$dx dy=$dy")
      active(world, newInputs, broadcast)

    case Leave(idStr) =>
      val playerId = PlayerId(idStr)
      val newWorld = world.copy(players = world.players - playerId)
      val newInputs = inputs - playerId
      active(newWorld, newInputs, broadcast)

    case Tick =>
      val newWorld = world.step(inputs, 0.030f)

      val protoPlayers = newWorld.players.values.map(PlayerStateMapper.toProto).toSeq

      val snapshot = ServerToClient(
        ServerToClient.Payload.WorldSnapshot(WorldSnapshot(protoPlayers))
      )

      broadcast(snapshot.toByteArray)

      active(newWorld, inputs, broadcast)
  }

}