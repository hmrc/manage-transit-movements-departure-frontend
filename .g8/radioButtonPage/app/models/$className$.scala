package models

sealed trait $className$

object $className$ extends RadioModel[$className$] {

  case object $option1key;format="Camel"$ extends WithName("$option1key;format="decap"$") with $className$
  case object $option2key;format="Camel"$ extends WithName("$option2key;format="decap"$") with $className$

  override val messageKeyPrefix: String = "$className;format="decap"$"

  val values: Seq[$className$] = Seq(
    $option1key;format="Camel"$,
    $option2key;format="Camel"$
  )
}
