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

import play.api.i18n.Messages
import play.api.libs.json._

import scala.util.matching.Regex

case class Address(line1: String, line2: String, postcode: String)

object Address {

  object Constants {
    val buildingAndStreetLength = 35
    val numberAndStreetLength   = 35
    val cityLength              = 35
    val townLength              = 35

    lazy val postCodeRegex: Regex       = "^[a-zA-Z\\s*0-9]*$".r
    lazy val postCodeFormatRegex: Regex = "^[a-zA-Z]{1,2}([0-9]{1,2}|[0-9][a-zA-Z])\\s*[0-9][a-zA-Z]{2}$".r

    object Fields {
      def buildingAndStreet(implicit messages: Messages): String = messages("address.buildingAndStreet")
      def numberAndStreet(implicit messages: Messages): String   = messages("address.numberAndStreet")
      def city(implicit messages: Messages): String              = messages("address.city")
      def town(implicit messages: Messages): String              = messages("address.town")
      def postcode(implicit messages: Messages): String          = messages("address.postcode")
    }
  }

  implicit val format: OFormat[Address] = Json.format[Address]
}
