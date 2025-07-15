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
import config.FrontendAppConfig
import generators.Generators
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.libs.json.{JsError, Json}
import play.api.test.Helpers.running

class DeclarationTypeSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  "DeclarationType" - {

    "must serialise" in {
      forAll(Gen.alphaNumStr, Gen.alphaNumStr) {
        (code, description) =>
          val declarationType = DeclarationType(code, description)
          Json.toJson(declarationType) mustEqual Json.parse(s"""
              |{
              |  "code": "$code",
              |  "description": "$description"
              |}
              |""".stripMargin)
      }
    }

    "must deserialise" - {
      "when phase-6" - {
        "when json contains a declaration type" in {
          running(_.configure("feature-flags.phase-6-enabled" -> true)) {
            app =>
              val config = app.injector.instanceOf[FrontendAppConfig]
              forAll(nonEmptyString, nonEmptyString) {
                (code, description) =>
                  val declarationType = DeclarationType(code, description)
                  Json
                    .parse(s"""
                         |{
                         |  "key": "$code",
                         |  "value": "$description"
                         |}
                         |""".stripMargin)
                    .as[DeclarationType](DeclarationType.reads(config)) mustEqual declarationType
              }
          }

        }
      }
      "when phase-5" - {
        "when json contains a declaration type" in {
          running(_.configure("feature-flags.phase-6-enabled" -> false)) {
            app =>
              val config = app.injector.instanceOf[FrontendAppConfig]
              forAll(nonEmptyString, nonEmptyString) {
                (code, description) =>
                  val declarationType = DeclarationType(code, description)
                  Json
                    .parse(s"""
                         |{
                         |  "code": "$code",
                         |  "description": "$description"
                         |}
                         |""".stripMargin)
                    .as[DeclarationType](DeclarationType.reads(config)) mustEqual declarationType
              }
          }

        }
      }

    }

    "must read from mongo" in {
      forAll(nonEmptyString, nonEmptyString) {
        (code, description) =>
          val declarationType = DeclarationType(code, description)
          Json
            .parse(s"""
                 |{
                 |  "code": "$code",
                 |  "description": "$description"
                 |}
                 |""".stripMargin)
            .as[DeclarationType] mustEqual declarationType
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

            val result = json.validate[DeclarationType]

            result mustBe a[JsError]
        }
      }
    }

    "must format as string" in {
      forAll(nonEmptyString, nonEmptyString) {
        (code, description) =>
          val declarationType = DeclarationType(code, description)
          declarationType.toString mustEqual s"$code - $description"
      }
    }
  }

}
