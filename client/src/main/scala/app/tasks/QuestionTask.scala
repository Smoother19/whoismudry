package app.tasks

import input.MouseState
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.TextInputListener
import scala.util.Random

case class QuestionData(prompt: String, answer: String)

class QuestionTask extends TaskGame {
  private var completed = false
  private var promptOpen = false

  private val questionBank = List(
    QuestionData("Quel est le sens de la vie ?", "42"),
    QuestionData("Combien font 7 x 8 ?", "56"),
    QuestionData("Quel langage est utilise en PCO ?", "Scala"),
    QuestionData("Qui est le boss final de la HES-SO ?", "Mudry")
  )

  private val currentQuestion = Random.shuffle(questionBank).head

  def getQuestionText: String = currentQuestion.prompt

  override def update(mouse: MouseState): Unit = {
    if (mouse.isTouched && !completed && !promptOpen) {
      promptOpen = true

      Gdx.input.getTextInput(new TextInputListener {
        override def input(text: String): Unit = {
          if (text.trim.toLowerCase == currentQuestion.answer.toLowerCase) {
            completed = true
          }
          promptOpen = false
        }

        override def canceled(): Unit = {
          promptOpen = false
        }

      }, currentQuestion.prompt, "", "Tapez votre reponse ici...")
    }
  }

  override def isComplete: Boolean = completed
}