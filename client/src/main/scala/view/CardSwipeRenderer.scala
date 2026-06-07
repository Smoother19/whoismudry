package view

import ch.hevs.gdx2d.lib.GdxGraphics
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics._
import config.{CardSwipeConfig, RenderConfig}
import model.Task
import model.tasks.AdminCard

class CardSwipeRenderer {
  private val batch = new SpriteBatch()
  private val buttonTexture = new Texture("tasksIcons/admincard.png")
  private val buttonWidth = 50f
  private val buttonHeight = 32f

  def renderMapButton(camera: OrthographicCamera, tasks: Seq[Task]): Unit = {
    batch.setProjectionMatrix(camera.combined)
    batch.begin()
    for (task <- tasks) {
      if (task.taskType == AdminCard) {
        batch.draw(buttonTexture,
          task.position.x - (buttonWidth / 2),
          task.position.y - (buttonHeight / 2),
          buttonWidth, buttonHeight)
      }
    }
    batch.end()
  }

  def render(g: GdxGraphics, progress: Float, success: Boolean): Unit = {
    g.drawFilledRectangle(RenderConfig.WindowWidth / 2f, RenderConfig.WindowHeight / 2f, 700f, 400f, 0f, Color.DARK_GRAY)

    g.drawString(RenderConfig.WindowWidth / 2f - 120, RenderConfig.WindowHeight / 2f + 150, "TASK : swipe card")
    g.drawString(RenderConfig.WindowWidth / 2f - 150, RenderConfig.WindowHeight / 2f - 150, "Appuyez sur X pour quitter")

    g.drawFilledRectangle(RenderConfig.WindowWidth / 2f, CardSwipeConfig.TrackY, 600f, 80f, 0f, Color.BLACK)

    val cardX = CardSwipeConfig.TrackStartX + progress * (CardSwipeConfig.TrackEndX - CardSwipeConfig.TrackStartX)
    val cardColor = if (success) Color.GREEN else Color.YELLOW

    g.drawFilledRectangle(cardX, CardSwipeConfig.TrackY, CardSwipeConfig.CardWidth, CardSwipeConfig.CardHeight, 0f, cardColor)
  }

  def dispose(): Unit = batch.dispose()
}