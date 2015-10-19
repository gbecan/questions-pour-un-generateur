package models

import scala.util.Random

/**
 * Created by gbecan on 10/19/15.
 */
class VariantSelector {

  def select(pattern: Pattern) : List[String] = {

    pattern match {
      case Repeat(p, min, max) =>
        val n = Random.nextInt(max - min + 1) + min
        (0 until n).toList.flatMap(i => select(p))
      case Sequence(patterns @ _*) =>
        patterns.toList.flatMap(select(_))
      case Type(name, variants) =>
        List(Random.shuffle(variants).head)
    }

  }

}
