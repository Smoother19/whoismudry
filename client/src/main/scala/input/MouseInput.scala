package input

import com.badlogic.gdx.Gdx
import config.RenderConfig

object MouseInput {
  def poll(): MouseState = {
    val x = Gdx.input.getX.toFloat
    val y = (RenderConfig.WindowHeight - Gdx.input.getY).toFloat
    MouseState(x, y, Gdx.input.isTouched)
  }
}