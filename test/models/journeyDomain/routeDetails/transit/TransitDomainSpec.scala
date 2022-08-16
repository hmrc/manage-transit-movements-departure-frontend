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

package models.journeyDomain.routeDetails.transit

import base.SpecBase
import commonTestUtils.UserAnswersSpecHelper
import generators.Generators
import models.DeclarationType.Option2
import models.domain.{EitherType, UserAnswersReader}
import models.reference.{CountryCode, CustomsOffice}
import org.scalacheck.Arbitrary.arbitrary
import pages.preTaskList.{DeclarationTypePage, OfficeOfDeparturePage}
import pages.routeDetails.routing.OfficeOfDestinationPage
import pages.routeDetails.transit._

class TransitDomainSpec extends SpecBase with UserAnswersSpecHelper with Generators {

  "TransitDomain" - {

    val countryCode     = arbitrary[CountryCode].sample.value
    val ctcCountryCodes = Seq(countryCode)
    def customsOffice   = arbitrary[CustomsOffice].sample.value.copy(countryId = countryCode)

    "can be parsed from UserAnswers" - {

      "when offices of departure and destination country codes are in set CL112 and both have same country code" in {
        val userAnswers = emptyUserAnswers
          .setValue(OfficeOfDeparturePage, customsOffice)
          .setValue(OfficeOfDestinationPage, customsOffice)
          .setValue(AddOfficeOfTransitYesNoPage, false)

        val expectedResult = TransitDomain(
          isT2DeclarationType = None,
          officesOfTransit = Nil
        )

        val result: EitherType[TransitDomain] = UserAnswersReader[TransitDomain](TransitDomain.userAnswersReader(ctcCountryCodes, Nil)).run(userAnswers)

        result.value mustBe expectedResult
      }

      "when T2 declaration type" ignore {
        val userAnswers = emptyUserAnswers
          .setValue(OfficeOfDeparturePage, customsOffice)
          .setValue(DeclarationTypePage, Option2)
          .setValue(OfficeOfDestinationPage, customsOffice)

        val expectedResult = TransitDomain(
          isT2DeclarationType = None,
          officesOfTransit = Nil
        )

        val result: EitherType[TransitDomain] = UserAnswersReader[TransitDomain](TransitDomain.userAnswersReader(Nil, Nil)).run(userAnswers)

        result.value mustBe expectedResult
      }

      "when T declaration type" - {

        "and some items are T2 declaration type" in {}

        "and no items are T2 declaration type" - {
          "and country code for office of departure or office of destination is in set CL112" in {}

          "and country code for neither office of departure nor office of destination is in set CL112" - {
            "and at least one country of routing is in set CL112" in {}

            "and no countries of routing are in set CL112" in {}
          }
        }
      }

      "when declaration type is neither T nor T2" - {
        "and country code for office of departure or office of destination is in set CL112" in {}

        "and country code for neither office of departure nor office of destination is in set CL112" - {
          "and at least one country of routing is in set CL112" in {}

          "and no countries of routing are in set CL112" in {}
        }
      }
    }

    "cannot be parsed from UserAnswers" - {

      "when offices of departure and destination are in CL112 and have same country code" - {
        "must return AddOfficeOfTransitYesNoPage" in {
          val userAnswers = emptyUserAnswers
            .setValue(OfficeOfDeparturePage, customsOffice)
            .setValue(OfficeOfDestinationPage, customsOffice)

          val result: EitherType[TransitDomain] = UserAnswersReader[TransitDomain](TransitDomain.userAnswersReader(ctcCountryCodes, Nil)).run(userAnswers)

          result.left.value.page mustBe AddOfficeOfTransitYesNoPage
        }
      }
    }
  }
}
