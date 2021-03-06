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

package models.journeyDomain

import base.SpecBase
import commonTestUtils.UserAnswersSpecHelper
import generators.Generators
import models.DeclarationType._
import models.ProcedureType.Normal
import models.SecurityDetailsType.NoSecurityDetails
import models.domain.{EitherType, UserAnswersReader}
import models.reference.CustomsOffice
import models.{DeclarationType, ProcedureType, SecurityDetailsType}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.forAll
import pages.QuestionPage
import pages.preTaskList._

class PreTaskListDomainSpec extends SpecBase with UserAnswersSpecHelper with Generators {

  "PreTaskListDomain" - {

    val gbCustomsOffice           = arbitrary[CustomsOffice](arbitraryGbCustomsOffice).sample.value
    val xiCustomsOffice           = arbitrary[CustomsOffice](arbitraryXiCustomsOffice).sample.value
    val carnetRef                 = Gen.alphaNumStr.sample.value
    val procedureType             = arbitrary[ProcedureType].sample.value
    val securityDetails           = arbitrary[SecurityDetailsType].sample.value
    val nonOption4DeclarationType = arbitrary[DeclarationType](arbitraryNonOption4DeclarationType).sample.value
    val detailsConfirmed          = true

    "can be parsed from UserAnswers" - {

      "when a TIR declaration" in {

        val tirUserAnswers = emptyUserAnswers
          .unsafeSetVal(OfficeOfDeparturePage)(xiCustomsOffice)
          .unsafeSetVal(ProcedureTypePage)(Normal)
          .unsafeSetVal(DeclarationTypePage)(Option4)
          .unsafeSetVal(TIRCarnetReferencePage)(carnetRef)
          .unsafeSetVal(SecurityDetailsTypePage)(securityDetails)
          .unsafeSetVal(DetailsConfirmedPage)(detailsConfirmed)

        val expectedResult = PreTaskListDomain(
          localReferenceNumber = emptyUserAnswers.lrn,
          officeOfDeparture = xiCustomsOffice,
          procedureType = Normal,
          declarationType = Option4,
          tirCarnetReference = Some(carnetRef),
          securityDetailsType = securityDetails,
          detailsConfirmed = detailsConfirmed
        )

        val result: EitherType[PreTaskListDomain] = UserAnswersReader[PreTaskListDomain].run(tirUserAnswers)

        result.value mustBe expectedResult
      }

      "when a non-TIR declaration" in {

        val userAnswers = emptyUserAnswers
          .unsafeSetVal(OfficeOfDeparturePage)(gbCustomsOffice)
          .unsafeSetVal(ProcedureTypePage)(procedureType)
          .unsafeSetVal(DeclarationTypePage)(nonOption4DeclarationType)
          .unsafeSetVal(SecurityDetailsTypePage)(securityDetails)
          .unsafeSetVal(DetailsConfirmedPage)(detailsConfirmed)

        val expectedResult = PreTaskListDomain(
          localReferenceNumber = emptyUserAnswers.lrn,
          officeOfDeparture = gbCustomsOffice,
          procedureType = procedureType,
          declarationType = nonOption4DeclarationType,
          tirCarnetReference = None,
          securityDetailsType = securityDetails,
          detailsConfirmed = detailsConfirmed
        )

        val result: EitherType[PreTaskListDomain] = UserAnswersReader[PreTaskListDomain].run(userAnswers)

        result.value mustBe expectedResult

      }
    }

    "cannot be parsed from UserAnswers" - {

      "when a TIR declaration without TIRCarnetReferece" in {

        val tirUserAnswers = emptyUserAnswers
          .unsafeSetVal(OfficeOfDeparturePage)(xiCustomsOffice)
          .unsafeSetVal(ProcedureTypePage)(Normal)
          .unsafeSetVal(DeclarationTypePage)(Option4)
          .unsafeSetVal(SecurityDetailsTypePage)(securityDetails)
          .unsafeSetVal(DetailsConfirmedPage)(true)

        val result: EitherType[PreTaskListDomain] = UserAnswersReader[PreTaskListDomain].run(tirUserAnswers)

        result.left.value.page mustBe TIRCarnetReferencePage
      }

      "when a TIR with a GB customs office" in {

        val tirUserAnswers = emptyUserAnswers
          .unsafeSetVal(OfficeOfDeparturePage)(gbCustomsOffice)
          .unsafeSetVal(ProcedureTypePage)(Normal)
          .unsafeSetVal(DeclarationTypePage)(Option4)
          .unsafeSetVal(TIRCarnetReferencePage)(carnetRef)
          .unsafeSetVal(SecurityDetailsTypePage)(securityDetails)
          .unsafeSetVal(DetailsConfirmedPage)(true)

        val result: EitherType[PreTaskListDomain] = UserAnswersReader[PreTaskListDomain].run(tirUserAnswers)

        result.left.value.page mustBe DeclarationTypePage
      }

      "when details not confirmed" - {

        "when false" in {

          val userAnswers = emptyUserAnswers
            .unsafeSetVal(OfficeOfDeparturePage)(gbCustomsOffice)
            .unsafeSetVal(ProcedureTypePage)(procedureType)
            .unsafeSetVal(DeclarationTypePage)(nonOption4DeclarationType)
            .unsafeSetVal(SecurityDetailsTypePage)(securityDetails)
            .unsafeSetVal(DetailsConfirmedPage)(false)

          val result: EitherType[PreTaskListDomain] = UserAnswersReader[PreTaskListDomain].run(userAnswers)

          result.left.value.page mustBe DetailsConfirmedPage
        }

        "when undefined" in {

          val userAnswers = emptyUserAnswers
            .unsafeSetVal(OfficeOfDeparturePage)(gbCustomsOffice)
            .unsafeSetVal(ProcedureTypePage)(procedureType)
            .unsafeSetVal(DeclarationTypePage)(nonOption4DeclarationType)
            .unsafeSetVal(SecurityDetailsTypePage)(securityDetails)

          val result: EitherType[PreTaskListDomain] = UserAnswersReader[PreTaskListDomain].run(userAnswers)

          result.left.value.page mustBe DetailsConfirmedPage
        }
      }

      "when any other mandatory page is missing" in {

        val mandatoryPages: Gen[QuestionPage[_]] = Gen.oneOf(
          OfficeOfDeparturePage,
          ProcedureTypePage,
          DeclarationTypePage,
          SecurityDetailsTypePage,
          DetailsConfirmedPage
        )

        val userAnswers = emptyUserAnswers
          .unsafeSetVal(OfficeOfDeparturePage)(gbCustomsOffice)
          .unsafeSetVal(ProcedureTypePage)(Normal)
          .unsafeSetVal(DeclarationTypePage)(Option1)
          .unsafeSetVal(SecurityDetailsTypePage)(NoSecurityDetails)
          .unsafeSetVal(DetailsConfirmedPage)(true)

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
