package controllers

import models._
import play.api.libs.json.{JsNumber, JsObject, JsString, JsArray}
import play.api.mvc._


class Application extends Controller {

//  val pattern = Repeat(
//    Sequence(
//      Question("Q1", "Q2"),
//      Repeat(Buzzer("B1", "B2"), 2, 4),
//      Answer("A1", "A2")
//  ), 2, 2)

  val path = "/assets/audio/"

  val question = RandomPattern(
    (path + "medicament.mp3", 2.01)
  )

  val buzzer = RandomPattern(
    (path + "buzzer.mp3", 1.38),
    (path + "buzzer2.mp3", 0.81),
    (path + "buzzer3.mp3", 0.86)
  )

  val answer = RandomPattern(
    (path + "macedoine.mp3", 0.84),
    (path + "laguadeloupe.mp3", 0.5)
  )

  val no = RandomPattern(
    (path + "non.mp3", 0.64),
    (path + "nonbuzz.mp3", 0.89)
  )

  val pattern = Sequence(question, Repeat(buzzer, 1, 5), answer, no)

  def index = Action {
    Ok(views.html.index())
  }

  def generate = Action {
    val variant = pattern.select()
    val variantJson = JsArray(variant.map(e => JsObject(Seq(
      "src" -> JsString(e._1),
      "duration" -> JsNumber(e._2)
    ))))
    Ok(variantJson)
  }

  def test = Action {
    Ok(views.html.test())
  }
}

