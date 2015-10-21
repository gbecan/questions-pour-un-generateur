package controllers

import java.io.File

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

  def loadDir(dirPath : String) : List[String] = {
    val dir = new File(audioPath + dirPath)
    val files = dir.listFiles().toList.filter(_.getName.endsWith(".mp3"))
    val paths = files.map(dirPath + "/" + _.getName)
    paths
  }

  val intro = RandomPattern(loadDir("intro"): _*)
  val question = RandomPattern(loadDir("question"): _*)
  val buzzer = RandomPattern(loadDir("buzzer"): _*)
  val answer = RandomPattern(loadDir("answer"): _*)
  val yes = RandomPattern(loadDir("yes"): _*)
  val no = RandomPattern(loadDir("no"): _*)

  val pattern = Sequence(
    intro,
    Repeat(Sequence(
      question,
      buzzer,
      Repeat(Sequence(answer, no), 0, 2),
      answer,
      yes
    ), 1, 3)
  )

  def index() = Action {
    Ok(views.html.index(None))
  }

  def play(variant : String) = Action {
    Ok(views.html.index(Some(variant)))
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

