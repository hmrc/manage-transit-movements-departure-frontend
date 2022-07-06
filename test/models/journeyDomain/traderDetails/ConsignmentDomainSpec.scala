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
import models.{Address, DeclarationType}
import models.SecurityDetailsType.{EntrySummaryDeclarationSecurityDetails, NoSecurityDetails}
import models.domain.{EitherType, UserAnswersReader}
import models.journeyDomain.traderDetails.{ConsignmentConsignorDomain, ConsignmentDomain}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import pages.preTaskList.{DeclarationTypePage, SecurityDetailsTypePage}
import pages.traderDetails.consignment._

class ConsignmentDomainSpec extends SpecBase with UserAnswersSpecHelper with Generators {

  "ConsignmentDomain" - {
    val consignorName    = Gen.alphaNumStr.sample.value
    val consignorAddress = arbitrary[Address].sample.value

    "can be parsed from UserAnswers" - {

      "when has the minimum consignment fields complete" in {

        val userAnswers = emptyUserAnswers
          .unsafeSetVal(DeclarationTypePage)(DeclarationType.Option1)
          .unsafeSetVal(SecurityDetailsTypePage)(NoSecurityDetails)
          .unsafeSetVal(ApprovedOperatorPage)(true)
          .unsafeSetVal(MoreThanOneConsigneePage)(true)

        val expectedResult = ConsignmentDomain(
          consignor = None,
          consignee = None
        )

        val result: EitherType[ConsignmentDomain] = UserAnswersReader[ConsignmentDomain].run(userAnswers)

        result.value mustBe expectedResult
      }

      "when the consignor fields are complete" in {

        val userAnswers = emptyUserAnswers
          .unsafeSetVal(DeclarationTypePage)(DeclarationType.Option1)
          .unsafeSetVal(SecurityDetailsTypePage)(EntrySummaryDeclarationSecurityDetails)
          .unsafeSetVal(ApprovedOperatorPage)(true)
          .unsafeSetVal(consignor.EoriYesNoPage)(false)
          .unsafeSetVal(consignor.NamePage)(consignorName)
          .unsafeSetVal(consignor.AddressPage)(consignorAddress)
          .unsafeSetVal(consignor.AddContactPage)(false)
          .unsafeSetVal(MoreThanOneConsigneePage)(true)

        val consignorDomain = ConsignmentConsignorDomain(
          eori = None,
          name = consignorName,
          address = consignorAddress,
          contact = None
        )
        val expectedResult = ConsignmentDomain(
          consignor = Some(consignorDomain),
          consignee = None
        )

        val result: EitherType[ConsignmentDomain] = UserAnswersReader[ConsignmentDomain].run(userAnswers)

        result.value mustBe expectedResult
      }

      "when the consignor fields are populated but we don't want security details but have the ApprovedOperatorPage as Yes" in {

        val userAnswers = emptyUserAnswers
          .unsafeSetVal(DeclarationTypePage)(DeclarationType.Option1)
          .unsafeSetVal(SecurityDetailsTypePage)(NoSecurityDetails)
          .unsafeSetVal(ApprovedOperatorPage)(true)
          .unsafeSetVal(MoreThanOneConsigneePage)(true)

        val expectedResult = ConsignmentDomain(
          consignor = None,
          consignee = None
        )

        val result: EitherType[ConsignmentDomain] = UserAnswersReader[ConsignmentDomain].run(userAnswers)

        result.value mustBe expectedResult
      }

      "when the consignor fields are populated we do not want security details and have the ApprovedOperatorPage as No" in {

        val userAnswers = emptyUserAnswers
          .unsafeSetVal(DeclarationTypePage)(DeclarationType.Option1)
          .unsafeSetVal(SecurityDetailsTypePage)(NoSecurityDetails)
          .unsafeSetVal(ApprovedOperatorPage)(false)
          .unsafeSetVal(consignor.EoriYesNoPage)(false)
          .unsafeSetVal(consignor.NamePage)(consignorName)
          .unsafeSetVal(consignor.AddressPage)(consignorAddress)
          .unsafeSetVal(consignor.AddContactPage)(false)
          .unsafeSetVal(MoreThanOneConsigneePage)(true)

        val consignorDomain = ConsignmentConsignorDomain(
          eori = None,
          name = consignorName,
          address = consignorAddress,
          contact = None
        )
        val expectedResult = ConsignmentDomain(
          consignor = Some(consignorDomain),
          consignee = None
        )

        val result: EitherType[ConsignmentDomain] = UserAnswersReader[ConsignmentDomain].run(userAnswers)

        result.value mustBe expectedResult
      }

      "when the consignor fields are populated but we don't want security details but have the ApprovedOperatorPage " +
        "as Yes, but we have an option4 declarationType" in {

          val userAnswers = emptyUserAnswers
            .unsafeSetVal(DeclarationTypePage)(DeclarationType.Option4)
            .unsafeSetVal(SecurityDetailsTypePage)(NoSecurityDetails)
            .unsafeSetVal(consignor.EoriYesNoPage)(false)
            .unsafeSetVal(consignor.NamePage)(consignorName)
            .unsafeSetVal(consignor.AddressPage)(consignorAddress)
            .unsafeSetVal(consignor.AddContactPage)(false)
            .unsafeSetVal(MoreThanOneConsigneePage)(true)

          val consignorDomain = ConsignmentConsignorDomain(
            eori = None,
            name = consignorName,
            address = consignorAddress,
            contact = None
          )
          val expectedResult = ConsignmentDomain(
            consignor = Some(consignorDomain),
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

        result.left.value.page mustBe DeclarationTypePage
      }
    }
  }
}
