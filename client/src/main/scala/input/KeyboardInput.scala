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

    val interact = Gdx.input.isKeyJustPressed(Keys.E)

    PlayerInput(dx, dy, interact)
  }

  def pollUserNameInput(current: String): String = {
    var name = current

    for (key <- Keys.A to Keys.Z) {
      if (Gdx.input.isKeyJustPressed(key)) {
        name = name + Keys.toString(key).toLowerCase
      }
    }

    if (Gdx.input.isKeyJustPressed(Keys.BACKSPACE) && name.nonEmpty) {
      name = name.substring(0, name.length - 1)
    }

    name
  }
}
