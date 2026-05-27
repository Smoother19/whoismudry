package model

// Logical representation of the game map, independent from libgdx to have control on it and reuse it later for the servers
// Stores walls as a 2D boolean array for fast collision lookups
class TileMap(val widthInTiles: Int, val heightInTiles: Int, val tileSize: Int, private val walls: Array[Array[Boolean]]) {
  def pixelWidth: Float = {
    (widthInTiles * tileSize).toFloat
  }

  def pixelHeight: Float ={
    (heightInTiles * tileSize).toFloat
  }

  def isWallAtTile(tileX: Int, tileY: Int): Boolean = {
    if (tileX < 0 || tileX >= widthInTiles || tileY < 0 || tileY >= heightInTiles){
      return true
    }else{
      walls(tileX)(tileY)
    }
  }

  def isWallAtPixel(pixelX: Float, pixelY: Float): Boolean = {
    val tileX = (pixelX / tileSize).toInt
    val tileY = (pixelY / tileSize).toInt

    isWallAtTile(tileX, tileY)
  }
}