package network.command

case class SubmitVote(voterId: String, targetId: String) extends Command