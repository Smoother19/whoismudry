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

  private var logicThread: GameLogicThread = _
  private var levelRenderer: LevelRenderer = _
  private var playerRenderer: PlayerRenderer = _
  private var visionMaskRenderer: VisionMaskRenderer = _
  private var miniMapRenderer: MiniMapRenderer = _

  override def onInit(): Unit = {
    setTitle("WhoIsMudry - 2026 game")
    assets.loadAll()
    assets.manager.finishLoading()

    val tiledMap = assets.getMap()
    val tileMap = TiledMapLoader.fromTiledMap(tiledMap, RenderConfig.WallLayerName)

    val startPos = Vec2(tileMap.pixelWidth / 2, tileMap.pixelHeight / 2)
    val initialPlayer = PlayerState(localPlayerId, startPos, Direction.Down, false)

    val initialWorld = World(tileMap, Map(localPlayerId -> initialPlayer))

    logicThread = new GameLogicThread(initialWorld, localPlayerId)
    logicThread.start()

    levelRenderer = new LevelRenderer(tiledMap)
    playerRenderer = new PlayerRenderer(assets.getCrewmateTexture())
    visionMaskRenderer = new VisionMaskRenderer()
    miniMapRenderer =  new MiniMapRenderer()
  }

  override def onGraphicRender(g: GdxGraphics): Unit = {
    logicThread.currentInput = KeyboardInput.poll()

    val currentWorld = logicThread.world

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
    miniMapRenderer.render(g, logicThread.world, localPlayerId)
  }

  override def onDispose(): Unit = {
    logicThread.running = false
    logicThread.join()
    levelRenderer.dispose()
    visionMaskRenderer.dispose()
    assets.dispose()
    super.onDispose()
  }
}
