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

package models.journeyDomain

import base.SpecBase
import commonTestUtils.UserAnswersSpecHelper
import generators.Generators
import models.ProcedureType
import models.ProcedureType.Normal
import models.reference._
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import pages.preTaskList._
import pages.sections.PreTaskListSection

class PreTaskListDomainSpec extends SpecBase with UserAnswersSpecHelper with Generators {

  "PreTaskListDomain" - {

    val gbCustomsOffice           = CustomsOffice("GB1", "Dover", None, "GB")
    val xiCustomsOffice           = CustomsOffice("XI1", "Belfast", None, "XI")
    val carnetRef                 = Gen.alphaNumStr.sample.value
    val additionalDeclarationType = arbitrary[AdditionalDeclarationType].sample.value
    val procedureType             = arbitrary[ProcedureType].sample.value
    val securityDetails           = arbitrary[SecurityType].sample.value
    val nonTirDeclarationType     = arbitrary[DeclarationType](arbitraryNonTIRDeclarationType).sample.value
    val tirDeclarationType        = arbitrary[DeclarationType](arbitraryTIRDeclarationType).sample.value

    "can be parsed from UserAnswers" - {

      "when a TIR declaration" in {

        val tirUserAnswers = emptyUserAnswers
          .unsafeSetVal(AdditionalDeclarationTypePage)(additionalDeclarationType)
          .unsafeSetVal(OfficeOfDeparturePage)(xiCustomsOffice)
          .unsafeSetVal(ProcedureTypePage)(Normal)
          .unsafeSetVal(DeclarationTypePage)(tirDeclarationType)
          .unsafeSetVal(TIRCarnetReferencePage)(carnetRef)
          .unsafeSetVal(SecurityDetailsTypePage)(securityDetails)

        val expectedResult = PreTaskListDomain(
          localReferenceNumber = emptyUserAnswers.lrn,
          additionalDeclarationType = additionalDeclarationType,
          officeOfDeparture = xiCustomsOffice,
          procedureType = Normal,
          declarationType = tirDeclarationType,
          tirCarnetReference = Some(carnetRef),
          securityDetailsType = securityDetails
        )

        val result = UserAnswersReader[PreTaskListDomain].run(tirUserAnswers)

        result.value.value mustBe expectedResult
        result.value.pages mustBe Seq(
          AdditionalDeclarationTypePage,
          OfficeOfDeparturePage,
          ProcedureTypePage,
          DeclarationTypePage,
          TIRCarnetReferencePage,
          SecurityDetailsTypePage,
          PreTaskListSection
        )
      }

      "when a non-TIR declaration" in {

        val userAnswers = emptyUserAnswers
          .unsafeSetVal(AdditionalDeclarationTypePage)(additionalDeclarationType)
          .unsafeSetVal(OfficeOfDeparturePage)(gbCustomsOffice)
          .unsafeSetVal(ProcedureTypePage)(procedureType)
          .unsafeSetVal(DeclarationTypePage)(nonTirDeclarationType)
          .unsafeSetVal(SecurityDetailsTypePage)(securityDetails)

        val expectedResult = PreTaskListDomain(
          localReferenceNumber = emptyUserAnswers.lrn,
          additionalDeclarationType = additionalDeclarationType,
          officeOfDeparture = gbCustomsOffice,
          procedureType = procedureType,
          declarationType = nonTirDeclarationType,
          tirCarnetReference = None,
          securityDetailsType = securityDetails
        )

        val result = UserAnswersReader[PreTaskListDomain].run(userAnswers)

        result.value.value mustBe expectedResult
        result.value.pages mustBe Seq(
          AdditionalDeclarationTypePage,
          OfficeOfDeparturePage,
          ProcedureTypePage,
          DeclarationTypePage,
          SecurityDetailsTypePage,
          PreTaskListSection
        )
      }
    }

    "cannot be parsed from UserAnswers" - {

      "when a TIR declaration without TIRCarnetReferece" in {

        val tirUserAnswers = emptyUserAnswers
          .unsafeSetVal(AdditionalDeclarationTypePage)(additionalDeclarationType)
          .unsafeSetVal(OfficeOfDeparturePage)(xiCustomsOffice)
          .unsafeSetVal(ProcedureTypePage)(Normal)
          .unsafeSetVal(DeclarationTypePage)(tirDeclarationType)
          .unsafeSetVal(SecurityDetailsTypePage)(securityDetails)

        val result = UserAnswersReader[PreTaskListDomain].run(tirUserAnswers)

        result.left.value.page mustBe TIRCarnetReferencePage
        result.left.value.pages mustBe Seq(
          AdditionalDeclarationTypePage,
          OfficeOfDeparturePage,
          ProcedureTypePage,
          DeclarationTypePage,
          TIRCarnetReferencePage
        )
      }

      "when a TIR with a GB customs office" in {

        val tirUserAnswers = emptyUserAnswers
          .unsafeSetVal(AdditionalDeclarationTypePage)(additionalDeclarationType)
          .unsafeSetVal(OfficeOfDeparturePage)(gbCustomsOffice)
          .unsafeSetVal(ProcedureTypePage)(Normal)
          .unsafeSetVal(DeclarationTypePage)(tirDeclarationType)
          .unsafeSetVal(TIRCarnetReferencePage)(carnetRef)
          .unsafeSetVal(SecurityDetailsTypePage)(securityDetails)

        val result = UserAnswersReader[PreTaskListDomain].run(tirUserAnswers)

        result.left.value.page mustBe DeclarationTypePage
        result.left.value.pages mustBe Seq(
          AdditionalDeclarationTypePage,
          OfficeOfDeparturePage,
          ProcedureTypePage,
          DeclarationTypePage
        )
      }
    }
  }
}
