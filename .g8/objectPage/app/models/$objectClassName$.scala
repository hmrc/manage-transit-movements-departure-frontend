package models

import play.api.libs.json._

case class $objectClassName$(value: String) {
  override def toString() = value
}

object $objectClassName$ {

  object Constants {
    val maxLength = $maxLength$
  }
  implicit val format: OFormat[$objectClassName$] = Json.format[$objectClassName$]

}
