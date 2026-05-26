package app

import ch.hevs.gdx2d.desktop.PortableApplication
import ch.hevs.gdx2d.lib.GdxGraphics
import com.badlogic.gdx.Gdx
import config.RenderConfig
import input.KeyboardInput
import loader.TiledMapLoader
import model._
import view._

class WhoIsMudry extends PortableApplication(RenderConfig.WindowWidth, RenderConfig.WindowHeight) {

  private val assets = new GameAssets()
  private val localPlayerId = PlayerId("local")

  private var levelRenderer: LevelRenderer = _
  private var playerRenderer: PlayerRenderer = _
  private var visionMaskRenderer: VisionMaskRenderer = _

  override def onInit(): Unit = {
    setTitle("WhoIsMudry - 2026 game")
    assets.loadAll()
    assets.manager.finishLoading()

    val tiledMap = assets.getMap()
    val tileMap = TiledMapLoader.fromTiledMap(tiledMap, RenderConfig.WallLayerName)

    GameManager.start(tileMap, localPlayerId)

    levelRenderer = new LevelRenderer(tiledMap)
    playerRenderer = new PlayerRenderer(assets.getCrewmateTexture())
    visionMaskRenderer = new VisionMaskRenderer()
  }

  override def onGraphicRender(g: GdxGraphics): Unit = {
    GameManager.updateLocalInput(KeyboardInput.poll())
    val currentWorld = GameManager.currentWorld

    val localPlayer = currentWorld.players(localPlayerId)
    val playerCenter = Vec2(localPlayer.position.x + RenderConfig.PlayerSpriteWidth / 2f, localPlayer.position.y + RenderConfig.PlayerSpriteHeight / 2f)

    g.clear()
    g.zoom(0.25f)
    g.moveCamera(playerCenter.x.toInt, playerCenter.y.toInt, currentWorld.tileMap.pixelWidth, currentWorld.tileMap.pixelHeight)

    levelRenderer.render(g.getCamera)

    val dt = Gdx.graphics.getDeltaTime
    currentWorld.players.values.foreach { state =>
      playerRenderer.render(g, state, dt)
    }

    visionMaskRenderer.renderAround(g, playerCenter)
  }

  override def onDispose(): Unit = {
    GameManager.stop()
    levelRenderer.dispose()
    visionMaskRenderer.dispose()
    assets.dispose()
    super.onDispose()
  }
}