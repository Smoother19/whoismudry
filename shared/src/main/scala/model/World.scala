package model

import config.GameplayConfig

case class World(tileMap: TileMap, players: Map[PlayerId, PlayerState], phase: GamePhase = GamePhase.Playing, meetingCooldown: Float = 0f) {

  def step(inputs: Map[PlayerId, PlayerInput], deltaTime: Float): World = phase match {

    case GamePhase.Playing =>
      var newPlayers = Map[PlayerId, PlayerState]()
      for ((id, state) <- players) {
        val input = inputs.getOrElse(id, PlayerInput.none)
        newPlayers = newPlayers + (id -> updatePlayer(state, input, deltaTime))
      }
      val newCooldown = math.max(0f, meetingCooldown - deltaTime)
      val moved = copy(players = newPlayers, meetingCooldown = newCooldown)
      moved.checkVictory()

    case GamePhase.Discussion(remaining) =>
      val left = remaining - deltaTime
      if (left <= 0f) copy(phase = GamePhase.Voting(GameplayConfig.VotingDuration, Map.empty))
      else copy(phase = GamePhase.Discussion(left))

    case GamePhase.Voting(remaining, votes) =>
      val left = remaining - deltaTime
      if (left <= 0f) {
        val ejected = mostVoted(votes)
        val newPlayers = ejected match {
          case Some(id) => players.updatedWith(id)(_.map(_.copy(isDead = true)))
          case None     => players
        }
        World(tileMap, newPlayers, GamePhase.Playing, GameplayConfig.MeetingCooldown).checkVictory()
      } else {
        copy(phase = GamePhase.Voting(left, votes))
      }


    case GamePhase.Lobby(remaining) =>
      if (players.size < GameplayConfig.MinPlayersToStart) {
        copy(phase = GamePhase.Lobby(GameplayConfig.LobbyCountdown))
      } else {
        val left = remaining - deltaTime
        if (left <= 0f) assignRoles().copy(phase = GamePhase.Playing)
        else copy(phase = GamePhase.Lobby(left))
      }

    case GamePhase.GameOver(_) =>
      this
  }

  private def assignRoles(): World = {
    val ids = players.keys.toSeq
    if (ids.isEmpty) this
    else {
      val mudryId = ids(scala.util.Random.nextInt(ids.size))
      val newPlayers = players.map { case (id, state) =>
        val role = if (id == mudryId) Role.Mudry else Role.Students
        id -> state.copy(role = role)
      }
      copy(players = newPlayers)
    }
  }

  private def checkVictory(): World = {
    val alivePlayers = players.values.filter(!_.isDead)
    val aliveMudry = alivePlayers.count(_.role == Role.Mudry)
    val aliveStudents = alivePlayers.count(_.role == Role.Students)

    if (aliveMudry == 0) {
      copy(phase = GamePhase.GameOver("Students won"))
    } else if (aliveMudry >= aliveStudents) {
      copy(phase = GamePhase.GameOver("Mudry won"))
    } else {
      this
    }
  }

  def killPlayer(killerId: PlayerId, targetId: PlayerId, now: Long): World = {
    if (phase != GamePhase.Playing) { println("kill refusé: pas en Playing"); return this }
    (players.get(killerId), players.get(targetId)) match {
      case (Some(killer), Some(target)) =>
        println(s"killer role = ${killer.role}, dist OK?")
        killer.role.executeKill(killer, target, now, GameplayConfig.KillCooldown, GameplayConfig.InteractionRadius) match {
          case Some((deadTarget, updatedKiller)) =>
            println("KILL réussi")
            copy(players = players + (killerId -> updatedKiller) + (targetId -> deadTarget))
          case None => println("executeKill a renvoyé None"); this
        }
      case _ => println("killer ou cible introuvable"); this
    }
  }

  def startMeeting(callerId: PlayerId): World = {
    val callerAlive = players.get(callerId).exists(!_.isDead)
    if (callerAlive && meetingCooldown <= 0f) {
      val gatherPoint = Vec2(tileMap.pixelWidth / 2f, tileMap.pixelHeight / 2f)
      val gathered = players.map { case (id, state) =>
        id -> state.copy(position = gatherPoint, isMoving = false)
      }
      World(tileMap, gathered, GamePhase.Discussion(GameplayConfig.DiscussionDuration), GameplayConfig.MeetingCooldown)
    } else this
  }

  def registerVote(voter: PlayerId, target: PlayerId): World = phase match {
    case GamePhase.Voting(remaining, votes) =>
      players.get(voter) match {
        case Some(v) if !v.isDead =>
          copy(phase = GamePhase.Voting(remaining, votes + (voter -> target)))
        case _ => this
      }
    case _ => this
  }

  private def mostVoted(votes: Map[PlayerId, PlayerId]): Option[PlayerId] = {
    if (votes.isEmpty) None
    else {
      val tally = votes.values.groupBy(identity).map { case (id, occ) => (id, occ.size) }
      val maxCount = tally.values.max
      val top = tally.filter { case (_, count) => count == maxCount }.keys
      if (top.size == 1) Some(top.head) else None
    }
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