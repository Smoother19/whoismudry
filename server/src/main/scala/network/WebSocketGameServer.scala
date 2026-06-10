package network

import org.apache.pekko.actor.typed.ActorRef
import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.server.WebSocketServer
import whoismudry.proto.client.ClientToServer
import whoismudry.proto.common.{TaskType, Welcome => WelcomeMsg}
import whoismudry.proto.server.ServerToClient
import network.command._

import java.net.InetSocketAddress
import java.nio.ByteBuffer


class WebSocketGameServer(port: Int, gameActor: ActorRef[Command]) extends WebSocketServer(new InetSocketAddress(port)) {
  private val connections = scala.collection.mutable.Map[WebSocket, String]()
  private var nextId = 1

  override def onOpen(conn: WebSocket, handshake: ClientHandshake): Unit = {
    val id = "player-" + nextId
    nextId = nextId + 1
    connections(conn) = id
  }

  override def onClose(conn: WebSocket, code: Int, reason: String, remote: Boolean): Unit = {
    val id = connections(conn)
    connections.remove(conn).foreach(id => gameActor ! Leave(id))
    println(s"Connection ${id} closed")
  }

  override def onMessage(conn: WebSocket, bytes: ByteBuffer): Unit = {
    val id = connections(conn)
    val message = ClientToServer.parseFrom(bytes.array())
    message.payload match {
      case ClientToServer.Payload.JoinGame(join) =>
        gameActor ! Join(id, join.username)

        val serverTasks = Seq(
          whoismudry.proto.common.TaskState("task_1", Some(whoismudry.proto.common.Vec2(1600f, 1800f)), TaskType.TASK_TYPE_ADMIN_CARD),
          whoismudry.proto.common.TaskState("task_2", Some(whoismudry.proto.common.Vec2(1600f, 1700f)), TaskType.TASK_TYPE_PRIERMUDRY),
          whoismudry.proto.common.TaskState("task_3", Some(whoismudry.proto.common.Vec2(1600f, 1900f)), TaskType.TASK_TYPE_QUESTION)
        )

        val welcome = ServerToClient(ServerToClient.Payload.Welcome(WelcomeMsg(playerId = id, tasks = serverTasks)))
        conn.send(welcome.toByteArray)

      case ClientToServer.Payload.SendInput(input) =>
        gameActor ! UpdateInput(id, input.dx, input.dy)

      case ClientToServer.Payload.CallMeeting(_) =>
        gameActor ! CallMeeting(id)

      case ClientToServer.Payload.SubmitVote(vote) =>
        gameActor ! SubmitVote(id, vote.targetId)

      case ClientToServer.Payload.KillPlayer(kp) =>
        gameActor ! KillPlayer(id, kp.targetId)

      case _ =>
    }
  }

  override def onMessage(conn: WebSocket, message: String): Unit = {
    println(s"Text message : $message")
  }

  override def onError(conn: WebSocket, ex: Exception): Unit = {
    println(s"Error : ${ex.getMessage}")
  }

  override def onStart(): Unit = {
    println(s"Websocket Server on port ${port}")
  }

  def sendTo(playerId: String, bytes: Array[Byte]): Unit = {
    for ((conn, id) <- connections) {
      if (id == playerId) {
        conn.send(bytes)
      }
    }
  }
}