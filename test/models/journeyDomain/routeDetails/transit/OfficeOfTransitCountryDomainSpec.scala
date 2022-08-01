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

package models.JourneyDomain.routeDetails.transit

import base.SpecBase
import generators.Generators
import models.domain.{EitherType, UserAnswersReader}
import models.journeyDomain.routeDetails.transit.OfficeOfTransitCountryDomain
import models.reference.{Country, CustomsOffice}
import org.scalacheck.Arbitrary.arbitrary
import pages.routeDetails.transit._

class OfficeOfTransitCountryDomainSpec extends SpecBase with Generators {

  "OfficeOfTransitCountryDomain" - {

    "can be parsed from UserAnswers" - {
      "when all data answered at index and we require ETA index" in {
        val country                    = arbitrary[Country].sample.value
        val customsOffice              = arbitrary[CustomsOffice].sample.value
        val addOfficeOfTransitETAYesNo = true
        val officeOfTransitETA         = arbitraryLocalDateTime.arbitrary.sample.get

        val userAnswers = emptyUserAnswers
          .setValue(OfficeOfTransitCountryPage(index), country)
          .setValue(OfficeOfTransitPage(index), customsOffice)
          .setValue(AddOfficeOfTransitETAYesNoPage(index), addOfficeOfTransitETAYesNo)
          .setValue(OfficeOfTransitETAPage(index), officeOfTransitETA)

        val expectedResult = OfficeOfTransitCountryDomain(
          country = country,
          customsOffice = customsOffice,
          addOfficeOfTransitETA = addOfficeOfTransitETAYesNo,
          officeOfTransitETA = Some(officeOfTransitETA)
        )(index)

        val result: EitherType[OfficeOfTransitCountryDomain] = UserAnswersReader[OfficeOfTransitCountryDomain](
          OfficeOfTransitCountryDomain.userAnswersReader(index)
        ).run(userAnswers)

        result.value mustBe expectedResult
      }

      "when all data answered at index and we don't require ETA index" in {
        val country                    = arbitrary[Country].sample.value
        val customsOffice              = arbitrary[CustomsOffice].sample.value
        val addOfficeOfTransitETAYesNo = false

        val userAnswers = emptyUserAnswers
          .setValue(OfficeOfTransitCountryPage(index), country)
          .setValue(OfficeOfTransitPage(index), customsOffice)
          .setValue(AddOfficeOfTransitETAYesNoPage(index), addOfficeOfTransitETAYesNo)

        val expectedResult = OfficeOfTransitCountryDomain(
          country = country,
          customsOffice = customsOffice,
          addOfficeOfTransitETA = addOfficeOfTransitETAYesNo,
          officeOfTransitETA = None
        )(index)

        val result: EitherType[OfficeOfTransitCountryDomain] = UserAnswersReader[OfficeOfTransitCountryDomain](
          OfficeOfTransitCountryDomain.userAnswersReader(index)
        ).run(userAnswers)

        result.value mustBe expectedResult
      }
    }

    "cannot be parsed from user answers" - {
      "when office of transit country not answered at index" in {
        val userAnswers = emptyUserAnswers

        val result: EitherType[OfficeOfTransitCountryDomain] = UserAnswersReader[OfficeOfTransitCountryDomain](
          OfficeOfTransitCountryDomain.userAnswersReader(index)
        ).run(userAnswers)

        result.left.value.page mustBe OfficeOfTransitCountryPage(index)
      }

      "when office of transit not answered at index" in {
        val country = arbitrary[Country].sample.value

        val userAnswers = emptyUserAnswers
          .setValue(OfficeOfTransitCountryPage(index), country)

        val result: EitherType[OfficeOfTransitCountryDomain] = UserAnswersReader[OfficeOfTransitCountryDomain](
          OfficeOfTransitCountryDomain.userAnswersReader(index)
        ).run(userAnswers)

        result.left.value.page mustBe OfficeOfTransitPage(index)
      }

      "when add office of transit eta not answered at index" in {
        val country       = arbitrary[Country].sample.value
        val customsOffice = arbitrary[CustomsOffice].sample.value

        val userAnswers = emptyUserAnswers
          .setValue(OfficeOfTransitCountryPage(index), country)
          .setValue(OfficeOfTransitPage(index), customsOffice)

        val result: EitherType[OfficeOfTransitCountryDomain] = UserAnswersReader[OfficeOfTransitCountryDomain](
          OfficeOfTransitCountryDomain.userAnswersReader(index)
        ).run(userAnswers)

        result.left.value.page mustBe AddOfficeOfTransitETAYesNoPage(index)
      }

      "when add of transit eta not answered at index" in {
        val country                    = arbitrary[Country].sample.value
        val customsOffice              = arbitrary[CustomsOffice].sample.value
        val addOfficeOfTransitETAYesNo = true

        val userAnswers = emptyUserAnswers
          .setValue(OfficeOfTransitCountryPage(index), country)
          .setValue(OfficeOfTransitPage(index), customsOffice)
          .setValue(AddOfficeOfTransitETAYesNoPage(index), addOfficeOfTransitETAYesNo)
        val result: EitherType[OfficeOfTransitCountryDomain] = UserAnswersReader[OfficeOfTransitCountryDomain](
          OfficeOfTransitCountryDomain.userAnswersReader(index)
        ).run(userAnswers)

        result.left.value.page mustBe OfficeOfTransitETAPage(index)
      }
    }
  }
}
