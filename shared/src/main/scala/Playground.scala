import whoismudry.proto.game._

object Playground extends App{
  val original = PlayerState("1", Some(Vec2(1f, 2f)), Direction.DIRECTION_UP, true)

  val bytes = original.toByteArray
  println(s"Length: ${bytes.length} bytes")

  val decoded = PlayerState.parseFrom(bytes)

  println(original == decoded)
}