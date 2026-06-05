package network

import org.apache.pekko.actor.typed.{ActorRef}
import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.server.WebSocketServer
import whoismudry.proto.client.ClientToServer
import whoismudry.proto.common.{Welcome => WelcomeMsg}
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
    val id = conn.getRemoteSocketAddress.toString
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
          whoismudry.proto.common.TaskState("task_1", Some(whoismudry.proto.common.Vec2(1600f, 1800f)), "AdminCard"),
          whoismudry.proto.common.TaskState("task_2", Some(whoismudry.proto.common.Vec2(400f, 300f)), "test")
        )

        val welcome = ServerToClient(ServerToClient.Payload.Welcome(WelcomeMsg(playerId = id, tasks = serverTasks)))
        conn.send(welcome.toByteArray)

      case ClientToServer.Payload.SendInput(input) =>
        gameActor ! UpdateInput(id, input.dx, input.dy)
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
}