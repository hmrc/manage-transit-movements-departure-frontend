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

package models.reference

import play.api.libs.json._

case class CountryCode(code: String)

object CountryCode {

  object Constants {
    val countryCodeLength = 2
  }

  implicit val format: Format[CountryCode] =
    new Format[CountryCode] {
      override def writes(o: CountryCode): JsValue = JsString(o.code)

      override def reads(json: JsValue): JsResult[CountryCode] = json match {
        case JsString(code) => JsSuccess(CountryCode(code))
        case x              => JsError(s"Expected a string, got a ${x.getClass}")
      }
    }

}
