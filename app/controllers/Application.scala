package controllers

import models._
import play.api.mvc._
import JLProductLine._


class Application extends Controller {

  val variantSelector = new VariantSelector
  val pattern = Sequence(question, Repeat(buzzer, 2, 4), answer)

  def index = Action {
    val variant = variantSelector.select(pattern)
    Ok(views.html.index(variant))
  }

}
