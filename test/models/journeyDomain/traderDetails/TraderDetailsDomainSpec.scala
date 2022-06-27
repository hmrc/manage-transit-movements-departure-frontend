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

package models.journeyDomain.traderDetails

import base.SpecBase
import commonTestUtils.UserAnswersSpecHelper
import generators.Generators
import models.SecurityDetailsType.NoSecurityDetails
import models.domain.{EitherType, UserAnswersReader}
import models.{Address, DeclarationType}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import pages.preTaskList.{DeclarationTypePage, SecurityDetailsTypePage}
import pages.traderDetails.consignment._
import pages.traderDetails.consignment.consignee.MoreThanOneConsigneePage
import pages.traderDetails.{holderOfTransit, ActingAsRepresentativePage}

class TraderDetailsDomainSpec extends SpecBase with UserAnswersSpecHelper with Generators {

  "TraderDetailsDomain" - {

    val nonOption4DeclarationType = arbitrary[DeclarationType](arbitraryNonOption4DeclarationType).sample.value
    val holderOfTransitName       = Gen.alphaNumStr.sample.value
    val holderOfTransitAddress    = arbitrary[Address].sample.value

    "can be parsed from UserAnswers" - {

      "when has the minimum fields complete" in {

        val userAnswers = emptyUserAnswers
          .unsafeSetVal(DeclarationTypePage)(nonOption4DeclarationType)
          .unsafeSetVal(SecurityDetailsTypePage)(NoSecurityDetails)
          .unsafeSetVal(holderOfTransit.EoriYesNoPage)(false)
          .unsafeSetVal(holderOfTransit.NamePage)(holderOfTransitName)
          .unsafeSetVal(holderOfTransit.AddressPage)(holderOfTransitAddress)
          .unsafeSetVal(holderOfTransit.AddContactPage)(false)
          .unsafeSetVal(ActingAsRepresentativePage)(false)
          .unsafeSetVal(ApprovedOperatorPage)(true)
          .unsafeSetVal(MoreThanOneConsigneePage)(true)

        val expectedResult = TraderDetailsDomain(
          holderOfTransit = HolderOfTransitEori(
            eori = None,
            name = holderOfTransitName,
            address = holderOfTransitAddress,
            additionalContact = None
          ),
          representative = None,
          consignment = ConsignmentDomain(
            consignor = None,
            consignee = None
          )
        )

        val result: EitherType[TraderDetailsDomain] = UserAnswersReader[TraderDetailsDomain].run(userAnswers)

        result.value mustBe expectedResult
      }
    }

    "cannot be parsed from UserAnswers" - {

      "when ActingAsRepresentativePage is missing" in {

        val userAnswers = emptyUserAnswers
          .unsafeSetVal(DeclarationTypePage)(DeclarationType.Option4)
          .unsafeSetVal(SecurityDetailsTypePage)(NoSecurityDetails)
          .unsafeSetVal(holderOfTransit.TirIdentificationYesNoPage)(false)
          .unsafeSetVal(holderOfTransit.NamePage)(holderOfTransitName)
          .unsafeSetVal(holderOfTransit.AddressPage)(holderOfTransitAddress)
          .unsafeSetVal(holderOfTransit.AddContactPage)(false)

        val result: EitherType[TraderDetailsDomain] = UserAnswersReader[TraderDetailsDomain].run(userAnswers)

        result.left.value.page mustBe ActingAsRepresentativePage
      }
    }
  }
}
