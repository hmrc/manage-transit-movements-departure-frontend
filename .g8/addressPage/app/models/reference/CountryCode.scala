package models.reference

import cats.Order
import play.api.libs.json._

case class CountryCode(code: String)

object CountryCode {

  object Constants {
    val countryCodeLength = 2
  }

  implicit val countryCodeWrites: Writes[CountryCode] = (countryCode: CountryCode) => JsString(countryCode.code)

  implicit val countryCodeReads: Reads[CountryCode] = {
    case JsObject(mapping) => JsSuccess(CountryCode(mapping("code").as[String]))
    case JsString(code)    => JsSuccess(CountryCode(code))
    case x                 => JsError(s"Expected a string, got a \${x.getClass}")
  }

  implicit val order: Order[CountryCode] = (x: CountryCode, y: CountryCode) => {
    (x, y).compareBy(_.code)
  }
}
