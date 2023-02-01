/*
 * Copyright 2023 HM Revenue & Customs
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
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.libs.json.Json
import uk.gov.hmrc.govukfrontend.views.viewmodels.select.SelectItem

class CurrencyCodeSpec extends SpecBase with ScalaCheckPropertyChecks {

  "CurrencyCode" - {

    "must serialise" in {
      forAll(Gen.alphaNumStr, Gen.alphaNumStr) {
        (currency, description) =>
          val currencyCode = CurrencyCode(currency, description)
          Json.toJson(currencyCode) mustBe Json.parse(s"""
                |{
                |  "currency": "$currency",
                |  "description": "$description"
                |}
                |""".stripMargin)
      }
    }

    "must deserialise" in {
      forAll(Gen.alphaNumStr, Gen.alphaNumStr) {
        (currency, description) =>
          val currencyCode = CurrencyCode(currency, description)
          Json
            .parse(s"""
                        |{
                        |  "currency": "$currency",
                        |  "description": "$description"
                        |}
                        |""".stripMargin)
            .as[CurrencyCode] mustBe currencyCode
      }
    }

    "must convert to select item" in {
      forAll(Gen.alphaNumStr, Gen.alphaNumStr, arbitrary[Boolean]) {
        (currency, description, selected) =>
          val currencyCode = CurrencyCode(currency, description)
          currencyCode.toSelectItem(selected) mustBe SelectItem(Some(currency), s"$currency", selected)
      }
    }

    "must format as string" in {
      forAll(Gen.alphaNumStr, Gen.alphaNumStr) {
        (currency, description) =>
          val currencyCode = CurrencyCode(currency, description)
          currencyCode.toString mustBe s"$currency"
      }
    }
  }

}
