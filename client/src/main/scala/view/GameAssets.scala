package view

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver
import com.badlogic.gdx.maps.tiled.{TiledMap, TmxMapLoader}
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.utils.Disposable
import config.RenderConfig

class GameAssets extends Disposable{
  val manager: AssetManager = new AssetManager()

  private val MAP_PATH = RenderConfig.MapAssetPath
  private val CREWMATE_TEXURE_PATH = RenderConfig.CrewmateTexturePath

  def loadAll(): Unit = {
    manager.setLoader(classOf[TiledMap], new TmxMapLoader(new InternalFileHandleResolver))

    manager.load(MAP_PATH, classOf[TiledMap])
    manager.load(CREWMATE_TEXURE_PATH, classOf[Texture])
  }

  def updateLoading(): Boolean = {
    manager.update()
  }

  def getMap(): TiledMap ={
    manager.get(MAP_PATH, classOf[TiledMap])
  }

  def getCrewmateTexture(): Texture = {
    manager.get(CREWMATE_TEXURE_PATH, classOf[Texture])
  }

  override def dispose(): Unit = {
    manager.dispose()
  }
}
