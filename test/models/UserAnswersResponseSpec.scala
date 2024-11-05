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
import models.UserAnswersResponse.*
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.libs.json.Json

class UserAnswersResponseSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  "UserAnswersResponse" - {

    "must deserilaise NotAccepted" in {
      forAll(nonEmptyString) {
        value =>
          val json = Json.parse(s"""
                   |{
                   |  "code" : "$value"
                   |}
                   |""".stripMargin)

          val result = json.validate[BadRequest]

          val expectedResult = BadRequest(value)

          result.get.mustBe(expectedResult)
      }
    }

    "must deserilaise BadRequest" in {
      forAll(nonEmptyString) {
        value =>
          val json = Json.parse(s"""
                   |{
                   |  "code" : "$value"
                   |}
                   |""".stripMargin)

          val result = json.validate[BadRequest]

          val expectedResult = BadRequest(value)

          result.get.mustBe(expectedResult)
      }
    }
  }
}
