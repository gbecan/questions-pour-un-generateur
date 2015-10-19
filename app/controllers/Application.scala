package controllers

import models._
import play.api.libs.json.{JsString, JsArray}
import play.api.mvc._
import JLProductLine._


class Application extends Controller {

  val variantSelector = new VariantSelector
  val pattern = Sequence(question, Repeat(buzzer, 2, 4), answer)

  def index = Action {
    Ok(views.html.index())
  }

  def generate = Action {
    val variant = variantSelector.select(pattern)
    val variantJson = JsArray(variant.map(s => JsString(s)))
    Ok(variantJson)
  }

}
