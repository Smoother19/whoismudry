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
  private var miniMapRenderer: MiniMapRenderer = _
  private var votingPhaseRenderer: VotingPhaseRenderer = _
  private var cardSwipeRenderer: CardSwipeRenderer = _

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
    miniMapRenderer =  new MiniMapRenderer()
    votingPhaseRenderer = new VotingPhaseRenderer()
    cardSwipeRenderer = new CardSwipeRenderer()
  }

  override def onGraphicRender(g: GdxGraphics): Unit = {
    GameManager.updateLocalInput(KeyboardInput.poll())
    val currentWorld = GameManager.currentWorld

    g.clear()

    val localPlayer = currentWorld.players(localPlayerId)
    val playerCenter = Vec2(
      localPlayer.position.x + RenderConfig.PlayerSpriteWidth / 2f,
      localPlayer.position.y + RenderConfig.PlayerSpriteHeight / 2f
    )

    g.zoom(0.25f)
    g.moveCamera(playerCenter.x.toInt, playerCenter.y.toInt, currentWorld.tileMap.pixelWidth, currentWorld.tileMap.pixelHeight)

    levelRenderer.render(g.getCamera)

    val dt = Gdx.graphics.getDeltaTime
    currentWorld.players.values.foreach { state =>
      playerRenderer.render(g, state, dt)
    }

    val taskPos = GameManager.cardTaskPos
    g.drawFilledCircle(taskPos.x, taskPos.y, 20f, com.badlogic.gdx.graphics.Color.BLUE)
    g.drawString(taskPos.x - 10, taskPos.y + 30, "ADMIN")

    val btnPos = GameManager.emergencyButtonPos
    g.drawFilledCircle(btnPos.x, btnPos.y, 20f, com.badlogic.gdx.graphics.Color.RED)
    g.drawString(btnPos.x - 20, btnPos.y + 30, "EMERGENCY")


    miniMapRenderer.render(g, currentWorld, localPlayerId)

    GameManager.currentState match {

      case ClientState.Playing =>
        visionMaskRenderer.renderAround(g, playerCenter)
        cardSwipeRenderer.reset()

      case ClientState.Voting(timeRemaining) =>
        g.zoom(1f)
        g.moveCamera(RenderConfig.WindowWidth / 2, RenderConfig.WindowHeight / 2, RenderConfig.WindowWidth, RenderConfig.WindowHeight)

        g.drawFilledRectangle(RenderConfig.WindowWidth / 2f, RenderConfig.WindowHeight / 2f, RenderConfig.WindowWidth.toFloat, RenderConfig.WindowHeight.toFloat, 0f, new com.badlogic.gdx.graphics.Color(0, 0, 0, 0.7f))

        votingPhaseRenderer.render(g, currentWorld, timeRemaining)

      case ClientState.DoingTask(ClientState.AdminCard) =>
        g.zoom(1f)
        g.moveCamera(RenderConfig.WindowWidth / 2, RenderConfig.WindowHeight / 2, RenderConfig.WindowWidth, RenderConfig.WindowHeight)

        g.drawFilledRectangle(RenderConfig.WindowWidth / 2f, RenderConfig.WindowHeight / 2f, RenderConfig.WindowWidth.toFloat, RenderConfig.WindowHeight.toFloat, 0f, new com.badlogic.gdx.graphics.Color(0, 0, 0, 0.7f))

        cardSwipeRenderer.render(g)
    }
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