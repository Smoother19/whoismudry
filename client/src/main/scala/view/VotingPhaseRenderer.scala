package view

import ch.hevs.gdx2d.lib.GdxGraphics
import com.badlogic.gdx.graphics.Color
import config.RenderConfig
import model.World

class VotingPhaseRenderer {

  def render(g: GdxGraphics, world: World, remainingTime: Float): Unit = {
    g.drawFilledRectangle(
      RenderConfig.WindowWidth / 2f,
      RenderConfig.WindowHeight / 2f,
      RenderConfig.WindowWidth.toFloat,
      RenderConfig.WindowHeight.toFloat,
      0f,
      Color.DARK_GRAY
    )

    g.drawString(
      RenderConfig.WindowWidth / 2f - 120,
      RenderConfig.WindowHeight - 50,
      "EMERGENCY MEETING"
    )

    val secondsLeft = math.ceil(remainingTime).toInt
    g.drawString(
      RenderConfig.WindowWidth / 2f - 50,
      RenderConfig.WindowHeight - 90,
      s"Temps restant : ${secondsLeft}s"
    )

    var yOffset = RenderConfig.WindowHeight - 150

    world.players.keys.foreach { playerId =>
      g.drawRectangle(
        RenderConfig.WindowWidth / 2f,
        yOffset - 10,
        300,
        40,
        0f
      )

      g.drawString(
        RenderConfig.WindowWidth / 2f - 140,
        yOffset,
        s"Voter : ${playerId.toString}"
      )

      yOffset -= 60
    }
  }

  def dispose(): Unit = {
  }
}