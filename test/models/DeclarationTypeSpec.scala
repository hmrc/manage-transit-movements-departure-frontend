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

import base.SpecBase
import commonTestUtils.UserAnswersSpecHelper
import generators.Generators
import models.DeclarationType.{Option1, Option2, Option3, Option4}
import models.reference.CountryCode
import org.scalacheck.Arbitrary.arbitrary
import org.scalatest.OptionValues
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.libs.json.{JsError, JsString, Json}

class DeclarationTypeSpec
    extends AnyFreeSpec
    with Matchers
    with ScalaCheckPropertyChecks
    with OptionValues
    with SpecBase
    with Generators
    with UserAnswersSpecHelper {

  "DeclarationType" - {

    "must deserialise valid values" in {
      forAll(arbitrary[DeclarationType]) {
        declarationType =>
          JsString(declarationType.toString).validate[DeclarationType].asOpt.value mustEqual declarationType
      }
    }

    "must fail to deserialise invalid values" in {

      val gen = arbitrary[String] retryUntil (!DeclarationType.values.map(_.toString).contains(_))

      forAll(gen) {
        invalidValue =>
          JsString(invalidValue).validate[DeclarationType] mustEqual JsError("error.invalid")
      }
    }

    "must serialise" in {
      forAll(arbitrary[DeclarationType]) {
        declarationType =>
          Json.toJson(declarationType) mustEqual JsString(declarationType.toString)
      }
    }

    "Radio options" - {
      "Must return the correct number of radios" - {
        "When Office of Departure is 'GB'" in {
          val radios   = DeclarationType.chooseValues(Some(CountryCode("GB")), None)
          val expected = Seq(Option1, Option2)
          radios mustBe expected

        }
        "When Office of Departure is 'XI' and the departure type is Simplified" in {
          val radios   = DeclarationType.chooseValues(Some(CountryCode("XI")), Some(ProcedureType.Simplified))
          val expected = Seq(Option1, Option2, Option3)
          radios mustBe expected
        }
        "When Office of Departure is 'XI' and the departure type is Normal" in {
          val radios   = DeclarationType.chooseValues(Some(CountryCode("XI")), Some(ProcedureType.Normal))
          val expected = Seq(Option1, Option2, Option3, Option4)
          radios mustBe expected
        }
      }
    }
  }
}
