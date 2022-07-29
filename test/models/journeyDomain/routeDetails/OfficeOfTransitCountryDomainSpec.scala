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

package models.journeyDomain.routeDetails

import base.SpecBase
import generators.Generators
import models.domain.{EitherType, UserAnswersReader}
import models.reference.Country
import org.scalacheck.Arbitrary.arbitrary
import pages.routeDetails.transit.OfficeOfTransitCountryPage

class OfficeOfTransitCountryDomainSpec extends SpecBase with Generators {

  "OfficeOfTransitCountryDomain" - {

    "can be parsed from UserAnswers" - {
      "when office of transit country not answered at index" in {
        val country = arbitrary[Country].sample.value

        val userAnswers = emptyUserAnswers
          .setValue(OfficeOfTransitCountryPage(index), country)

        val expectedResult = OfficeOfTransitCountryDomain(
          country = country
        )(index)

        val result: EitherType[OfficeOfTransitCountryDomain] = UserAnswersReader[OfficeOfTransitCountryDomain](
          OfficeOfTransitCountryDomain.userAnswersReader(index)
        ).run(userAnswers)

        result.value mustBe expectedResult
      }
    }

    "cannot be parsed from user answers" - {
      "when country of routing not answered at index" ignore {
        val userAnswers = emptyUserAnswers

        val result: EitherType[OfficeOfTransitCountryDomain] = UserAnswersReader[OfficeOfTransitCountryDomain](
          OfficeOfTransitCountryDomain.userAnswersReader(index)
        ).run(userAnswers)

        result.left.value.page mustBe OfficeOfTransitCountryPage(index)
      }
    }
  }
}
