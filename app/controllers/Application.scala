package controllers

import java.io.File
import javax.inject.Inject

import akka.actor.ActorSystem
import models._
import play.api.Logger
import play.api.libs.Crypto
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json.Json
import play.api.mvc._
import play.modules.reactivemongo.json._
import play.modules.reactivemongo.json.collection.JSONCollection
import play.modules.reactivemongo.{MongoController, ReactiveMongoApi, ReactiveMongoComponents}

import scala.concurrent.Future

class Application @Inject() (val reactiveMongoApi: ReactiveMongoApi)
  extends Controller with MongoController with ReactiveMongoComponents {

  Logger.info("test")

  val path = "/audio/"
  val audioPath = "/var/www/qpug/audio/"
  val merger = new VariantMerger

  val stats = new Stats(reactiveMongoApi)
  val scores = new Scores(reactiveMongoApi)

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

  def index() = Action.async {
    stats.getCounter().map { counter =>
      Ok(views.html.index(None, counter))
    }
  }

  def play(variant : String) = Action.async {
    stats.getCounter().map { counter =>
      Ok(views.html.index(Some(variant), counter))
    }
  }

  def generate = Action.async {
    val variant = pattern.select()
    val encryptedVariant = Crypto.encryptAES(variant.mkString(","))
    Logger.info(variant.mkString(", "))

    stats.increaseCounter().flatMap { _ =>
      stats.getCounter().map { counter =>
        Ok(Json.obj(
          "variant" -> encryptedVariant,
          "counter" -> counter
        ))
      }
    }


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


  def score(encryptedVariant : String, score : Int) = Action {
    val decryptedVariant = Crypto.decryptAES(encryptedVariant)
    val variant = decryptedVariant.split(",").toList

    // Check range of score
    val checkedScore = if (score < 0) {
      0
    } else if(score > 4) {
      4
    } else {
      score
    }

    scores.addScore(variant, checkedScore)

    Ok("")
  }


}

