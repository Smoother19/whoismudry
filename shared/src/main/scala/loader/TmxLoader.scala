package loader

import model.TileMap
import scala.xml.XML

object TmxLoader {

  def load(resourcePath: String, wallLayerName: String): TileMap = {
    val stream = getClass.getResourceAsStream(resourcePath)
    if (stream == null) {
      throw new RuntimeException("Map not found: " + resourcePath)
    }

    val map = XML.load(stream)

    val width = (map \ "@width").text.toInt
    val height = (map \ "@height").text.toInt
    val tileSize = (map \ "@tilewidth").text.toInt

    val layers = map \ "layer"
    var wallLayer = layers.head
    for (layer <- layers) {
      val name = (layer \ "@name").text
      if (name == wallLayerName) {
        wallLayer = layer
      }
    }

    val csv = (wallLayer \ "data").text
    val parts = csv.split(",")

    val walls = Array.ofDim[Boolean](width, height)
    var i = 0
    for (part <- parts) {
      val clean = part.trim
      if (clean != "") {
        val gid = clean.toLong
        val x = i % width
        val y = height - 1 - (i / width)
        walls(x)(y) = gid != 0
        i = i + 1
      }
    }

    new TileMap(width, height, tileSize, walls)
  }
}