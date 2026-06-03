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
  private val username = "smooth"

  private var levelRenderer: LevelRenderer = _
  private var playerRenderer: PlayerRenderer = _
  private var visionMaskRenderer: VisionMaskRenderer = _
  private var miniMapRenderer: MiniMapRenderer = _
  private var votingPhaseRenderer: VotingPhaseRenderer = _
  private var cardSwipeRenderer: CardSwipeRenderer = _

  override def onInit(): Unit = {
    setTitle("WhoIsMudry - 2026 game")
    assets.loadAll()
    assets.manager.finishLoading()

    val tiledMap = assets.getMap()
    val tileMap = TiledMapLoader.fromTiledMap(tiledMap, RenderConfig.WallLayerName)

    GameManager.start(tileMap, username)

    levelRenderer = new LevelRenderer(tiledMap)
    playerRenderer = new PlayerRenderer(assets.getCrewmateTexture())
    visionMaskRenderer = new VisionMaskRenderer()
    miniMapRenderer =  new MiniMapRenderer()
    votingPhaseRenderer = new VotingPhaseRenderer()
    cardSwipeRenderer = new CardSwipeRenderer()
  }

  override def onGraphicRender(g: GdxGraphics): Unit = {
    GameManager.updateLocalInput(KeyboardInput.poll())
    val currentWorld = GameManager.currentWorld

    g.clear()

    val localId = GameManager.currentLocalId
    val playerCenter =
      if (localId != null) currentWorld.players.get(localId) match {
        case Some(p) => Vec2(p.position.x + RenderConfig.PlayerSpriteWidth / 2f, p.position.y + RenderConfig.PlayerSpriteHeight / 2f)
        case None    => Vec2(currentWorld.tileMap.pixelWidth / 2f, currentWorld.tileMap.pixelHeight / 2f)
      } else Vec2(currentWorld.tileMap.pixelWidth / 2f, currentWorld.tileMap.pixelHeight / 2f)

    g.zoom(0.25f)
    g.moveCamera(playerCenter.x.toInt, playerCenter.y.toInt, currentWorld.tileMap.pixelWidth, currentWorld.tileMap.pixelHeight)

    levelRenderer.render(g.getCamera)

    val dt = Gdx.graphics.getDeltaTime
    currentWorld.players.values.foreach { state =>
      playerRenderer.render(g, state, dt)
    }

    visionMaskRenderer.renderAround(g, playerCenter)
    miniMapRenderer.render(g, currentWorld, localId)
  }

  override def onDispose(): Unit = {
    GameManager.stop()
    levelRenderer.dispose()
    visionMaskRenderer.dispose()
    assets.dispose()
    votingPhaseRenderer.dispose()
    super.onDispose()
  }
}