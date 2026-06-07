package view

import model.PlayerId

case class VoteOption(playerId: PlayerId, x: Float, y: Float, width: Float, height: Float) {
  def contains(px: Float, py: Float): Boolean =
    math.abs(px - x) < width / 2f && math.abs(py - y) < height / 2f
}