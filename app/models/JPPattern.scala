package models

case class Question(initVariants : String*) extends RandomPattern(initVariants)
case class Answer(initVariants : String*) extends RandomPattern(initVariants)
case class Buzzer(initVariants : String*) extends RandomPattern(initVariants)
