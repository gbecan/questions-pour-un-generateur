package models

import play.api.libs.json._
import play.modules.reactivemongo.ReactiveMongoApi
import play.modules.reactivemongo.json._
import play.modules.reactivemongo.json.collection.JSONCollection
import play.api.libs.concurrent.Execution.Implicits._

import scala.concurrent.Future

/**
 * Created by gbecan on 10/22/15.
 */
class Scores(reactiveMongoApi : ReactiveMongoApi) {

  val scoresColl = reactiveMongoApi.db.collection[JSONCollection]("scores")

  def addScore(variant : List[String], score : Int) {

    val query = Json.obj("variant" -> variant)
    val update = Json.obj(
      "$inc" -> Json.obj(
        "counter" -> score
      )
    )

    scoresColl.update(query, update, upsert = true)

  }

  def topVariants(limit : Int) : Future[List[List[String]]] = {
    val topJson = scoresColl
    .find(Json.obj())
    .sort(Json.obj("counter" -> -1))
    .cursor[JsValue]()
    .collect[List](limit)

    // Convert result to list of variants
    topJson.map {futureTop =>
      futureTop.map { top =>
        val variant = top.as[JsObject].value("variant").as[JsArray].value
        variant.toList.map(_.as[JsString].value)
      }
    }

  }
}
