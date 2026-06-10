package view

import ch.hevs.gdx2d.lib.GdxGraphics
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.{Color, OrthographicCamera, Texture}
import config.RenderConfig
import model.Task
import model.tasks.Question
import app.tasks.{TaskGame, QuestionTask}

class QuestionRenderer extends TaskRenderer {

  private val batch = new SpriteBatch()
  private val buttonTexture = new Texture("tasksIcons/question.png")
  private val buttonWidth = 50f
  private val buttonHeight = 32f

  def renderMapButton(camera: OrthographicCamera, tasks: Seq[Task]): Unit = {
    batch.setProjectionMatrix(camera.combined)
    batch.begin()
    for (task <- tasks) {
      if (task.taskType == Question) {
        batch.draw(buttonTexture,
          task.position.x - (buttonWidth / 2),
          task.position.y - (buttonHeight / 2),
          buttonWidth, buttonHeight)
      }
    }
    batch.end()
  }

  override def render(g: GdxGraphics, task: TaskGame): Unit = task match {
    case question: QuestionTask =>
      val centerX = RenderConfig.WindowWidth / 2f
      val centerY = RenderConfig.WindowHeight / 2f

      g.drawFilledRectangle(centerX, centerY, 600f, 400f, 0f, new Color(0.1f, 0.1f, 0.15f, 1f))
      g.drawFilledRectangle(centerX, centerY + 180f, 600f, 40f, 0f, new Color(0.2f, 0.2f, 0.3f, 1f))
      g.drawString(centerX - 50f, centerY + 175f, "Terminal Admin")

      if (question.isComplete) {
        g.drawString(centerX - 100f, centerY, "ACCES AUTORISE - Tache completee !")
      } else {
        g.drawString(centerX - 120f, centerY + 50f, question.getQuestionText)
        g.drawFilledRectangle(centerX, centerY - 20f, 200f, 50f, 0f, Color.BLUE)
        g.drawString(centerX - 70f, centerY - 25f, "CLIQUEZ ICI POUR TAPER")
      }

      g.drawString(centerX - 120f, centerY - 150f, "Appuyez sur X pour quitter")

    case _ =>
  }

  def dispose(): Unit = {
    batch.dispose()
    buttonTexture.dispose()
  }
}