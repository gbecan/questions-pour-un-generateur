package models

import akka.actor.Actor.Receive
import akka.actor.{Actor, Props}
import models.StatsActor.{GetCounter, IncreaseCounter}

/**
 * Created by gbecan on 10/22/15.
 */
class StatsActor extends Actor {

  var counter = 0

  override def receive: Receive = {
    case IncreaseCounter() =>
      counter += 1
    case GetCounter() =>
      sender() ! counter
  }
}

object StatsActor {
  def props = Props[StatsActor]

  case class IncreaseCounter()
  case class GetCounter()

}
