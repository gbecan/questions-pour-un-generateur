package models

import scala.util.Random

/**
 * Created by gbecan on 10/16/15.
 */
abstract class Pattern[T] {
  def select(history : List[T] = Nil) : List[T]
}

case class Repeat[T](pattern : Pattern[T], min : Int, max : Int) extends Pattern[T] {
  override def select(history: List[T]): List[T] = {
    val n = Random.nextInt(max - min + 1) + min
    (0 until n).toList.foldLeft((Nil : List[T])){ (acc, _)  =>
      acc ::: pattern.select(history ::: acc)
    }
  }
}

case class Sequence[T](patterns : Pattern[T]*) extends Pattern[T] {
  override def select(history: List[T]): List[T] = {
    patterns.toList.foldLeft(Nil : List[T]){ (acc, p) =>
      acc ::: p.select(history ::: acc)
    }
  }
}

case class Or[T](pattern1: Pattern[T], pattern2: Pattern[T]) extends Pattern[T] {
  override def select(history: List[T]): List[T] = {
    if (Random.nextBoolean()) {
      pattern1.select(history)
    } else {
      pattern2.select(history)
    }
  }
}

case class RandomPattern[T](variants : T*) extends Pattern[T] {
  override def select(history: List[T]): List[T] = {
    if (variants.nonEmpty) {
      List(Random.shuffle(variants).head)
    } else {
      Nil
    }
  }
}

case class UniqueRandomPattern[T](variants: T*) extends Pattern[T] {
  override def select(history: List[T]): List[T] = {
    val filteredVariant = variants.filter(s => !history.contains(s))
    if (filteredVariant.nonEmpty) {
      List(Random.shuffle(filteredVariant).head)
    } else {
      Nil
    }
  }
}
