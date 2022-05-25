/*
 * Copyright 2022 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package models

import models.domain.StringFieldRegex.stringFieldRegex
import play.api.i18n.Messages
import play.api.libs.json._

import scala.util.matching.Regex

case class Address(line1: String, line2: String, postcode: String)

object Address {

  sealed trait AddressLine {
    val field: String
    val length: Int                              = 35
    val regex: Regex                             = stringFieldRegex
    def arg(implicit messages: Messages): String = messages(s"address.$field")
  }

  case object NumberAndStreet extends AddressLine {
    override val field: String = "numberAndStreet"
  }

  case object BuildingAndStreet extends AddressLine {
    override val field: String = "buildingAndStreet"
  }

  case object Town extends AddressLine {
    override val field: String = "town"
  }

  case object City extends AddressLine {
    override val field: String = "city"
  }

  case object Postcode extends AddressLine {
    override val field: String = "postcode"
    override val regex: Regex  = "^[a-zA-Z\\s*0-9]*$".r
    val formatRegex: Regex     = "^[a-zA-Z]{1,2}([0-9]{1,2}|[0-9][a-zA-Z])\\s*[0-9][a-zA-Z]{2}$".r
  }

  implicit val format: OFormat[Address] = Json.format[Address]
}
