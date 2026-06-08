import network.{GameActor, WebSocketGameServer}
import network.command.Command
import org.apache.pekko.actor.typed.ActorSystem

object ServerMain extends App {
  var server: WebSocketGameServer = _

  val gameActor: ActorSystem[Command] =
    ActorSystem(GameActor(bytes => server.broadcast(bytes), (id, bytes) => server.sendTo(id, bytes)), "whoismudry-server")

  server = new WebSocketGameServer(8080, gameActor)

  server.start()
}