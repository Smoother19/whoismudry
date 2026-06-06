package app

import app.ClientState._
import model.{TileMap, TaskType}

class LocalStateManager(tileMap: TileMap) {

  private var current: ClientState = MainMenu("")

  def currentState: ClientState = current

  def updateTypedName(newName: String): Unit = {
    current match {
      case MainMenu(_) => current = MainMenu(newName)
      case _ =>
    }
  }

  def confirmName(): Unit = {
    current match {
      case MainMenu(name) if name.trim.nonEmpty =>
        GameManager.start(tileMap, name.trim)
        current = FreeRoam
      case _ =>
    }
  }

  def openTask(task: TaskType): Unit = {
    current match {
      case FreeRoam => current = DoingTask(task)
      case _ =>
    }
  }


  def closeTask(): Unit = {
    current match {
      case DoingTask(_) => current = FreeRoam
      case _ =>
    }
  }
}