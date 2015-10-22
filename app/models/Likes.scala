package models

import play.api.libs.json.Json
import play.modules.reactivemongo.ReactiveMongoApi
import play.modules.reactivemongo.json._
import play.modules.reactivemongo.json.collection.JSONCollection
import play.api.libs.concurrent.Execution.Implicits._

/**
 * Created by gbecan on 10/22/15.
 */
class Likes(reactiveMongoApi : ReactiveMongoApi) {

  val likesColl = reactiveMongoApi.db.collection[JSONCollection]("likes")

  def addLike(variant : List[String], up : Boolean) {

    val likesColl = reactiveMongoApi.db.collection[JSONCollection]("likes")
    val query = Json.obj("variant" -> variant)
    val update = Json.obj(
      "$inc" -> Json.obj(
        "counter" -> (if (up) {1} else {-1})
      )
    )
    likesColl.update(query, update, upsert = true)

  }
}
