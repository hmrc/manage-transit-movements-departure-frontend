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

import models.domain.StringFieldRegex.{postalCodeRegex, stringFieldRegex}
import play.api.i18n.Messages

import scala.util.matching.Regex

sealed trait AddressLine {
  val field: String
  def arg(implicit messages: Messages): String = messages(s"address.$field")
}

object AddressLine {

  case object AddressLine1 extends AddressLine {
    override val field: String = "addressLine1"
    val length: Int            = 35
    val regex: Regex           = stringFieldRegex
  }

  case object AddressLine2 extends AddressLine {
    override val field: String = "addressLine2"
    val length: Int            = 35
    val regex: Regex           = stringFieldRegex
  }

  case object PostalCode extends AddressLine {
    override val field: String = "postalCode"
    val length: Int            = 9
    val regex: Regex           = postalCodeRegex
  }

  case object Country extends AddressLine {
    override val field: String = "country"
  }
}
