package models

import play.api.data.Form
import play.api.i18n.Messages
import play.api.libs.json._
import uk.gov.hmrc.viewmodels._

sealed trait $className$

object $className$ extends RadioModel[className$] { {

  case object $option1key;format="Camel"$ extends WithName("$option1key;format="decap"$") with $className$
  case object $option2key;format="Camel"$ extends WithName("$option2key;format="decap"$") with $className$

  val values: Seq[$className$] = Seq(
    $option1key;format="Camel"$,
    $option2key;format="Camel"$
  )
}
