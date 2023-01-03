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

package models.journeyDomain.routeDetails.routing

import base.SpecBase
import generators.Generators
import models.domain.{EitherType, UserAnswersReader}
import models.reference.Country
import org.scalacheck.Arbitrary.arbitrary
import pages.routeDetails.routing.index.CountryOfRoutingPage

class CountryOfRoutingDomainSpec extends SpecBase with Generators {

  "CountryOfRoutingDomain" - {

    "can be parsed from UserAnswers" - {
      "when country of routing not answered at index" in {
        val country = arbitrary[Country].sample.value

        val userAnswers = emptyUserAnswers
          .setValue(CountryOfRoutingPage(index), country)

        val expectedResult = CountryOfRoutingDomain(
          country = country
        )(index)

        val result: EitherType[CountryOfRoutingDomain] = UserAnswersReader[CountryOfRoutingDomain](
          CountryOfRoutingDomain.userAnswersReader(index)
        ).run(userAnswers)

        result.value mustBe expectedResult
      }
    }

    "cannot be parsed from user answers" - {
      "when country of routing not answered at index" in {
        val userAnswers = emptyUserAnswers

        val result: EitherType[CountryOfRoutingDomain] = UserAnswersReader[CountryOfRoutingDomain](
          CountryOfRoutingDomain.userAnswersReader(index)
        ).run(userAnswers)

        result.left.value.page mustBe CountryOfRoutingPage(index)
      }
    }
  }
}
