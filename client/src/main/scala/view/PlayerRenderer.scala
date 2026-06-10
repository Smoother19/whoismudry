package view

import config.RenderConfig
import model.{Direction, PlayerId, PlayerState}
import ch.hevs.gdx2d.lib.GdxGraphics
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.{BitmapFont, GlyphLayout}
import com.badlogic.gdx.graphics.Color
import app.GameManager
import model.Role

class PlayerRenderer(texture: Texture) {

  private val frames: Array[Array[TextureRegion]] = TextureRegion.split(texture, RenderConfig.PlayerSpriteWidth, RenderConfig.PlayerSpriteHeight)
  private var stateTime: Map[PlayerId, Float] = Map.empty

  private val font = new BitmapFont()
  private val layout = new GlyphLayout()

  def render(g: GdxGraphics, state: PlayerState, deltaTime: Float): Unit = {
    val prev = stateTime.getOrElse(state.playerId, 0f)
    val newTime = if (state.isMoving) prev + deltaTime else 0f
    stateTime = stateTime + (state.playerId -> newTime)
    var currentFrameIdx = 0

    if (state.isMoving) {
      currentFrameIdx = ((newTime / RenderConfig.PlayerAnimationFrameDuration) % RenderConfig.PlayerAnimationFrameCount).toInt
    } else 0

    val frameToDraw = frames(rowIndexFor(state.facing))(currentFrameIdx)
    g.draw(frameToDraw, state.position.x, state.position.y)

    layout.setText(font, state.username)
    val textWidth = layout.width
    val centerX = state.position.x + RenderConfig.PlayerSpriteWidth / 2f - textWidth / 2f
    val textY = state.position.y + RenderConfig.PlayerSpriteHeight

    val isLocalMudry =
      state.playerId == GameManager.currentLocalId && GameManager.currentLocalRole == Role.Mudry

    g.setColor(if (isLocalMudry) Color.RED else Color.WHITE)
    g.drawString(centerX, textY, state.username)
    g.setColor(Color.WHITE)

    if (state.isDead) {
      g.setColor(new Color(1f, 1f, 1f, 0.1f))
      g.draw(frameToDraw, state.position.x, state.position.y)
      g.setColor(Color.WHITE)
    } else {
      g.draw(frameToDraw, state.position.x, state.position.y)
    }
  }

  private def rowIndexFor(dir: Direction): Int = dir match {
    case Direction.Up    => 8
    case Direction.Left  => 9
    case Direction.Down  => 10
    case Direction.Right => 11
  }

  def dispose(): Unit = font.dispose()
}