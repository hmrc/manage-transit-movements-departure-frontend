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
import models.{Address, Coordinates, LocationOfGoodsIdentification, LocationType, PostalCodeAddress}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.forAll
import pages.QuestionPage
import pages.routeDetails.locationOfGoods._

class LocationOfGoodsDomainSpec extends SpecBase with UserAnswersSpecHelper with Generators {

  private val typeOfLocation = arbitrary[LocationType].sample.value
  private val contactName    = Gen.alphaNumStr.sample.value
  private val contactPhone   = Gen.alphaNumStr.sample.value

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

        "is W (Coordinate identifier)" in {
          val qualifierOfIdentification = LocationOfGoodsIdentification.CoordinatesIdentifier
          val coordinate                = arbitrary[Coordinates].sample.value

          val userAnswers = emptyUserAnswers
            .setValue(LocationOfGoodsTypePage, typeOfLocation)
            .setValue(LocationOfGoodsIdentificationPage, qualifierOfIdentification)
            .setValue(LocationOfGoodsCoordinatesPage, coordinate)
            .setValue(AddContactYesNoPage, false)

          val expectedResult = LocationOfGoodsW(
            typeOfLocation = typeOfLocation,
            coordinates = coordinate,
            additionalContact = None
          )

          val result: EitherType[LocationOfGoodsDomain] = UserAnswersReader[LocationOfGoodsDomain].run(userAnswers)

          result.value mustBe expectedResult
          result.value.qualifierOfIdentification mustBe qualifierOfIdentification
        }

        "is X (EORI number) and AddIdentifierYesNoPage is answered No" in {
          val qualifierOfIdentification = LocationOfGoodsIdentification.EoriNumber
          val eoriNumber                = Gen.alphaNumStr.sample.value

          val userAnswers = emptyUserAnswers
            .setValue(LocationOfGoodsTypePage, typeOfLocation)
            .setValue(LocationOfGoodsIdentificationPage, qualifierOfIdentification)
            .setValue(LocationOfGoodsEoriPage, eoriNumber)
            .setValue(AddIdentifierYesNoPage, false)
            .setValue(AddContactYesNoPage, false)

          val expectedResult = LocationOfGoodsX(
            typeOfLocation = typeOfLocation,
            identificationNumber = eoriNumber,
            additionalIdentifier = None,
            additionalContact = None
          )

          val result: EitherType[LocationOfGoodsDomain] = UserAnswersReader[LocationOfGoodsDomain].run(userAnswers)

          result.value mustBe expectedResult
          result.value.qualifierOfIdentification mustBe qualifierOfIdentification
        }

        "is X (EORI number) and AddIdentifierYesNoPage is answered Yes" in {
          val qualifierOfIdentification = LocationOfGoodsIdentification.EoriNumber
          val eoriNumber                = Gen.alphaNumStr.sample.value

          val userAnswers = emptyUserAnswers
            .setValue(LocationOfGoodsTypePage, typeOfLocation)
            .setValue(LocationOfGoodsIdentificationPage, qualifierOfIdentification)
            .setValue(LocationOfGoodsEoriPage, eoriNumber)
            .setValue(AddIdentifierYesNoPage, true)
            .setValue(AdditionalIdentifierPage, "1234")
            .setValue(AddContactYesNoPage, false)

          val expectedResult = LocationOfGoodsX(
            typeOfLocation = typeOfLocation,
            identificationNumber = eoriNumber,
            additionalIdentifier = Some("1234"),
            additionalContact = None
          )

          val result: EitherType[LocationOfGoodsDomain] = UserAnswersReader[LocationOfGoodsDomain].run(userAnswers)

          result.value mustBe expectedResult
          result.value.qualifierOfIdentification mustBe qualifierOfIdentification
        }

