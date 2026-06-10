package model

trait Role {
  val canKill: Boolean

  def executeKill(killer: PlayerState, target: PlayerState, currentTime: Long, cooldownMs: Long, killRadius: Double): Option[(PlayerState, PlayerState)] = None
}

object Role {
  case object Students extends Role {
    override val canKill = false
  }
  case object Mudry extends Role {
    override val canKill = true

    override def executeKill(killer: PlayerState, target: PlayerState, currentTime: Long, cooldownMs: Long, killRadius: Double): Option[(PlayerState, PlayerState)] = {
      if (!killer.isDead && !target.isDead && (currentTime - killer.lastKillTime >= cooldownMs)) {

        val dx = killer.position.x - target.position.x
        val dy = killer.position.y - target.position.y
        val distance = Math.sqrt(dx * dx + dy * dy)

        if (distance <= killRadius) {
          val deadTarget = target.copy(isDead = true)
          val updatedKiller = killer.copy(lastKillTime = currentTime)

          Some((deadTarget, updatedKiller))
        } else {
          None
        }
      } else {
        None
      }
    }
  }
}
