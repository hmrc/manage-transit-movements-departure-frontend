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

package models.JourneyDomain.traderDetails

import base.SpecBase
import commonTestUtils.UserAnswersSpecHelper
import generators.Generators
import models.domain.{EitherType, UserAnswersReader}
import models.journeyDomain.traderDetails.ConsignmentDomain
import pages.traderDetails.consignment._

class ConsignmentDomainSpec extends SpecBase with UserAnswersSpecHelper with Generators {

  "ConsignmentDomain" - {

    "can be parsed from UserAnswers" - {

      "when has all consignment fields complete" in {

        val userAnswers = emptyUserAnswers
          .unsafeSetVal(ApprovedOperatorPage)(false)
          .unsafeSetVal(consignee.MoreThanOneConsigneePage)(true)

        val expectedResult = ConsignmentDomain(
          consignor = None,
          consignee = None
        )

        val result: EitherType[ConsignmentDomain] = UserAnswersReader[ConsignmentDomain].run(userAnswers)

        result.value mustBe expectedResult
      }
    }

    "cannot be parsed from UserAnswer" - {

      "when ApprovedOperatorPage type is missing" in {

        val userAnswers = emptyUserAnswers

        val result: EitherType[ConsignmentDomain] = UserAnswersReader[ConsignmentDomain].run(userAnswers)

        result.left.value.page mustBe ApprovedOperatorPage
      }
    }
  }
}
