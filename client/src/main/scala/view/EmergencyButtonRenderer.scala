package view

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import model.Vec2

class EmergencyButtonRenderer {
  private val batch = new SpriteBatch()
  private val texture = new Texture("tasksIcons/emergency.png")
  private val width = 64f
  private val height = 64f

  def render(camera: OrthographicCamera, position: Vec2): Unit = {
    batch.setProjectionMatrix(camera.combined)
    batch.begin()
    batch.draw(texture, position.x - width / 2f, position.y - height / 2f, width, height)
    batch.end()
  }

  def dispose(): Unit = {
    batch.dispose()
    texture.dispose()
  }
}