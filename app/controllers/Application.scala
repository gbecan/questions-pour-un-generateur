package controllers

import java.io.File

import models._
import play.api.libs.Crypto
import play.api.libs.json.{JsNumber, JsObject, JsString, JsArray}
import play.api.mvc._


class Application extends Controller {

  val path = "/audio/"
  val audioPath = "/var/www/qpug/audio/"
  val merger = new VariantMerger


  val intro = RandomPattern(
    "intro.mp3"
  )

  val question = RandomPattern(
    "medicament.mp3",
    "question3.mp3",
    "questionan177.mp3"
  )

  val buzzer = RandomPattern(
    "buzzer.mp3",
    "buzzer2.mp3",
    "buzzer3.mp3"
  )

  val answer = RandomPattern(
    "macedoine.mp3",
    "laguadeloupe.mp3",
    "allomamanbobo.mp3"
  )

  val confirm = RandomPattern(
    "non.mp3",
    "nonbuzz.mp3",
    "voila.mp3"
  )

  val pattern = Sequence(intro, question, Repeat(buzzer, 2, 5 ), answer, confirm)

  def index = Action {
    Ok(views.html.index())
  }

  def generate = Action {
    val variant = pattern.select()
    val encryptedVariant = Crypto.encryptAES(variant.mkString(","))
    Ok(path + encryptedVariant)
  }

  def getAudioFile(encryptedVariant : String) = Action {
    val decryptedVariant = Crypto.decryptAES(encryptedVariant)
    val variant = decryptedVariant.split(",").toList

    val file = merger.merge(variant.map(elem => audioPath + elem))

    if (file.isDefined) {
      Ok.sendFile(file.get)
    } else {
      NotFound("Variant not found")
    }

  }

}

