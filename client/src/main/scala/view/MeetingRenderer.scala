package view

import ch.hevs.gdx2d.lib.GdxGraphics
import com.badlogic.gdx.graphics.Color
import config.RenderConfig
import model.{GamePhase, PlayerId, World}

class MeetingRenderer {

  def render(g: GdxGraphics, world: World, phase: GamePhase): Seq[VoteOption] = {
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

    val (label, remaining, canVote, votes) = phase match {
      case GamePhase.Discussion(t) => ("Discussion", t, false, Map.empty[PlayerId, PlayerId])
      case GamePhase.Voting(t, v)  => ("Vote", t, true, v)
      case GamePhase.Playing       => ("", 0f, false, Map.empty[PlayerId, PlayerId])
    }

    g.drawString(
      RenderConfig.WindowWidth / 2f - 50, RenderConfig.WindowHeight - 90f,
      s"$label - ${math.ceil(remaining).toInt}s"
    )

    val rowWidth = 300f
    val rowHeight = 40f
    var yOffset = RenderConfig.WindowHeight - 150f
    var options = Seq.empty[VoteOption]

    world.players.foreach { case (playerId, state) =>
      if (!state.isDead){
        val centerX = RenderConfig.WindowWidth / 2f
        g.drawRectangle(centerX, yOffset - 10f, rowWidth, rowHeight, 0f)

        val count = votes.values.count(_ == playerId)
        g.drawString(centerX - 140f, yOffset, s"${state.username}  ($count)")

        if (canVote) {
          options = options :+ VoteOption(playerId, centerX, yOffset - 10f, rowWidth, rowHeight)
        }
        yOffset -= 60f
      }
    }

    options
  }

  def dispose(): Unit = {}
}