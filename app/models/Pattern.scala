package models

/**
 * Created by gbecan on 10/16/15.
 */
abstract class Pattern
case class Repeat(pattern : Pattern, min : Int, max : Int) extends Pattern
case class Sequence(pattern : Pattern*) extends Pattern
case class Type(name: String, variants: List[String]) extends Pattern