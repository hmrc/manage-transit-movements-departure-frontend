package models

import models.domain.StringFieldRegex.{alphaNumericRegex, alphaNumericWithSpacesRegex, stringFieldRegex}
import play.api.i18n.Messages

import scala.util.matching.Regex

sealed trait AddressLine {
  val field: String
  def arg(implicit messages: Messages): String = messages(s"address.\$field")
}

object AddressLine {

  case object Country extends AddressLine {
    override val field: String = "country"
  }

  sealed trait AddressLineWithValidation extends AddressLine {
    val length: Int
    val regex: Regex
  }

  case object StreetNumber extends AddressLineWithValidation {
    override val field: String = "streetNumber"
    override val length: Int   = 17
    override val regex: Regex  = alphaNumericRegex
  }

  case object NumberAndStreet extends AddressLineWithValidation {
    override val field: String = "numberAndStreet"
    override val length: Int   = 70
    override val regex: Regex  = stringFieldRegex
  }

  case object City extends AddressLineWithValidation {
    override val field: String = "city"
    override val length: Int   = 35
    override val regex: Regex  = stringFieldRegex
  }

  case object PostalCode extends AddressLineWithValidation {
    override val field: String = "postalCode"
    override val length: Int   = 17
    override val regex: Regex  = alphaNumericWithSpacesRegex
  }
}
