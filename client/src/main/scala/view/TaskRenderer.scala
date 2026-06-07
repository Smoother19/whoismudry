package view

import ch.hevs.gdx2d.lib.GdxGraphics
import app.tasks.TaskGame

trait TaskRenderer {
  def render(g: GdxGraphics, task: TaskGame): Unit
}