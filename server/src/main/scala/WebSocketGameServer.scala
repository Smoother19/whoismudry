package server

import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.server.WebSocketServer

import java.net.InetSocketAddress
import java.nio.ByteBuffer
import whoismudry.proto.client.ClientToServer
import org.apache.pekko.actor.typed.ActorRef
import org.apache.pekko.actor.typed.ActorSystem
import whoismudry.proto.server.ServerToClient
import whoismudry.proto.common.{Welcome => WelcomeMsg}


class WebSocketGameServer(port: Int, gameActor: ActorRef[GameActor.Command]) extends WebSocketServer(new InetSocketAddress(port)) {
  private val connections = scala.collection.mutable.Map[WebSocket, String]()

  override def onOpen(conn: WebSocket, handshake: ClientHandshake): Unit = {
    val id =conn.getRemoteSocketAddress.toString
    connections(conn) = id
    gameActor ! GameActor.Join(id)
    val welcome = ServerToClient(ServerToClient.Payload.Welcome(WelcomeMsg(id)))
    conn.send(welcome.toByteArray)
    println(s"Connection ${id} opened")
  }

  override def onClose(conn: WebSocket, code: Int, reason: String, remote: Boolean): Unit = {
    val id =conn.getRemoteSocketAddress.toString
    connections.remove(conn).foreach(id => gameActor ! GameActor.Leave(id))
    println(s"Connection ${id} closed")
  }

  override def onMessage(conn: WebSocket, bytes: ByteBuffer): Unit = {
    val id = connections(conn)
    val message = ClientToServer.parseFrom(bytes.array())
    message.payload match {
      case ClientToServer.Payload.SendInput(input) =>
        gameActor ! GameActor.UpdateInput(id, input.dx, input.dy)
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

object ServerMain extends App {
  var server: WebSocketGameServer = _

  val gameActor: ActorSystem[GameActor.Command] =
    ActorSystem(GameActor(bytes => server.broadcast(bytes)), "whoismudry-server")

  server = new WebSocketGameServer(8080, gameActor)

  server.start()
}