        "is Y (Authorisation number) and AddIdentifierYesNoPage is answered No" in {
          val qualifierOfIdentification = LocationOfGoodsIdentification.AuthorisationNumber
          val authorisationNumber       = Gen.alphaNumStr.sample.value

          val userAnswers = emptyUserAnswers
            .setValue(LocationOfGoodsTypePage, typeOfLocation)
            .setValue(LocationOfGoodsIdentificationPage, qualifierOfIdentification)
            .setValue(LocationOfGoodsAuthorisationNumberPage, authorisationNumber)
            .setValue(AddIdentifierYesNoPage, false)
            .setValue(AddContactYesNoPage, false)

          val expectedResult = LocationOfGoodsY(
            typeOfLocation = typeOfLocation,
            authorisationNumber = authorisationNumber,
            additionalIdentifier = None,
            additionalContact = None
          )

          val result: EitherType[LocationOfGoodsDomain] = UserAnswersReader[LocationOfGoodsDomain].run(userAnswers)

          result.value mustBe expectedResult
          result.value.qualifierOfIdentification mustBe qualifierOfIdentification
        }

        "is Y (Authorisation number) and AddIdentifierYesNoPage is answered Yes" in {
          val qualifierOfIdentification = LocationOfGoodsIdentification.AuthorisationNumber
          val authorisationNumber       = Gen.alphaNumStr.sample.value

          val userAnswers = emptyUserAnswers
            .setValue(LocationOfGoodsTypePage, typeOfLocation)
            .setValue(LocationOfGoodsIdentificationPage, qualifierOfIdentification)
            .setValue(LocationOfGoodsAuthorisationNumberPage, authorisationNumber)
            .setValue(AddIdentifierYesNoPage, true)
            .setValue(AdditionalIdentifierPage, "1234")
            .setValue(AddContactYesNoPage, false)

          val expectedResult = LocationOfGoodsY(
            typeOfLocation = typeOfLocation,
            authorisationNumber = authorisationNumber,
            additionalIdentifier = Some("1234"),
            additionalContact = None
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
            .setValue(AddressPage, address)
            .setValue(AddContactYesNoPage, false)

          val expectedResult = LocationOfGoodsZ(
            typeOfLocation = typeOfLocation,
            address = address,
            additionalContact = None
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
            .setValue(AddContactYesNoPage, false)

          val expectedResult = LocationOfGoodsU(
            typeOfLocation = typeOfLocation,
            unLocode = unLocode,
            additionalContact = None
          )

          val result: EitherType[LocationOfGoodsDomain] = UserAnswersReader[LocationOfGoodsDomain].run(userAnswers)

          result.value mustBe expectedResult
          result.value.qualifierOfIdentification mustBe qualifierOfIdentification
        }

        "is T (PostalCode)" in {
          val qualifierOfIdentification = LocationOfGoodsIdentification.PostalCode
          val postalCodeAddress         = arbitrary[PostalCodeAddress].sample.value

          val userAnswers = emptyUserAnswers
            .setValue(LocationOfGoodsTypePage, typeOfLocation)
            .setValue(LocationOfGoodsIdentificationPage, qualifierOfIdentification)
            .setValue(LocationOfGoodsPostalCodePage, postalCodeAddress)
            .setValue(AddContactYesNoPage, false)

          val expectedResult = LocationOfGoodsT(
            typeOfLocation = typeOfLocation,
            postalCodeAddress = postalCodeAddress,
            additionalContact = None
          )

          val result: EitherType[LocationOfGoodsDomain] = UserAnswersReader[LocationOfGoodsDomain].run(userAnswers)

          result.value mustBe expectedResult
          result.value.qualifierOfIdentification mustBe qualifierOfIdentification
        }

      }

      "when is Y(Authorisation Number) and all optional pages are answered" in {
        val qualifierOfIdentification = LocationOfGoodsIdentification.AuthorisationNumber
        val authorisationNumber       = Gen.alphaNumStr.sample.value

        val userAnswers = emptyUserAnswers
          .setValue(LocationOfGoodsTypePage, typeOfLocation)
          .setValue(LocationOfGoodsIdentificationPage, qualifierOfIdentification)
          .setValue(LocationOfGoodsAuthorisationNumberPage, authorisationNumber)
          .setValue(AddIdentifierYesNoPage, true)
          .setValue(AdditionalIdentifierPage, "1234")
          .setValue(AddContactYesNoPage, true)
          .setValue(contact.LocationOfGoodsContactNamePage, contactName)
          .setValue(contact.TelephoneNumberPage, contactPhone)

        val result: EitherType[LocationOfGoodsDomain] = UserAnswersReader[LocationOfGoodsDomain].run(userAnswers)

        val expectedResult = LocationOfGoodsY(
          typeOfLocation = typeOfLocation,
          authorisationNumber = authorisationNumber,
          additionalIdentifier = Some("1234"),
          additionalContact = Some(AdditionalContactDomain(contactName, contactPhone))
        )

        result.value mustBe expectedResult
      }

    }

