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
import cats.data.NonEmptySet
import generators.Generators
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.libs.json.{JsError, Json}

class CountrySpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  "format" - {
    "must deserialise" - {
      "when json contains a code" in {
        forAll(nonEmptyString) {
          value =>
            val json = Json.parse(s"""
                 |{
                 |  "code" : "$value"
                 |}
                 |""".stripMargin)

            val result = json.validate[Country]

            val expectedResult = Country(value)

            result.get.mustBe(expectedResult)
        }
      }
    }

    "must fail to deserialise" - {
      "when json is unexpected shape" in {
        forAll(nonEmptyString, nonEmptyString) {
          (key, value) =>
            val json = Json.parse(s"""
                 |{
                 |  "$key" : "$value"
                 |}
                 |""".stripMargin)

            val result = json.validate[Country]

            result.mustBe(a[JsError])
        }
      }
    }

    "must serialise" in {
      forAll(nonEmptyString) {
        value =>
          val country = Country(value)

          val result = Json.toJson(country)

          val expectedResult = Json.parse(s"""
               |{
               |  "code" : "$value"
               |}
               |""".stripMargin)

          result mustBe expectedResult
      }
    }
  }

  "order" - {
    "must order countries by code" in {
      val unorderedCountries = Seq(
        Country("AD"),
        Country("XI"),
        Country("FR")
      )

      val orderedCountries = Seq(
        Country("AD"),
        Country("FR"),
        Country("XI")
      )

      val result = NonEmptySet
        .of(unorderedCountries.head, unorderedCountries.tail*)
        .toSortedSet
        .toList

      result.mustBe(orderedCountries)
    }

    "must ensure countries are unique by code" in {
      val duplicateCountries = Seq(
        Country("AD"),
        Country("AD"),
        Country("XI"),
        Country("XI"),
        Country("FR"),
        Country("FR")
      )

      val uniqueCountries = Seq(
        Country("AD"),
        Country("FR"),
        Country("XI")
      )

      val result = NonEmptySet
        .of(duplicateCountries.head, duplicateCountries.tail*)
        .toSortedSet
        .toList

      result.mustBe(uniqueCountries)
    }
  }
}
