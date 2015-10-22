package models

import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json.{JsNumber, JsObject, Json}
import play.modules.reactivemongo.ReactiveMongoApi
import play.modules.reactivemongo.json._
import play.modules.reactivemongo.json.collection.JSONCollection

import scala.concurrent.Future

/**
 * Created by gbecan on 10/22/15.
 */
class Stats(reactiveMongoApi : ReactiveMongoApi) {

  val statsColl = reactiveMongoApi.db.collection[JSONCollection]("statistics")

  def increaseCounter() : Future[Any] = {
    val query = Json.obj()
    val update = Json.obj(
      "$inc" -> Json.obj("counter" -> 1)
    )
    statsColl.update(query, update, upsert = true)
  }

  def getCounter() : Future[Int] = {
    val result = statsColl.find(Json.obj()).one[JsObject]
    val futureCounter = result.map { stats =>
      if (stats.isDefined) {
        val counter = stats.get.value("counter").as[JsNumber].value.toInt
        counter
      } else {
         0
      }
    }
    futureCounter
  }
}

