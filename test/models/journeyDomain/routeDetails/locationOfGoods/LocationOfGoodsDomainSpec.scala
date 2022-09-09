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
import models.reference.{CustomsOffice, UnLocode}
import models.{Address, LocationOfGoodsIdentification, LocationType}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import pages.routeDetails.locationOfGoods._

class LocationOfGoodsDomainSpec extends SpecBase with UserAnswersSpecHelper with Generators {

  private val typeOfLocation = arbitrary[LocationType].sample.value

  "LocationOfGoodsDomain" - {

    "can be parsed from UserAnswers" - {

      "when qualifier of identification" - {

        "is V (Customs office identifier)" in {
          val qualifierOfIdentification = LocationOfGoodsIdentification.CustomsOfficeIdentifier
          val customsOffice             = arbitrary[CustomsOffice].sample.value

          val userAnswers = emptyUserAnswers
            .setValue(LocationOfGoodsTypePage, typeOfLocation)
            .setValue(LocationOfGoodsIdentificationPage, qualifierOfIdentification)
            .setValue(LocationOfGoodsCustomsOfficeIdentifierPage, customsOffice)

          val expectedResult = LocationOfGoodsV(
            typeOfLocation = typeOfLocation,
            customsOffice = customsOffice
          )

          val result: EitherType[LocationOfGoodsDomain] = UserAnswersReader[LocationOfGoodsDomain].run(userAnswers)

          result.value mustBe expectedResult
          result.value.qualifierOfIdentification mustBe qualifierOfIdentification
        }

        "is X (EORI number)" in {
          val qualifierOfIdentification = LocationOfGoodsIdentification.EoriNumber
          val eoriNumber                = Gen.alphaNumStr.sample.value

          val userAnswers = emptyUserAnswers
            .setValue(LocationOfGoodsTypePage, typeOfLocation)
            .setValue(LocationOfGoodsIdentificationPage, qualifierOfIdentification)
            .setValue(LocationOfGoodsEoriPage, eoriNumber)

          val expectedResult = LocationOfGoodsX(
            typeOfLocation = typeOfLocation,
            identificationNumber = eoriNumber
          )

          val result: EitherType[LocationOfGoodsDomain] = UserAnswersReader[LocationOfGoodsDomain].run(userAnswers)

          result.value mustBe expectedResult
          result.value.qualifierOfIdentification mustBe qualifierOfIdentification
        }

        "is Y (Authorisation number)" in {
          val qualifierOfIdentification = LocationOfGoodsIdentification.AuthorisationNumber
          val authorisationNumber       = Gen.alphaNumStr.sample.value

          val userAnswers = emptyUserAnswers
            .setValue(LocationOfGoodsTypePage, typeOfLocation)
            .setValue(LocationOfGoodsIdentificationPage, qualifierOfIdentification)
            .setValue(LocationOfGoodsAuthorisationNumberPage, authorisationNumber)

          val expectedResult = LocationOfGoodsY(
            typeOfLocation = typeOfLocation,
            authorisationNumber = authorisationNumber
          )

          val result: EitherType[LocationOfGoodsDomain] = UserAnswersReader[LocationOfGoodsDomain].run(userAnswers)

          result.value mustBe expectedResult
          result.value.qualifierOfIdentification mustBe qualifierOfIdentification
        }

        "is Z (Address)" in {
          val qualifierOfIdentification = LocationOfGoodsIdentification.AddressIdentifier
          val address                   = arbitrary[Address].sample.value

          val userAnswers = emptyUserAnswers
            .setValue(LocationOfGoodsTypePage, typeOfLocation)
            .setValue(LocationOfGoodsIdentificationPage, qualifierOfIdentification)
            .setValue(LocationOfGoodsAddressPage, address)

          val expectedResult = LocationOfGoodsZ(
            typeOfLocation = typeOfLocation,
            address = address
          )

          val result: EitherType[LocationOfGoodsDomain] = UserAnswersReader[LocationOfGoodsDomain].run(userAnswers)

          result.value mustBe expectedResult
          result.value.qualifierOfIdentification mustBe qualifierOfIdentification
        }

        "is U (UnLocode)" in {
          val qualifierOfIdentification = LocationOfGoodsIdentification.UnlocodeIdentifier
          val unLocode                  = arbitrary[UnLocode].sample.value

          val userAnswers = emptyUserAnswers
            .setValue(LocationOfGoodsTypePage, typeOfLocation)
            .setValue(LocationOfGoodsIdentificationPage, qualifierOfIdentification)
            .setValue(LocationOfGoodsUnLocodePage, unLocode)

          val expectedResult = LocationOfGoodsU(
            typeOfLocation = typeOfLocation,
            unLocode = unLocode
          )

          val result: EitherType[LocationOfGoodsDomain] = UserAnswersReader[LocationOfGoodsDomain].run(userAnswers)

          result.value mustBe expectedResult
          result.value.qualifierOfIdentification mustBe qualifierOfIdentification
        }

      }
    }

    "cannot be parsed from UserAnswers" - {}
  }
}
