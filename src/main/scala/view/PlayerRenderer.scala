package view

import config.RenderConfig
import model.{PlayerState, Direction}
import ch.hevs.gdx2d.lib.GdxGraphics
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.Texture

class PlayerRenderer(texture: Texture) {

  private val frames: Array[Array[TextureRegion]] = TextureRegion.split(texture, RenderConfig.PlayerSpriteWidth, RenderConfig.PlayerSpriteHeight)
  private var stateTime: Float = 0f

  def render(g: GdxGraphics, state: PlayerState, deltaTime: Float): Unit = {
    if (state.isMoving){
      stateTime += deltaTime
    }else{
      stateTime = 0f
    }

    val currentFrameIdx = if (state.isMoving){((stateTime / RenderConfig.PlayerAnimationFrameDuration) % RenderConfig.PlayerAnimationFrameCount).toInt}
    else{0}

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