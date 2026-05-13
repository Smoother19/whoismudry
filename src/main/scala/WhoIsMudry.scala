import ch.hevs.gdx2d.desktop.PortableApplication
import ch.hevs.gdx2d.lib.GdxGraphics
import com.badlogic.gdx.graphics.{GL20, OrthographicCamera, Texture, Pixmap}
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.Gdx

class WhoIsMudry extends PortableApplication(Gamecfg.WINDOWLENGTH, Gamecfg.WINDOWWIDTH){

  val assets = new GameAssets()
  val levelManager = new LevelManager()
  var player: Player = _
  var visionMask: Texture = _

  override def onInit(): Unit = {
    setTitle("WhoIsMudry - 2026 game")

    assets.loadAll()
    assets.manager.finishLoading()

    val loadedMap = assets.getMap()
    levelManager.load(loadedMap)

    val startX = levelManager.mapPixelWidth / 2
    val startY = levelManager.mapPixelHeight / 2
    player = new Player(assets.getCrewmateTexture(), startX, startY, levelManager)

    visionMask = createVisionMask(Gamecfg.VISIONMASK_RADIUS)
  }

  private def createVisionMask(radius: Int): Texture = {
    val size = Gamecfg.DARKMASK
    val pixmap = new Pixmap(size, size, Pixmap.Format.RGBA8888)
    val maxDarkness = Gamecfg.MAXDARKNESS

    for (x <- 0 until size) {
      for( y <- 0 until size) {
        val dx = x - size / 2
        val dy = y - size / 2
        val dist = Math.sqrt(dx * dx + dy * dy).toFloat

        if (dist >= radius) {
          pixmap.setColor(0f, 0f, 0f, maxDarkness)
        } else {
          val alpha = (dist / radius) * maxDarkness
          pixmap.setColor(0f, 0f, 0f, alpha)
        }
        pixmap.drawPixel(x, y)
      }
    }

    val texture = new Texture(pixmap)
    pixmap.dispose()
    texture
  }
  override def onGameLogicUpdate(): Unit = {
    super.onGameLogicUpdate()

    var dx = 0.0f
    var dy = 0.0f

    if (Gdx.input.isKeyPressed(Keys.D)) dx += 1.0f
    if (Gdx.input.isKeyPressed(Keys.A))  dx -= 1.0f
    if (Gdx.input.isKeyPressed(Keys.W)) dy += 1.0f
    if (Gdx.input.isKeyPressed(Keys.S)) dy -= 1.0f

    val deltaTime = Gdx.graphics.getDeltaTime
    player.update(deltaTime, dx, dy)
  }

  override def onGraphicRender(g: GdxGraphics): Unit = {
    g.clear()
    g.zoom(0.25f)
    g.moveCamera(player.x.toInt, player.y.toInt, levelManager.mapPixelWidth, levelManager.mapPixelHeight)

    val camera: OrthographicCamera = g.getCamera

    levelManager.render(camera)
    onGameLogicUpdate()
    player.render(g)
    val maskOffset = Gamecfg.DARKMASK / 2
    val playerCenterX = player.x + (Gamecfg.PLAYERSPRITEWIDTH / 2)
    val playerCenterY = player.y + (Gamecfg.PLAYERSPRITEHEIGHT / 2)
    g.draw(visionMask, playerCenterX - maskOffset, playerCenterY - maskOffset)
  }

  override def onDispose(): Unit = {
    assets.dispose()
    levelManager.dispose()
    super.onDispose()
  }
}