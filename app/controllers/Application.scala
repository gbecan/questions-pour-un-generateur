package controllers

import java.io.File

import models._
import play.api.libs.json.{JsNumber, JsObject, JsString, JsArray}
import play.api.mvc._


class Application extends Controller {

  val path = "/audio/"
  val audioPath = "/var/www/qpug/audio/"

  val intro = RandomPattern(
    ("intro.mp3", 8.75)
  )

  val question = RandomPattern(
    ("medicament.mp3", 2.01),
    ("question3.mp3", 4.0),
    ("questionan177.mp3", 3.0)
  )

  val buzzer = RandomPattern(
    ("buzzer.mp3", 1.38),
    ("buzzer2.mp3", 0.81),
    ("buzzer3.mp3", 0.86)
  )

  val answer = RandomPattern(
    ("macedoine.mp3", 0.84),
    ("laguadeloupe.mp3", 0.5),
    ("allomamanbobo.mp3", 1.02)
  )

  val confirm = RandomPattern(
    ("non.mp3", 0.64),
    ("nonbuzz.mp3", 0.89),
    ("voila.mp3", 0.91)
  )

  val pattern = Sequence(intro, question, Repeat(buzzer, 1, 5), answer, confirm)

  def index = Action {
    Ok(views.html.index())
  }

  def generate = Action {
    val variant = pattern.select()
    val variantJson = JsArray(variant.map(e => JsObject(Seq(
      "src" -> JsString(path + e._1),
      "duration" -> JsNumber(e._2)
    ))))
    Ok(variantJson)
  }

  def getAudioFile(filePath : String) = Action {
    val file = new File(audioPath + filePath)
    // TODO : check security
    if (file.exists()) {
      Ok.sendFile(file)
    } else {
      NotFound(file.getName)
    }

  }

}

