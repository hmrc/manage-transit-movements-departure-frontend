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

package models

import base.SpecBase
import generators.Generators
import models.DeclarationType.{Option1, Option2, Option3, Option4, Option5}
import models.reference.CustomsOffice
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.preTaskList.{OfficeOfDeparturePage, ProcedureTypePage}
import play.api.libs.json.{JsError, JsString, Json}

class DeclarationTypeSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

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
        "When Office of Departure is 'XI' and the departure type is Normal" in {
          val answers = emptyUserAnswers
            .setValue(OfficeOfDeparturePage, CustomsOffice("XI343", "name", None))
            .setValue(ProcedureTypePage, ProcedureType.Normal)

          val radios   = DeclarationType.valuesU(answers)
          val expected = Seq(Option1, Option2, Option3, Option4, Option5)
          radios mustBe expected
        }

        "When Office of Departure is 'GB'" in {
          val answers = emptyUserAnswers
            .setValue(OfficeOfDeparturePage, CustomsOffice("GB24R", "name", None))

          val radios   = DeclarationType.valuesU(answers)
          val expected = Seq(Option1, Option2, Option3, Option5)
          radios mustBe expected
        }

        "When Office of Departure is 'XI' and Simplified" in {
          val answers = emptyUserAnswers
            .setValue(OfficeOfDeparturePage, CustomsOffice("XI93F", "name", None))
            .setValue(ProcedureTypePage, ProcedureType.Simplified)

          val radios   = DeclarationType.valuesU(answers)
          val expected = Seq(Option1, Option2, Option3, Option5)
          radios mustBe expected
        }
      }
    }
  }
}
