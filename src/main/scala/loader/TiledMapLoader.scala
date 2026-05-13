package loader

import model.TileMap
import com.badlogic.gdx.maps.tiled.{TiledMap, TiledMapTileLayer}

// bridge between model/TilemMap and GDX2D
object TiledMapLoader {

  def fromTiledMap(tiledMap: TiledMap, wallLayerName: String): TileMap = {

    // Read map properties
    val props = tiledMap.getProperties
    val widthInTiles  = props.get("width",      classOf[Int])
    val heightInTiles = props.get("height",     classOf[Int])
    val tileWidth     = props.get("tilewidth",  classOf[Int])

    // Get WalLayer
    val wallLayer = tiledMap.getLayers
      .get(wallLayerName)
      .asInstanceOf[TiledMapTileLayer]

    // 2d Array of bolean
    val walls = Array.ofDim[Boolean](widthInTiles, heightInTiles)
    for (x <- 0 until widthInTiles; y <- 0 until heightInTiles) {
      walls(x)(y) = wallLayer.getCell(x, y) != null
    }

    new TileMap(widthInTiles, heightInTiles, tileWidth, walls)
  }
}
