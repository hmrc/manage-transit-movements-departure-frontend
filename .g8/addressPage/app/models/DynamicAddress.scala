package models

import play.api.libs.json.{Json, OFormat}

case class DynamicAddress(
  numberAndStreet: String,
  city: String,
  postalCode: Option[String]
) {

  override def toString: String = Seq(Some(numberAndStreet), Some(city), postalCode).flatten.mkString("<br>")
}

object DynamicAddress {
  implicit val format: OFormat[DynamicAddress] = Json.format[DynamicAddress]
}
