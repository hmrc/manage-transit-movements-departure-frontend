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

package models.reference

import base.SpecBase
import generators.Generators
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.libs.json.{JsError, Json}

class AdditionalDeclarationTypeSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  "AdditionalDeclarationType" - {

    "must serialise" in {
      forAll(nonEmptyString, nonEmptyString) {
        (code, description) =>
          val additionalDeclarationType = AdditionalDeclarationType(code, description)
          Json.toJson(additionalDeclarationType) mustBe Json.parse(s"""
               |{
               |  "code": "$code",
               |  "description": "$description"
               |}
               |""".stripMargin)
      }
    }

    "must deserialise" - {
      "when json contains an additional declaration type" in {
        forAll(nonEmptyString, nonEmptyString) {
          (code, description) =>
            val additionalDeclarationType = AdditionalDeclarationType(code, description)
            Json
              .parse(s"""
                   |{
                   |  "code": "$code",
                   |  "description": "$description"
                   |}
                   |""".stripMargin)
              .as[AdditionalDeclarationType] mustBe additionalDeclarationType
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

            val result = json.validate[AdditionalDeclarationType]

            result.mustBe(a[JsError])
        }
      }
    }

    "must format as string" in {
      forAll(nonEmptyString, nonEmptyString) {
        (code, description) =>
          val additionalDeclarationType = AdditionalDeclarationType(code, description)
          additionalDeclarationType.toString mustBe s"$code - $description"
      }
    }
  }
}
