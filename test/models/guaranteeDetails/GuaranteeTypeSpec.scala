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

package models.guaranteeDetails

import base.SpecBase
import generators.Generators
import models.guaranteeDetails.GuaranteeType._
import models.reference.{CountryCode, CustomsOffice}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.preTaskList.OfficeOfDeparturePage
import play.api.libs.json.{JsError, JsString, Json}

class GuaranteeTypeSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  "GuaranteeType" - {

    "must deserialise valid values" in {

      val gen = Gen.oneOf(GuaranteeType.values)

      forAll(gen) {
        guaranteeType =>
          JsString(guaranteeType.toString).validate[GuaranteeType].asOpt.value mustEqual guaranteeType
      }
    }

    "must fail to deserialise invalid values" in {

      val gen = arbitrary[String] suchThat (!GuaranteeType.values.map(_.toString).contains(_))

      forAll(gen) {
        invalidValue =>
          JsString(invalidValue).validate[GuaranteeType] mustEqual JsError("error.invalid")
      }
    }

    "must serialise" in {

      val gen = Gen.oneOf(GuaranteeType.values)

      forAll(gen) {
        guaranteeType =>
          Json.toJson(guaranteeType) mustEqual JsString(guaranteeType.toString)
      }
    }

    "Radio options" - {

      "Must return the correct number of radios" - {
        "When Office of Departure is 'XI'" in {
          val answers = emptyUserAnswers
            .setValue(OfficeOfDeparturePage, CustomsOffice("id", "name", CountryCode("XI"), None))

          val radios = GuaranteeType.valuesU(answers)
          val expected = Seq(
            GuaranteeWaiver,
            ComprehensiveGuarantee,
            IndividualGuarantee,
            CashDepositGuarantee,
            FlatRateVoucher,
            GuaranteeWaiverSecured,
            GuaranteeNotRequiredExemptPublicBody,
            GuaranteeWaiverByAgreement,
            GuaranteeNotRequired
          )

          radios mustBe expected
        }

        "When Office of Departure is 'GB'" in {
          val answers = emptyUserAnswers
            .setValue(OfficeOfDeparturePage, CustomsOffice("id", "name", CountryCode("GB"), None))

          val radios = GuaranteeType.valuesU(answers)
          val expected = Seq(
            GuaranteeWaiver,
            ComprehensiveGuarantee,
            IndividualGuarantee,
            CashDepositGuarantee,
            FlatRateVoucher,
            GuaranteeWaiverSecured,
            GuaranteeNotRequiredExemptPublicBody,
            IndividualGuaranteeMultiple,
            GuaranteeWaiverByAgreement,
            GuaranteeNotRequired
          )

          radios mustBe expected
        }
      }
    }
  }
}
