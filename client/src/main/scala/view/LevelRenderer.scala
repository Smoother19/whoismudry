package view

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.utils.Disposable

class LevelRenderer(tileMap: TiledMap) extends Disposable{
  private val mapRenderer: OrthogonalTiledMapRenderer = new OrthogonalTiledMapRenderer(tileMap)

  def render(camera: OrthographicCamera): Unit = {
    mapRenderer.setView(camera)
    mapRenderer.render()
  }

  override def dispose(): Unit ={
    mapRenderer.dispose()
  }
}
