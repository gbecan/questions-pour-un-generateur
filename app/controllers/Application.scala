package controllers

import models._
import play.api.libs.json.{JsString, JsArray}
import play.api.mvc._


class Application extends Controller {

//  val pattern = Repeat(
//    Sequence(
//      Question("Q1", "Q2"),
//      Repeat(Buzzer("B1", "B2"), 2, 4),
//      Answer("A1", "A2")
//  ), 2, 2)

  val path = "/assets/audio/"
  val pattern = Question(path + "test0.mp3", path + "test1.mp3", path + "test2.mp3")

  def index = Action {
    Ok(views.html.index())
  }

  def generate = Action {
    val variant = pattern.select()
    val variantJson = JsArray(variant.map(s => JsString(s)))
    Ok(variantJson)
  }

  def test = Action {
    Ok(views.html.test())
  }
}

