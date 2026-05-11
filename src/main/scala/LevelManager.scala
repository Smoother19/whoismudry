import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.maps.tiled.{TiledMap, TiledMapTileLayer}
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer

trait GameLevel {
  def render(camera: OrthographicCamera): Unit
  def collisions(xPixel: Float, yPixel: Float): Boolean
  def state(): Unit
  def interact(): Unit
  def update(): Unit
  def load(loadedMap: TiledMap): Unit
  def dimension(): Unit
  def worldToGrid(pixel: Float): Int
  def dispose(): Unit
}

class LevelManager extends GameLevel {

  var map: TiledMap = _
  var mapRenderer: OrthogonalTiledMapRenderer = _

  var mapPixelWidth: Float = 0f
  var mapPixelHeight: Float = 0f

  override def load(loadedMap: TiledMap): Unit = {
    this.map = loadedMap
    this.mapRenderer = new OrthogonalTiledMapRenderer(this.map)

    val prop = map.getProperties

    val mapWidthInTiles = prop.get("width", classOf[Int])
    val mapHeightInTiles = prop.get("height", classOf[Int])
    val tileWidth = prop.get("tilewidth", classOf[Int])
    val tileHeight = prop.get("tileheight", classOf[Int])

    mapPixelWidth = (mapWidthInTiles * tileWidth).toFloat
    mapPixelHeight = (mapHeightInTiles * tileHeight).toFloat
  }

  override def render(camera: OrthographicCamera): Unit = {
    if (mapRenderer != null) {
      mapRenderer.setView(camera)
      mapRenderer.render()
    }
  }

  override def collisions(xPixel: Float, yPixel: Float): Boolean = {
    if (map == null) return false

    val tileX = (xPixel / Gamecfg.TILESIZE).toInt
    val tileY = (yPixel / Gamecfg.TILESIZE).toInt

    val layer = map.getLayers.get(Gamecfg.MAP_WALLLAYER)

    if (layer == null || !layer.isInstanceOf[TiledMapTileLayer]) {
      return false
    }

    val wallLayer = layer.asInstanceOf[TiledMapTileLayer]
    val cell = wallLayer.getCell(tileX, tileY)

    cell != null
  }

  override def dispose(): Unit = {
    mapRenderer.dispose()
    map.dispose()
  }

  override def worldToGrid(pixel: Float): Int = {
    (pixel / Gamecfg.TILESIZE).toInt
  }

  override def state(): Unit = {}
  override def interact(): Unit = {}
  override def update(): Unit = {}
  override def dimension(): Unit = {}
}