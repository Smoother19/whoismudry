package view

import ch.hevs.gdx2d.lib.GdxGraphics
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.utils.Disposable
import model.{PlayerId, World}

class MiniMapRenderer() extends Disposable {

  def render(g: GdxGraphics, world: World, localPlayerId: PlayerId): Unit = {
    val cam = g.getCamera
    val zoom = cam.zoom
    val viewWidth = cam.viewportWidth * zoom
    val viewHeight = cam.viewportHeight * zoom

    val screenTileSize = 3f
    val worldTileSize = screenTileSize * zoom

    val miniMapWidth = world.tileMap.widthInTiles * worldTileSize
    val miniMapHeight = world.tileMap.heightInTiles * worldTileSize


    val screenMargin = 20f
    val worldMargin = screenMargin * zoom

    val topRightX = cam.position.x + (viewWidth / 2f)
    val topRightY = cam.position.y + (viewHeight / 2f)


    val startX = topRightX - miniMapWidth - worldMargin
    val startY = topRightY - miniMapHeight - worldMargin


    val centerX = startX + (miniMapWidth / 2f)
    val centerY = startY + (miniMapHeight / 2f)
    g.drawFilledRectangle(centerX, centerY, miniMapWidth, miniMapHeight, 0f, new Color(0.00f, 0.00f, 0.00f, 0.6f))


    for (x <- 0 until world.tileMap.widthInTiles) {
      for (y <- 0 until world.tileMap.heightInTiles) {
        if (world.tileMap.isWallAtTile(x, y)) {
          val tileX = startX + (x * worldTileSize) + (worldTileSize / 2f)
          val tileY = startY + (y * worldTileSize) + (worldTileSize / 2f)
          g.drawFilledRectangle(tileX, tileY, worldTileSize, worldTileSize, 0f, Color.GRAY)
        }
      }
    }


    world.players.foreach { case (id, state) =>
      // calcule la position du joueur par rapport à la taille de la map
      val ratioX = state.position.x / world.tileMap.pixelWidth
      val ratioY = state.position.y / world.tileMap.pixelHeight

      val playerX = startX + (ratioX * miniMapWidth)
      val playerY = startY + (ratioY * miniMapHeight)


      val worldRadius = 4f * zoom

      if (id == localPlayerId) {
        g.drawFilledCircle(playerX, playerY, worldRadius, Color.CYAN)
      } else {
        g.drawFilledCircle(playerX, playerY, worldRadius, Color.RED)
      }
    }
  }

  override def dispose(): Unit = {}
}