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

package models.journeyDomain.transport

import base.SpecBase
import generators.Generators
import models.DeclarationType
import models.domain.{EitherType, UserAnswersReader}
import org.scalacheck.Arbitrary.arbitrary
import pages.preTaskList.DeclarationTypePage
import pages.transport.preRequisites._

class PreRequisitesDomainSpec extends SpecBase with Generators {

  "PreRequisitesDomain" - {

    "can be parsed from user answers" - {
      "when using same UCR for all items" in {
        val declarationType = arbitrary[DeclarationType](arbitraryNonOption4DeclarationType).sample.value

        val userAnswers = emptyUserAnswers
          .setValue(DeclarationTypePage, declarationType)
          .setValue(SameUcrYesNoPage, true)

        val expectedResult = PreRequisitesDomain(
          ucr = Some(""),
          countryOfDispatch = None
        )

        val result: EitherType[PreRequisitesDomain] = UserAnswersReader[PreRequisitesDomain].run(userAnswers)

        result.value mustBe expectedResult
      }
    }

    "can not be parsed from user answers" - {
      "when answers are empty" in {
        val result: EitherType[PreRequisitesDomain] = UserAnswersReader[PreRequisitesDomain].run(emptyUserAnswers)

        result.left.value.page mustBe SameUcrYesNoPage
      }
    }
  }
}
