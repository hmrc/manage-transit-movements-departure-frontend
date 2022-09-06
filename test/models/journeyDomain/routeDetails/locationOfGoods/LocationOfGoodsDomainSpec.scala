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

package models.journeyDomain.routeDetails.locationOfGoods

import base.SpecBase
import commonTestUtils.UserAnswersSpecHelper
import generators.Generators
import models.domain.{EitherType, UserAnswersReader}
import models.journeyDomain.routeDetails.locationOfGoods.LocationOfGoodsDomain._
import models.reference.CustomsOffice
import models.{LocationOfGoodsIdentification, LocationType}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import pages.routeDetails.locationOfGoods._

class LocationOfGoodsDomainSpec extends SpecBase with UserAnswersSpecHelper with Generators {

  private val typeOfLocation = arbitrary[LocationType].sample.value

  "LocationOfGoodsDomain" - {

    "can be parsed from UserAnswers" - {

      "when qualifier of identification" - {

        "is V (Customs office identifier)" in {
          val customsOffice = arbitrary[CustomsOffice].sample.value

          val userAnswers = emptyUserAnswers
            .setValue(LocationOfGoodsTypePage, typeOfLocation)
            .setValue(LocationOfGoodsIdentificationPage, LocationOfGoodsIdentification.CustomsOfficeIdentifier)
            .setValue(LocationOfGoodsCustomsOfficeIdentifierPage, customsOffice)

          val expectedResult = LocationOfGoodsV(
            typeOfLocation = typeOfLocation,
            customsOffice = customsOffice
          )

          val result: EitherType[LocationOfGoodsDomain] = UserAnswersReader[LocationOfGoodsDomain].run(userAnswers)

          result.value mustBe expectedResult
        }

        "is X (EORI number)" in {
          val eoriNumber = Gen.alphaNumStr.sample.value

          val userAnswers = emptyUserAnswers
            .setValue(LocationOfGoodsTypePage, typeOfLocation)
            .setValue(LocationOfGoodsIdentificationPage, LocationOfGoodsIdentification.EoriNumber)
            .setValue(LocationOfGoodsEoriPage, eoriNumber)

          val expectedResult = LocationOfGoodsX(
            typeOfLocation = typeOfLocation,
            identificationNumber = eoriNumber
          )

          val result: EitherType[LocationOfGoodsDomain] = UserAnswersReader[LocationOfGoodsDomain].run(userAnswers)

          result.value mustBe expectedResult
        }

        "is Y (Authorisation number)" in {
          val authorisationNumber = Gen.alphaNumStr.sample.value

          val userAnswers = emptyUserAnswers
            .setValue(LocationOfGoodsTypePage, typeOfLocation)
            .setValue(LocationOfGoodsIdentificationPage, LocationOfGoodsIdentification.AuthorisationNumber)
            .setValue(LocationOfGoodsAuthorisationNumberPage, authorisationNumber)

          val expectedResult = LocationOfGoodsY(
            typeOfLocation = typeOfLocation,
            authorisationNumber = authorisationNumber
          )

          val result: EitherType[LocationOfGoodsDomain] = UserAnswersReader[LocationOfGoodsDomain].run(userAnswers)

          result.value mustBe expectedResult
        }
      }
    }

    "cannot be parsed from UserAnswers" - {}
  }
}
