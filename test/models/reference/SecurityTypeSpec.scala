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

class SecurityTypeSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  "SecurityType" - {

    "must serialise" in {
      forAll(nonEmptyString, nonEmptyString) {
        (code, description) =>
          val securityType = SecurityType(code, description)
          Json.toJson(securityType) mustBe Json.parse(s"""
              |{
              |  "code": "$code",
              |  "description": "$description"
              |}
              |""".stripMargin)
      }
    }

    "must deserialise" - {
      "when json contains a security type" in {
        forAll(nonEmptyString, nonEmptyString) {
          (code, description) =>
            val securityType = SecurityType(code, description)
            Json
              .parse(s"""
                   |{
                   |  "code": "$code",
                   |  "description": "$description"
                   |}
                   |""".stripMargin)
              .as[SecurityType] mustBe securityType
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

            val result = json.validate[SecurityType]

            result.mustBe(a[JsError])
        }
      }
    }

    "must format as string" - {
      "when description doesn't contain raw HTML" in {
        forAll(nonEmptyString, nonEmptyString) {
          (code, description) =>
            val securityType = SecurityType(code, description)
            securityType.toString mustBe description
        }
      }

      "when description contains raw HTML" in {
        val securityType = SecurityType("3", "ENS &amp; EXS")
        securityType.toString mustBe "ENS & EXS"
      }
    }
  }

}
