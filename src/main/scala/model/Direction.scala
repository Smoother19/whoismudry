package model

trait Direction

// all the directions are in a Direction object to access them by ex:Direction.Up
object Direction {
  case object Up extends Direction
  case object Down extends Direction
  case object Right extends Direction
  case object Left extends Direction
}
