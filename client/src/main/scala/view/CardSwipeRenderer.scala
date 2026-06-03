package view

import app.GameManager
import ch.hevs.gdx2d.lib.GdxGraphics
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.graphics.Color
import config.RenderConfig

class CardSwipeRenderer {

  private val startX = RenderConfig.WindowWidth / 2f - 250f
  private val endX = RenderConfig.WindowWidth / 2f + 250f
  private val trackY = RenderConfig.WindowHeight / 2f

  private var cardX = startX
  private var isDragging = false
  private var showSuccess = false

  def render(g: GdxGraphics): Unit = {
    g.drawFilledRectangle(
      RenderConfig.WindowWidth / 2f,
      RenderConfig.WindowHeight / 2f,
      700f,
      400f,
      0f,
      Color.DARK_GRAY
    )

    g.drawString(
      RenderConfig.WindowWidth / 2f - 120,
      RenderConfig.WindowHeight / 2f + 150,
      "TASK : swipe card"
    )

    g.drawString(
      RenderConfig.WindowWidth / 2f - 150,
      RenderConfig.WindowHeight / 2f - 150,
      "Appuyez sur X pour quitter"
    )

    g.drawFilledRectangle(RenderConfig.WindowWidth / 2f, trackY, 600f, 80f, 0f, Color.BLACK)

    val mouseX = Gdx.input.getX().toFloat
    val mouseY = (RenderConfig.WindowHeight - Gdx.input.getY()).toFloat // Dans libGDX, l'axe Y de la souris est inversé

    if (Gdx.input.isTouched && !showSuccess) {
      if (math.abs(mouseX - cardX) < 60 && math.abs(mouseY - trackY) < 60) {
        isDragging = true
      }

      if (isDragging) {
        cardX = math.min(endX, math.max(startX, mouseX))
      }

    } else {
      if (isDragging) {
        if (cardX >= endX - 20f) {
          showSuccess = true
          //GameManager.completeTask()
        } else {
          cardX = startX
        }
      }
      isDragging = false
    }

    if (Gdx.input.isKeyJustPressed(Keys.X)) {
      reset()
      //GameManager.cancelTask()
    }

    val cardColor = if (showSuccess) Color.GREEN else Color.YELLOW
    g.drawFilledRectangle(cardX, trackY, 100f, 140f, 0f, cardColor)
  }

  def reset(): Unit = {
    cardX = startX
    isDragging = false
    showSuccess = false
  }
}