    "cannot be parsed from UserAnswers" - {

      "when is X(Eori Number) and a mandatory page is missing" in {
        val mandatoryPages: Gen[QuestionPage[_]] = Gen.oneOf(
          LocationOfGoodsIdentificationPage,
          LocationOfGoodsEoriPage,
          AddIdentifierYesNoPage,
          AddContactYesNoPage
        )

        val qualifierOfIdentification = LocationOfGoodsIdentification.EoriNumber
        val eoriNumber                = Gen.alphaNumStr.sample.value

        val userAnswers = emptyUserAnswers
          .setValue(LocationOfGoodsTypePage, typeOfLocation)
          .setValue(LocationOfGoodsIdentificationPage, qualifierOfIdentification)
          .setValue(LocationOfGoodsEoriPage, eoriNumber)
          .setValue(AddIdentifierYesNoPage, false)
          .setValue(AddContactYesNoPage, false)

        forAll(mandatoryPages) {
          mandatoryPage =>
            val invalidUserAnswers = userAnswers.unsafeRemove(mandatoryPage)

            val result: EitherType[LocationOfGoodsDomain] = UserAnswersReader[LocationOfGoodsDomain].run(invalidUserAnswers)

            result.left.value.page mustBe mandatoryPage
        }
      }

      "when is Y(Authorisation Number) and AdditionalIdentifier page is missing when required" in {
        val qualifierOfIdentification = LocationOfGoodsIdentification.AuthorisationNumber
        val authorisationNumber       = Gen.alphaNumStr.sample.value

        val userAnswers = emptyUserAnswers
          .setValue(LocationOfGoodsTypePage, typeOfLocation)
          .setValue(LocationOfGoodsIdentificationPage, qualifierOfIdentification)
          .setValue(LocationOfGoodsAuthorisationNumberPage, authorisationNumber)
          .setValue(AddIdentifierYesNoPage, true)
          .setValue(AddContactYesNoPage, false)

        val result: EitherType[LocationOfGoodsDomain] = UserAnswersReader[LocationOfGoodsDomain].run(userAnswers)

        result.left.value.page mustBe AdditionalIdentifierPage

      }

      "when is Y(Authorisation Number) and contact name details is missing when required" in {
        val qualifierOfIdentification = LocationOfGoodsIdentification.AuthorisationNumber
        val authorisationNumber       = Gen.alphaNumStr.sample.value

        val userAnswers = emptyUserAnswers
          .setValue(LocationOfGoodsTypePage, typeOfLocation)
          .setValue(LocationOfGoodsIdentificationPage, qualifierOfIdentification)
          .setValue(LocationOfGoodsAuthorisationNumberPage, authorisationNumber)
          .setValue(AddIdentifierYesNoPage, true)
          .setValue(AdditionalIdentifierPage, "1234")
          .setValue(AddContactYesNoPage, true)

        val result: EitherType[LocationOfGoodsDomain] = UserAnswersReader[LocationOfGoodsDomain].run(userAnswers)

        result.left.value.page mustBe contact.LocationOfGoodsContactNamePage
      }

      "when is Y(Authorisation Number) and contact phone number details is missing when required" in {
        val qualifierOfIdentification = LocationOfGoodsIdentification.AuthorisationNumber
        val authorisationNumber       = Gen.alphaNumStr.sample.value

        val userAnswers = emptyUserAnswers
          .setValue(LocationOfGoodsTypePage, typeOfLocation)
          .setValue(LocationOfGoodsIdentificationPage, qualifierOfIdentification)
          .setValue(LocationOfGoodsAuthorisationNumberPage, authorisationNumber)
          .setValue(AddIdentifierYesNoPage, true)
          .setValue(AdditionalIdentifierPage, "1234")
          .setValue(AddContactYesNoPage, true)
          .setValue(contact.LocationOfGoodsContactNamePage, contactName)

        val result: EitherType[LocationOfGoodsDomain] = UserAnswersReader[LocationOfGoodsDomain].run(userAnswers)

        result.left.value.page mustBe contact.TelephoneNumberPage
      }
    }
  }
}
