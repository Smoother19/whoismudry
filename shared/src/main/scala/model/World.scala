package model

import config.GameplayConfig

case class World(val tileMap: TileMap, val players: Map[PlayerId, PlayerState]) {

  // apdate the simulation by one step and check all the collision with all the players state (only on position level)
  def step(inputs: Map[PlayerId, PlayerInput], deltaTime: Float): World = {
    var newPlayers = Map[PlayerId, PlayerState]()

    for ((id, state) <- players) {
      val input = inputs.getOrElse(id, PlayerInput.none)
      val newState = updatePlayer(state, input, deltaTime)
      newPlayers = newPlayers + (id -> newState)
    }

    World(tileMap, newPlayers)
  }

  private def updatePlayer(state: PlayerState, input: PlayerInput, dt: Float): PlayerState = {
    if (!input.isMoving) {
      state.copy(isMoving = false)
    } else {
      val newFacing = directionFromInput(input)

      val displacement = Vec2(input.dx, input.dy) * (GameplayConfig.PlayerSpeed * dt)

      val triedX = state.position + Vec2(displacement.x, 0f)
      val posAfterX = if (collides(triedX)) state.position else triedX

      val triedY = posAfterX + Vec2(0f, displacement.y)
      val posAfterY = if (collides(triedY)) posAfterX else triedY

      state.copy(position = posAfterY, facing = newFacing, isMoving = true)
    }
  }

  private def collides(pos: Vec2): Boolean = {
    val corners = GameplayConfig.PlayerHitbox.corners(pos)

    for (corner <- corners) {
      if (tileMap.isWallAtPixel(corner.x, corner.y)) {
        return true
      }
    }
    false
  }

  private def directionFromInput(input: PlayerInput): Direction = {
    if (input.dx > 0f) {
      Direction.Right
    }
    else if (input.dx < 0f) {
      Direction.Left
    }
    else if (input.dy > 0f) {
      Direction.Up
    }
    else {
      Direction.Down
    }
  }
}