package view

import config.RenderConfig
import model.{Direction, PlayerId, PlayerState}
import ch.hevs.gdx2d.lib.GdxGraphics
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.Texture

class PlayerRenderer(texture: Texture) {

  private val frames: Array[Array[TextureRegion]] = TextureRegion.split(texture, RenderConfig.PlayerSpriteWidth, RenderConfig.PlayerSpriteHeight)
  private var stateTime: Map[PlayerId, Float] = Map.empty

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
  }

  private def rowIndexFor(dir: Direction): Int = dir match {
    case Direction.Up    => 8
    case Direction.Left  => 9
    case Direction.Down  => 10
    case Direction.Right => 11
  }
}