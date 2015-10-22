package models

import play.api.libs.json.Json
import play.modules.reactivemongo.ReactiveMongoApi
import play.modules.reactivemongo.json._
import play.modules.reactivemongo.json.collection.JSONCollection
import play.api.libs.concurrent.Execution.Implicits._

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
}
