/*
 * Copyright 2024 HM Revenue & Customs
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

import base.SpecBase
import generators.Generators
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.libs.json.{JsError, Json}

class DepartureMessageSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  "reads" - {
    "must deserialise" - {
      "when json contains a message type" in {
        forAll(nonEmptyString) {
          value =>
            val json = Json.parse(s"""
                 |{
                 |  "type" : "$value"
                 |}
                 |""".stripMargin)

            val result = json.validate[DepartureMessage]

            val expectedResult = DepartureMessage(value)

            result.get.mustEqual(expectedResult)
        }
      }
    }

    "must fail to deserialise" - {
      "when json is in unexpected shape" in {
        forAll(nonEmptyString, nonEmptyString) {
          (key, value) =>
            val json = Json.parse(s"""
                 |{
                 |  "$key" : "$value"
                 |}
                 |""".stripMargin)

            val result = json.validate[DepartureMessage]

            result mustBe a[JsError]
        }
      }
    }
  }
}
