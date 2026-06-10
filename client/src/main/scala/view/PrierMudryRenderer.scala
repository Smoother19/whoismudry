package view

import ch.hevs.gdx2d.lib.GdxGraphics
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.{Color, OrthographicCamera, Texture}
import config.RenderConfig
import model.Task
import model.tasks.PrierMudry
import app.tasks.{TaskGame, PrierMudryTask}

class PrierMudryRenderer extends TaskRenderer {

  private val batch = new SpriteBatch()
  private val buttonTexture = new Texture("tasksIcons/prierMudrybutton.png")
  private val buttonWidth = 50f
  private val buttonHeight = 32f
  private val backgroundTexture = new Texture("tasksIcons/saint_mudry.png")
  private val characterTexture = new Texture("tasksIcons/crewmate_back.png")

  def renderMapButton(camera: OrthographicCamera, tasks: Seq[Task]): Unit = {
    batch.setProjectionMatrix(camera.combined)
    batch.begin()
    for (task <- tasks) {
      if (task.taskType == PrierMudry) {
        batch.draw(buttonTexture,
          task.position.x - (buttonWidth / 2),
          task.position.y - (buttonHeight / 2),
          buttonWidth, buttonHeight)
      }
    }
    batch.end()
  }

  def render(g: GdxGraphics, task: TaskGame): Unit = task match {
    case prierMudry: PrierMudryTask =>
      val centerX = RenderConfig.WindowWidth / 2f
      val centerY = RenderConfig.WindowHeight / 2f

      batch.begin()

      val bgW = backgroundTexture.getWidth.toFloat
      val bgH = backgroundTexture.getHeight.toFloat
      batch.draw(backgroundTexture, centerX - (bgW / 2f), centerY - (bgH / 2f), bgW, bgH)

      val charW = characterTexture.getWidth.toFloat
      val charH = characterTexture.getHeight.toFloat
      batch.draw(characterTexture, centerX - (charW / 2f), (centerY - 60f) - (charH / 2f), charW, charH)

      batch.end()

      g.drawFilledRectangle(centerX, RenderConfig.WindowHeight - 60f, 320f, 50f, 0f, new Color(0, 0, 0, 0.6f))

      if (prierMudry.isComplete) {
        g.drawString(centerX - 75f, RenderConfig.WindowHeight - 55f, "Tache terminee !")
      } else {
        g.drawString(centerX - 95f, RenderConfig.WindowHeight - 55f, s"Prier le Mudry : ${prierMudry.remainingSeconds}s")
      }

      g.drawString(centerX - 120f, 50f, "Appuyez sur X pour quitter")

    case _ =>
  }


  def dispose(): Unit = {
    batch.dispose()
    buttonTexture.dispose()
    backgroundTexture.dispose()
    characterTexture.dispose()
  }
}