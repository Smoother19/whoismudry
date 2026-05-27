import org.apache.pekko.actor.typed.ActorSystem
import org.apache.pekko.actor.typed.Behavior
import org.apache.pekko.actor.typed.scaladsl.Behaviors

object GreeterActor {
  trait Command
  case class Greet (name: String) extends Command

  def apply(): Behavior[Command] = Behaviors.receiveMessage { message =>
    message match{
      case Greet(name) =>
        println(s"Hello ${name}")
        Behaviors.same
    }
  }
}

object ServerMain extends App{
  val system: ActorSystem[GreeterActor.Command] = ActorSystem(GreeterActor(), "whoismudry-server")

  system ! GreeterActor.Greet("Mudry")
  system ! GreeterActor.Greet("World")

  Thread.sleep(500)
  system.terminate()
}