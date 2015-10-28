package controllers

import java.io.File
import javax.inject.{Inject, Singleton}

import models._
import play.api.Logger
import play.api.libs.Crypto
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json.Json
import play.api.mvc._
import play.modules.reactivemongo.{MongoController, ReactiveMongoApi, ReactiveMongoComponents}

import scala.concurrent.Future
import scala.io.Source
import scala.util.Random

@Singleton
class Application @Inject() (val reactiveMongoApi: ReactiveMongoApi)
  extends Controller with MongoController with ReactiveMongoComponents {

  Logger.info(reactiveMongoApi.db.name)

  val path = "/audio/"
  val audioPath = "/var/www/qpug/audio/"
  val merger = new VariantMerger

  val stats = new Stats(reactiveMongoApi)
  val scores = new Scores(reactiveMongoApi)

  def loadDir(dirPath : String) : List[String] = {
    val dir = new File(audioPath + dirPath)
    if (dir.exists()) {
      val files = dir.listFiles().toList.filter(_.getName.endsWith(".mp3"))
      val paths = files.map(dirPath + "/" + _.getName)
      paths
    } else {
      Nil
    }
  }

//  val silence = RandomPattern(loadDir("silence"): _*)
//  val intro = RandomPattern(loadDir("intro"): _*)
//  val question = RandomPattern(loadDir("question"): _*)
//  val buzzer = RandomPattern(loadDir("buzzer"): _*)
//  val answer = RandomPattern(loadDir("answer"): _*)
//  val yes = RandomPattern(loadDir("yes"): _*)
//  val no = RandomPattern(loadDir("no"): _*)
//  val name = RandomPattern(loadDir("name"): _*)
//
//  val pattern = Sequence(
//    intro,
//    Repeat(Sequence(
//      silence,
//      question,
//      silence,
//      name,
//      Repeat(Sequence(answer, no, buzzer), 0, 2),
//      answer,
//      yes
//    ), 3, 3)
//  )

  val silence = RandomPattern(loadDir("silence"): _*)

  val intro = RandomPattern(loadDir("intro"): _*)
  val question = UniqueRandomPattern(loadDir("questions"): _*)
  val answer = UniqueRandomPattern(loadDir("réponses"): _*)
  val yes = RandomPattern(loadDir("ok"): _*)
  val no = RandomPattern(loadDir("non"): _*)
  val name = RandomPattern(loadDir("prénoms"): _*)
  val buzzer = RandomPattern(loadDir("buzzer"): _*)

  val pattern = Sequence(
    intro,
    Repeat(Sequence(
      silence,
      question,
      name,
      silence,
      Repeat(Sequence(answer, no, silence, buzzer, name), 0, 2),
      answer,
      silence,
      yes
    ), 3, 3)
  )

  val titles = Source.fromFile("/var/www/qpug/title.txt").getLines().toList

  val top = 5

  def encryptVariant(variant : List[String]) : String = {
    Crypto.encryptAES(variant.mkString(","))
  }

  def decryptVariant(encryptedVariant : String) : List[String] = {
    val decryptedVariant = Crypto.decryptAES(encryptedVariant)
    decryptedVariant.split(",").toList
  }

  def index() = Action.async {
    var title = Random.shuffle(titles).head
    val futureTopVariants = scores.topVariants(top)
    val futureCounter = stats.getCounter()
    for (topVariants <- futureTopVariants; counter <- futureCounter) yield {
      val encryptedVariants = topVariants.map(v => (encryptVariant(v._1), v._2))
      Ok(views.html.index(title, None, counter, encryptedVariants))
    }
  }
  def play(variant : String) = Action.async {
    var title = Random.shuffle(titles).head
    val futureTopVariants = scores.topVariants(top)
    val futureCounter = stats.getCounter()
    for (topVariants <- futureTopVariants; counter <- futureCounter) yield {
      val encryptedVariants = topVariants.map(v => (encryptVariant(v._1), v._2))
      Ok(views.html.index(title, Some(variant), counter, encryptedVariants))
    }
  }

  def generate = Action.async {
    val variant = pattern.select()
    val encryptedVariant = encryptVariant(variant)
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
    val variant = decryptVariant(encryptedVariant)

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
    val variant = decryptVariant(encryptedVariant)

    // Check range of score
    val checkedScore = if (score < 0) {
      0
    } else if(score > 4) {
      4
    } else {
      score
    }

    scores.addScore(variant, checkedScore - 2)

    Ok("")
  }


  def bonusTPMP() = Action {
    Ok.sendFile(new File(audioPath + "/bonus/cancun.mp3"))
  }


  def bonusMer() = Action {
    Ok.sendFile(new File(audioPath + "/bonus/lamernoir.mp3"))
  }
}

