package app.tasks

import input.MouseState

trait TaskGame {
  def update(mouse: MouseState): Unit
  def isComplete: Boolean
}