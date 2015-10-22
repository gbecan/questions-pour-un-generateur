package controllers

import java.io.File
import javax.inject.Inject

import akka.actor.ActorSystem
import akka.util.Timeout
import models._
import play.api.Logger
import play.api.libs.Crypto
import play.api.libs.iteratee.{Enumeratee, Enumerator}
import play.api.libs.json.{JsValue, Json, JsNumber, JsObject}
import play.api.mvc._

import play.api.libs.concurrent.Execution.Implicits._
import play.modules.reactivemongo.json.collection.JSONCollection
import play.modules.reactivemongo.{MongoController, ReactiveMongoComponents, ReactiveMongoApi}
import play.modules.reactivemongo.json._

import scala.concurrent.Future
import akka.pattern.ask
import scala.concurrent.duration._

class Application @Inject() (val reactiveMongoApi: ReactiveMongoApi, val actorSystem: ActorSystem)
  extends Controller with MongoController with ReactiveMongoComponents {

  val path = "/audio/"
  val audioPath = "/var/www/qpug/audio/"
  val merger = new VariantMerger

  val counterActor = actorSystem.actorOf(StatsActor.props)


  implicit val timeout = Timeout(5.seconds)

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
    val askCounter = counterActor ? StatsActor.GetCounter()
    askCounter.mapTo[Int].map { counter =>
      Ok(views.html.index(None, counter))
    }
  }

  def play(variant : String) = Action.async {
    val askCounter = counterActor ? StatsActor.GetCounter()
    askCounter.mapTo[Int].map { counter =>
      Ok(views.html.index(Some(variant), counter))
    }
  }

  def generate = Action {
    val variant = pattern.select()
    val encryptedVariant = Crypto.encryptAES(variant.mkString(","))

    Logger.info(variant.mkString(", "))

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
        counterActor ! StatsActor.IncreaseCounter()
        Ok.sendFile(file.get, onClose = () => file.get.delete())
      } else {
        NotFound("Variant not found")
      }
    }
  }


  def like(encryptedVariant : String, up : Boolean) = Action {
    val decryptedVariant = Crypto.decryptAES(encryptedVariant)
    val variant = decryptedVariant.split(",").toList

    // Save to database
    saveLike(variant, up)

    Ok("")
  }

  def saveLike(variant : List[String], up : Boolean) {

    // TODO : not thread safe !

    val likesColl = reactiveMongoApi.db.collection[JSONCollection]("likes")
    val query = Json.obj("variant" -> variant)
    likesColl.find(query).one[JsValue].map { previous =>
      val oldCounter = previous.map(_.as[JsObject].value("counter").as[JsNumber].value.toInt).getOrElse(0)
      val newCounter = oldCounter + (if (up) {1} else {-1})

      val update = Json.obj(
        "variant" -> variant,
        "counter" -> newCounter
      )

      likesColl.update(query, update, upsert = true)
    }
  }

}

