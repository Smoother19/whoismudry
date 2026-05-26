import whoismudry.proto.test.TestMessage

object Playground extends App{
  val original = TestMessage("test", 42)

  val bytes = original.toByteArray

  println(s"Length: ${bytes.length} bytes")

  val decoded = TestMessage.parseFrom(bytes)

  println(original == decoded)
}