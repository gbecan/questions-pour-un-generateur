package controllers

import models._
import play.api.Logger
import play.api.libs.Crypto
import play.api.libs.iteratee.{Enumeratee, Enumerator}
import play.api.mvc._

import play.api.libs.concurrent.Execution.Implicits._

import scala.concurrent.Future


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
    Ok(encryptedVariant)
  }

  def getAudioFile(encryptedVariant : String) = Action.async {
    val decryptedVariant = Crypto.decryptAES(encryptedVariant)
    val variant = decryptedVariant.split(",").toList

    val futureFile = Future {
      merger.merge(variant.map(elem => audioPath + elem))
    }

    futureFile.map { file =>
      if (file.isDefined) {
        Ok.sendFile(file.get, onClose = () => file.get.delete())
      } else {
        NotFound("Variant not found")
      }
    }
  }

  def up(encryptedVariant : String) = Action {
    val decryptedVariant = Crypto.decryptAES(encryptedVariant)
    val variant = decryptedVariant.split(",").toList
    // TODO : save
    Logger.info("+1 : " + variant)
    Ok("")
  }

  def down(encryptedVariant : String) = Action {
    val decryptedVariant = Crypto.decryptAES(encryptedVariant)
    println(decryptedVariant)
    val variant = decryptedVariant.split(",").toList
    // TODO : save
    Logger.info("-1 : " + variant)
    Ok("")
  }

}

