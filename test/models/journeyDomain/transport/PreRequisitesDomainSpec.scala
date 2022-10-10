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
import models.domain.{EitherType, UserAnswersReader}
import org.scalacheck.Gen
import pages.QuestionPage
import pages.transport.preRequisites._

class PreRequisitesDomainSpec extends SpecBase with Generators {

  private val UCR = Gen.alphaNumStr.sample.value

  "PreRequisitesDomain" - {

    "can be parsed from user answers" - {
      "when using same UCR for all items" in {
        val userAnswers = emptyUserAnswers
          .setValue(SameUcrYesNoPage, true)
          .setValue(UniqueConsignmentReferencePage, UCR)

        val expectedResult = PreRequisitesDomain(
          ucr = Some(UCR)
        )

        val result: EitherType[PreRequisitesDomain] = UserAnswersReader[PreRequisitesDomain].run(userAnswers)

        result.value mustBe expectedResult
      }
    }

    "can not be parsed from user answers" - {
      "when answers are empty" in {

        val mandatoryPages: Seq[QuestionPage[_]] = Seq(
          SameUcrYesNoPage,
          UniqueConsignmentReferencePage
        )

        val userAnswers = emptyUserAnswers
          .setValue(SameUcrYesNoPage, true)
          .setValue(UniqueConsignmentReferencePage, UCR)

        mandatoryPages.map {
          mandatoryPage =>
            val updatedAnswers = userAnswers.removeValue(mandatoryPage)

            val result: EitherType[PreRequisitesDomain] = UserAnswersReader[PreRequisitesDomain](PreRequisitesDomain.userAnswersReader).run(updatedAnswers)

            result.left.value.page mustBe mandatoryPage
        }
      }
    }
  }
}
