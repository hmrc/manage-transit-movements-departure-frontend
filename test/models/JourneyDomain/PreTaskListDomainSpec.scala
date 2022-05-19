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

package models.JourneyDomain

import base.SpecBase
import commonTestUtils.UserAnswersSpecHelper
import generators.Generators
import models.DeclarationType._
import models.ProcedureType.Normal
import models.SecurityDetailsType.NoSecurityDetails
import models.domain.{EitherType, UserAnswersReader}
import models.journeyDomain.PreTaskListDomain
import models.reference.{CountryCode, CustomsOffice}
import models.{DeclarationType, ProcedureType, SecurityDetailsType}
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.forAll
import pages.QuestionPage
import pages.preTaskList._

class PreTaskListDomainSpec extends SpecBase with UserAnswersSpecHelper with Generators {

  "PreTaskListDomain" - {

    "can be parsed from UserAnswers" - {

      "when a TIR declaration" in {

        val securityDetails = Gen.oneOf(SecurityDetailsType.values).sample.value

        val tirUserAnswers = emptyUserAnswers
          .unsafeSetVal(OfficeOfDeparturePage)(CustomsOffice("id", "name", CountryCode("code"), None))
          .unsafeSetVal(ProcedureTypePage)(Normal)
          .unsafeSetVal(SecurityDetailsTypePage)(securityDetails)
          .unsafeSetVal(DeclarationTypePage)(Option4)
          .unsafeSetVal(TIRCarnetReferencePage)("tirCarnetReference")

        val expectedResult = PreTaskListDomain(
          emptyUserAnswers.lrn,
          CustomsOffice("id", "name", CountryCode("code"), None),
          Normal,
          Option4,
          Some("tirCarnetReference"),
          securityDetails
        )

        val result: EitherType[PreTaskListDomain] = UserAnswersReader[PreTaskListDomain].run(tirUserAnswers)

        result.value mustBe expectedResult
      }

      "when a none TIR declaration" in {

        val procedureType   = Gen.oneOf(ProcedureType.values).sample.value
        val declarationType = Gen.oneOf(Option1, Option2, Option3, Option5).sample.value
        val securityDetails = Gen.oneOf(SecurityDetailsType.values).sample.value

        val userAnswers = emptyUserAnswers
          .unsafeSetVal(OfficeOfDeparturePage)(CustomsOffice("id", "name", CountryCode("code"), None))
          .unsafeSetVal(ProcedureTypePage)(procedureType)
          .unsafeSetVal(SecurityDetailsTypePage)(securityDetails)
          .unsafeSetVal(DeclarationTypePage)(declarationType)

        val expectedResult = PreTaskListDomain(
          emptyUserAnswers.lrn,
          CustomsOffice("id", "name", CountryCode("code"), None),
          procedureType,
          declarationType,
          None,
          securityDetails
        )

        val result: EitherType[PreTaskListDomain] = UserAnswersReader[PreTaskListDomain].run(userAnswers)

        result.value mustBe expectedResult

      }
    }

    "cannot be parsed from UserAnswers" - {

      "when a TIR declaration without TIRCarnetReferece" in {

        val securityDetails = Gen.oneOf(SecurityDetailsType.values).sample.value

        val tirUserAnswers = emptyUserAnswers
          .unsafeSetVal(OfficeOfDeparturePage)(CustomsOffice("id", "name", CountryCode("code"), None))
          .unsafeSetVal(ProcedureTypePage)(Normal)
          .unsafeSetVal(SecurityDetailsTypePage)(securityDetails)
          .unsafeSetVal(DeclarationTypePage)(Option4)

        val result: EitherType[PreTaskListDomain] = UserAnswersReader[PreTaskListDomain].run(tirUserAnswers)

        result.left.value.page mustBe TIRCarnetReferencePage
      }

      "when any other mandatory page is missing" in {

        val mandatoryPages: Gen[QuestionPage[_]] = Gen.oneOf(
          OfficeOfDeparturePage,
          ProcedureTypePage,
          SecurityDetailsTypePage,
          DeclarationTypePage
        )

        val userAnswers = emptyUserAnswers
          .unsafeSetVal(OfficeOfDeparturePage)(CustomsOffice("id", "name", CountryCode("code"), None))
          .unsafeSetVal(ProcedureTypePage)(Normal)
          .unsafeSetVal(SecurityDetailsTypePage)(NoSecurityDetails)
          .unsafeSetVal(DeclarationTypePage)(Option1)

        forAll(mandatoryPages) {
          mandatoryPage =>
            val invalidUserAnswers = userAnswers.unsafeRemove(mandatoryPage)

            val result: EitherType[PreTaskListDomain] = UserAnswersReader[PreTaskListDomain].run(invalidUserAnswers)

            result.left.value.page mustBe mandatoryPage
        }
      }
    }
  }
}
