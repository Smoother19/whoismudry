package app

import ch.hevs.gdx2d.desktop.PortableApplication
import ch.hevs.gdx2d.lib.GdxGraphics
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.Gdx
import config.RenderConfig
import loader.TiledMapLoader
import model._
import view._
import app.ClientState._
import app.tasks.{TaskGame, TaskFactory}
import input.{KeyboardInput, MouseInput}

class WhoIsMudry extends PortableApplication(RenderConfig.WindowWidth, RenderConfig.WindowHeight) {

  private val assets = new GameAssets()

  private var levelRenderer: LevelRenderer = _
  private var playerRenderer: PlayerRenderer = _
  private var visionMaskRenderer: VisionMaskRenderer = _
  private var miniMapRenderer: MiniMapRenderer = _
  private var meetingRenderer: MeetingRenderer = _
  private var cardSwipeRenderer: CardSwipeRenderer = _
  private var emergencyButtonRenderer: EmergencyButtonRenderer = _
  private var lobbyRenderer: LobbyRenderer = _
  private var localState: LocalStateManager = _

  private var currentTask: TaskGame = _
  private var currentTaskRenderer: TaskRenderer = _

  override def onInit(): Unit = {
    setTitle("WhoIsMudry - 2026 game")
    assets.loadAll()
    assets.manager.finishLoading()

    val tiledMap = assets.getMap()
    val tileMap = TiledMapLoader.fromTiledMap(tiledMap, RenderConfig.WallLayerName)

    localState = new LocalStateManager(tileMap)

    levelRenderer = new LevelRenderer(tiledMap)
    playerRenderer = new PlayerRenderer(assets.getCrewmateTexture())
    visionMaskRenderer = new VisionMaskRenderer()
    miniMapRenderer =  new MiniMapRenderer()
    meetingRenderer = new MeetingRenderer()
    cardSwipeRenderer = new CardSwipeRenderer()
    emergencyButtonRenderer = new EmergencyButtonRenderer()
    lobbyRenderer = new LobbyRenderer()
  }

  override def onGraphicRender(g: GdxGraphics): Unit = {
    g.clear()

    GameManager.currentPhase match {
      case GamePhase.Discussion(_) | GamePhase.Voting(_, _) =>
        renderMeeting(g)

      case GamePhase.Lobby(remaining) =>
        g.zoom(1f)
        g.moveCamera(RenderConfig.WindowWidth / 2, RenderConfig.WindowHeight / 2, RenderConfig.WindowWidth, RenderConfig.WindowHeight)
        lobbyRenderer.render(g, GameManager.currentWorld, remaining)

      case GamePhase.Playing =>
        renderLocalState(g)
    }
  }

  private def renderMeeting(g: GdxGraphics): Unit = {
    localState.closeTask()

    g.zoom(1f)
    g.moveCamera(RenderConfig.WindowWidth / 2, RenderConfig.WindowHeight / 2,
      RenderConfig.WindowWidth, RenderConfig.WindowHeight)

    val options = meetingRenderer.render(g, GameManager.currentWorld, GameManager.currentPhase)

    if (Gdx.input.justTouched()) {
      val mouse = MouseInput.poll()
      options.find(_.contains(mouse.x, mouse.y)).foreach { opt =>
        GameManager.submitVote(opt.playerId)
      }
    }
  }

  private def renderLocalState(g: GdxGraphics): Unit = {
    localState.currentState match {
      case MainMenu(userName) =>
        val updated = KeyboardInput.pollUserNameInput(userName)
        localState.updateTypedName(updated)
        if (Gdx.input.isKeyJustPressed(Keys.ENTER)) localState.confirmName()

        g.drawString(RenderConfig.WindowWidth / 2f - 150f, RenderConfig.WindowHeight / 2f + 50f, "whoIsMudry")
        g.drawString(RenderConfig.WindowWidth / 2f - 150f, RenderConfig.WindowHeight / 2f, "Pseudo : " + updated)
        g.drawString(RenderConfig.WindowWidth / 2f - 150f, RenderConfig.WindowHeight / 2f - 50, "Press Enter to join game")

      case FreeRoam =>
        GameManager.updateLocalInput(KeyboardInput.poll())
        val currentWorld = GameManager.currentWorld

        val localId = GameManager.currentLocalId
        val playerCenter =
          if (localId != null) currentWorld.players.get(localId) match {
            case Some(p) => Vec2(p.position.x + RenderConfig.PlayerSpriteWidth / 2f, p.position.y + RenderConfig.PlayerSpriteHeight / 2f)
            case None => Vec2(currentWorld.tileMap.pixelWidth / 2f, currentWorld.tileMap.pixelHeight / 2f)
          } else Vec2(currentWorld.tileMap.pixelWidth / 2f, currentWorld.tileMap.pixelHeight / 2f)

        g.zoom(0.25f)
        g.moveCamera(playerCenter.x.toInt, playerCenter.y.toInt, currentWorld.tileMap.pixelWidth, currentWorld.tileMap.pixelHeight)

        levelRenderer.render(g.getCamera)
        val emergencyButtonPos = Vec2(currentWorld.tileMap.pixelWidth / 2f, currentWorld.tileMap.pixelHeight / 2f + 20f)
        cardSwipeRenderer.renderMapButton(g.getCamera, GameManager.tasks)
        emergencyButtonRenderer.render(g.getCamera, emergencyButtonPos)

        val localIsDead = localId != null && currentWorld.players.get(localId).exists(_.isDead)

        val dt = Gdx.graphics.getDeltaTime
        currentWorld.players.values.foreach { state =>
          if (localIsDead || !state.isDead) {
            playerRenderer.render(g, state, dt)
          }
        }

        visionMaskRenderer.renderAround(g, playerCenter)
        miniMapRenderer.render(g, currentWorld, localId)

        if (GameManager.currentLocalInput.interact && localId != null) {
          val dx = emergencyButtonPos.x - playerCenter.x
          val dy = emergencyButtonPos.y - playerCenter.y
          val distanceToButton = math.sqrt(dx * dx + dy * dy)

          if (distanceToButton < config.GameplayConfig.InteractionRadius) {
            GameManager.callMeeting()
          } else if (GameManager.currentLocalRole == Role.Mudry) {
            GameManager.attemptKill()
          } else {
            GameManager.taskNearLocalPlayer(playerCenter).foreach { task =>
              val (logic, renderer) = TaskFactory.create(task.taskType)
              currentTask = logic
              currentTaskRenderer = renderer
              localState.openTask(task.taskType)
            }
          }
        }

      case DoingTask(_) =>
        g.zoom(1f)
        g.moveCamera(RenderConfig.WindowWidth / 2, RenderConfig.WindowHeight / 2,
          RenderConfig.WindowWidth, RenderConfig.WindowHeight)
        GameManager.updateLocalInput(PlayerInput.none)

        currentTask.update(MouseInput.poll())
        currentTaskRenderer.render(g, currentTask)

        if (currentTask.isComplete || Gdx.input.isKeyJustPressed(Keys.X)) {
          localState.closeTask()
        }
    }
  }


  override def onDispose(): Unit = {
    GameManager.stop()
    levelRenderer.dispose()
    visionMaskRenderer.dispose()
    assets.dispose()
    meetingRenderer.dispose()
    emergencyButtonRenderer.dispose()
    playerRenderer.dispose()
    super.onDispose()
  }
}