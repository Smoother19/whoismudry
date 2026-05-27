package input

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Keys
import model.PlayerInput


object KeyboardInput {
  def poll(): PlayerInput = {
    var dx: Float = 0f
    var dy: Float = 0f

    if (Gdx.input.isKeyPressed(Keys.D)) dx += 1.0f
    if (Gdx.input.isKeyPressed(Keys.A))  dx -= 1.0f
    if (Gdx.input.isKeyPressed(Keys.W)) dy += 1.0f
    if (Gdx.input.isKeyPressed(Keys.S)) dy -= 1.0f

    PlayerInput(dx, dy)
  }
}
