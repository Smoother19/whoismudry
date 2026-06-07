package view

import ch.hevs.gdx2d.lib.GdxGraphics
import com.badlogic.gdx.graphics.Color
import config.{GameplayConfig, RenderConfig}
import model.World

class LobbyRenderer {
  def render(g: GdxGraphics, world: World, remaining: Float): Unit = {
    g.drawFilledRectangle(
      RenderConfig.WindowWidth / 2f, RenderConfig.WindowHeight / 2f,
      RenderConfig.WindowWidth.toFloat, RenderConfig.WindowHeight.toFloat,
      0f, Color.DARK_GRAY)

    g.drawString(RenderConfig.WindowWidth / 2f - 80f, RenderConfig.WindowHeight - 60f, "LOBBY")

    val count = world.players.size
    val statusText =
      if (count < GameplayConfig.MinPlayersToStart)
        s"Waiting for other players ($count/${GameplayConfig.MinPlayersToStart})"
      else
        s"Start in ${math.ceil(remaining).toInt}s"

    g.drawString(RenderConfig.WindowWidth / 2f - 120f, RenderConfig.WindowHeight - 110f, statusText)

    var yOffset = RenderConfig.WindowHeight - 180f
    world.players.values.foreach { state =>
      g.drawString(RenderConfig.WindowWidth / 2f - 100f, yOffset, state.username)
      yOffset -= 40f
    }
  }

  def dispose(): Unit = {}
}