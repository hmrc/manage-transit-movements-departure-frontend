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
import commonTestUtils.UserAnswersSpecHelper
import generators.Generators
import models.DeclarationType
import models.DeclarationType._
import models.domain.{EitherType, UserAnswersReader}
import org.scalacheck.Arbitrary.arbitrary
import pages.preTaskList._
import pages.routeDetails.routing.BindingItineraryPage

class RoutingDomainSpec extends SpecBase with UserAnswersSpecHelper with Generators {

  "RoutingDomain" - {

    "can be parsed from UserAnswers" - {

      "when a TIR declaration" in {

        val userAnswers = emptyUserAnswers
          .unsafeSetVal(DeclarationTypePage)(Option4)
          .unsafeSetVal(BindingItineraryPage)(true)

        val expectedResult = RoutingDomain(
          bindingItinerary = true
        )

        val result: EitherType[RoutingDomain] = UserAnswersReader[RoutingDomain].run(userAnswers)

        result.value mustBe expectedResult
      }

      "when a non-TIR declaration" in {

        val declarationType = arbitrary[DeclarationType](arbitraryNonOption4DeclarationType).sample.value

        val userAnswers = emptyUserAnswers
          .unsafeSetVal(DeclarationTypePage)(declarationType)
          .unsafeSetVal(BindingItineraryPage)(true)

        val expectedResult = RoutingDomain(
          bindingItinerary = true
        )

        val result: EitherType[RoutingDomain] = UserAnswersReader[RoutingDomain].run(userAnswers)

        result.value mustBe expectedResult
      }
    }

    "cannot be parsed from UserAnswers" - {

      "when binding itinerary page is missing" in {

        val userAnswers = emptyUserAnswers

        val result: EitherType[RoutingDomain] = UserAnswersReader[RoutingDomain].run(userAnswers)

        result.left.value.page mustBe BindingItineraryPage
      }
    }
  }
}
