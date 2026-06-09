package network

import model.mapper.PlayerStateMapper
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import whoismudry.proto.client.ClientToServer
import whoismudry.proto.server.ServerToClient
import whoismudry.proto.common.SendInput
import app.GameManager
import model.PlayerId
import whoismudry.proto.common.JoinGame
import model.mapper._

import java.net.URI
import java.nio.ByteBuffer

class GameClient(serverUri: URI, username: String) extends WebSocketClient(serverUri) {

  override def onOpen(handshake: ServerHandshake): Unit = {
    val join = ClientToServer(ClientToServer.Payload.JoinGame(JoinGame(username = username)))
    send(join.toByteArray)
    println("Connected to server")
  }

  override def onMessage(bytes: ByteBuffer): Unit = {
    val arr = new Array[Byte](bytes.remaining())
    bytes.get(arr)
    val msg = ServerToClient.parseFrom(arr)

    msg.payload match {

      case ServerToClient.Payload.RoleAssignment(assignment) =>
        GameManager.setLocalRole(RoleMapper.fromProto(assignment.role))
        println(s"gameclient role: ${assignment.role}")

      case ServerToClient.Payload.WorldSnapshot(snap) =>
        GameManager.updateWorld(snap.players.map(PlayerStateMapper.fromProto))
        snap.phase.foreach(p => GameManager.updatePhase(GamePhaseMapper.fromProto(p)))

      case ServerToClient.Payload.Welcome(welcome) =>
        GameManager.setLocalId(PlayerId(welcome.playerId))

        val receivedTasks = welcome.tasks.flatMap(TaskMapper.fromProto)
        GameManager.setTasks(receivedTasks)

        println(s"Reçu ${receivedTasks.size} tâches du serveur")
        receivedTasks.foreach(task => println(task.taskType))

      case _ =>
    }
  }

  override def onMessage(message: String): Unit = {
    println(s"Receveived from server : $message")
  }

  override def onClose(code: Int, reason: String, remote: Boolean): Unit = {
    println(s"Connection closed : $reason")
  }

  override def onError(ex: Exception): Unit = {
    println(s"Error : ${ex.getMessage}")
  }

  def sendInput(dx: Float, dy: Float): Unit = {
    if (isOpen){
      val message = ClientToServer(ClientToServer.Payload.SendInput(SendInput(dx, dy)))
      send(message.toByteArray)
    }
  }

  def callMeeting(): Unit = {
    if (isOpen) {
      val msg = ClientToServer(ClientToServer.Payload.CallMeeting(
        whoismudry.proto.common.CallMeeting()))
      send(msg.toByteArray)
    }
  }

  def submitVote(targetId: String): Unit = {
    if (isOpen) {
      val msg = ClientToServer(ClientToServer.Payload.SubmitVote(
        whoismudry.proto.common.SubmitVote(targetId)))
      send(msg.toByteArray)
    }
  }
}