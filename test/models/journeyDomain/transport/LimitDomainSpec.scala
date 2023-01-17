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

package models.journeyDomain.transport

import base.SpecBase
import generators.Generators
import models.domain.{EitherType, UserAnswersReader}
import pages.transport.authorisationsAndLimit.limit.LimitDatePage

import java.time.LocalDate

class LimitDomainSpec extends SpecBase with Generators {

  "LimitDomainSpec" - {

    val date = LocalDate.now

    "can be parsed from UserAnswers" - {

      "when limit date is provided" in {

        val userAnswers = emptyUserAnswers
          .setValue(LimitDatePage, date)

        val expectedResult = LimitDomain(
          limitDate = date
        )

        val result: EitherType[LimitDomain] = UserAnswersReader[LimitDomain](LimitDomain.userAnswersReader)
          .run(userAnswers)

        result.value mustBe expectedResult
      }

    }

    "can not be parsed from UserAnswers" - {

      "when limit date is not provided" in {

        val result: EitherType[LimitDomain] = UserAnswersReader[LimitDomain](LimitDomain.userAnswersReader)
          .run(emptyUserAnswers)

        result.left.value.page mustBe LimitDatePage
      }

    }
  }
}
