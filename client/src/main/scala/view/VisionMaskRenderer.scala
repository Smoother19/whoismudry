package view

import ch.hevs.gdx2d.lib.GdxGraphics
import com.badlogic.gdx.graphics.{Pixmap, Texture}
import com.badlogic.gdx.utils.Disposable
import config.{GameplayConfig, RenderConfig}
import model.Vec2

class VisionMaskRenderer extends Disposable{
  private val texture: Texture = createMask()

  private def createMask(): Texture = {
    val size = RenderConfig.VisionMaskTextureSize
    val pixmap = new Pixmap(size, size, Pixmap.Format.RGBA8888)
    val maxDarkness = RenderConfig.MaxDarkness

    for (x <- 0 until size) {
      for (y <- 0 until size) {
        val dx = x - size / 2
        val dy = y - size / 2
        val dist = Math.sqrt(dx * dx + dy * dy).toFloat

        if (dist >= GameplayConfig.VisionRadius) {
          pixmap.setColor(0f, 0f, 0f, maxDarkness)
        } else {
          val alpha = (dist / GameplayConfig.VisionRadius) * maxDarkness
          pixmap.setColor(0f, 0f, 0f, alpha)
        }
        pixmap.drawPixel(x, y)
      }

    }
    val tex = new Texture(pixmap)
    pixmap.dispose()
    tex
  }

  def renderAround(g: GdxGraphics, center: Vec2): Unit = {
    val half = RenderConfig.VisionMaskTextureSize / 2
    g.draw(texture, center.x - half, center.y - half)
  }

  override def dispose(): Unit = {
    texture.dispose()
  }
}
