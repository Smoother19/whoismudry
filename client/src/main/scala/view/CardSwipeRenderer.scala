package view

import ch.hevs.gdx2d.lib.GdxGraphics
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics._
import config.{CardSwipeConfig, RenderConfig}
import model.Task
import model.tasks.AdminCard
import app.tasks.{TaskGame, CardSwipeTask}

class CardSwipeRenderer extends TaskRenderer {
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

  def render(g: GdxGraphics, task: TaskGame): Unit = task match {
    case swipe: CardSwipeTask =>
      g.drawFilledRectangle(RenderConfig.WindowWidth / 2f, RenderConfig.WindowHeight / 2f, 700f, 400f, 0f, Color.DARK_GRAY)
      g.drawString(RenderConfig.WindowWidth / 2f - 120, RenderConfig.WindowHeight / 2f + 150, "TASK : swipe card")
      g.drawString(RenderConfig.WindowWidth / 2f - 150, RenderConfig.WindowHeight / 2f - 150, "Appuyez sur X pour quitter")
      g.drawFilledRectangle(RenderConfig.WindowWidth / 2f, CardSwipeConfig.TrackY, 600f, 80f, 0f, Color.BLACK)

      val cardX = CardSwipeConfig.TrackStartX + swipe.progress * (CardSwipeConfig.TrackEndX - CardSwipeConfig.TrackStartX)
      val cardColor = if (swipe.isComplete) Color.GREEN else Color.YELLOW
      g.drawFilledRectangle(cardX, CardSwipeConfig.TrackY, CardSwipeConfig.CardWidth, CardSwipeConfig.CardHeight, 0f, cardColor)

    case _ =>
  }

  def dispose(): Unit = batch.dispose()
}