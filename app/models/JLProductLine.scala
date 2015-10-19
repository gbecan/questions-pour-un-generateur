package models

/**
 * Created by gbecan on 10/19/15.
 */
object JLProductLine {

  val question = Type("Question", List(
    "Je suis un ...?",
    "En 1986, ...?"
  ))

  val answer = Type("Answer", List(
    "Cancun"
  ))

  val buzzer = Type("Buzzer", List(
    "Bzz",
    "Buzzzz"
  ))

}
