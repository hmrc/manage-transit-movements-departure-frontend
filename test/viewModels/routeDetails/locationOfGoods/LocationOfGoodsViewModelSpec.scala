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

package viewModels.routeDetails.locationOfGoods

import base.SpecBase
import generators.{Generators, RouteDetailsUserAnswersGenerator}
import models.reference.{CustomsOffice, UnLocode}
import models.{Address, Coordinates, LocationOfGoodsIdentification, LocationType, PostalCodeAddress}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.routeDetails.locationOfGoods._

class LocationOfGoodsViewModelSpec extends SpecBase with ScalaCheckPropertyChecks with Generators with RouteDetailsUserAnswersGenerator {
  "apply" - {
    "when 'v' customs office" - {
      "must return 3 rows" in {
        val userAnswers = emptyUserAnswers
          .setValue(LocationOfGoodsTypePage, arbitrary[LocationType].sample.value)
          .setValue(LocationOfGoodsIdentificationPage, arbitrary[LocationOfGoodsIdentification].sample.value)
          .setValue(LocationOfGoodsCustomsOfficeIdentifierPage, arbitrary[CustomsOffice].sample.value)

        val section = LocationOfGoodsViewModel.apply(userAnswers).section

        section.rows.size mustBe 3
        section.sectionTitle mustNot be(defined)

      }
    }

    "when 'x' EORI" - {
      "must return 8 rows" in {
        val userAnswers = emptyUserAnswers
          .setValue(LocationOfGoodsTypePage, arbitrary[LocationType].sample.value)
          .setValue(LocationOfGoodsIdentificationPage, arbitrary[LocationOfGoodsIdentification].sample.value)
          .setValue(LocationOfGoodsEoriPage, arbitrary[String].sample.value)
          .setValue(LocationOfGoodsAddIdentifierYesNoPage, true)
          .setValue(AdditionalIdentifierPage, arbitrary[String].sample.value)
          .setValue(AddContactYesNoPage, true)
          .setValue(contact.LocationOfGoodsContactNamePage, arbitrary[String].sample.value)
          .setValue(contact.TelephoneNumberPage, arbitrary[String].sample.value)

        val section = LocationOfGoodsViewModel.apply(userAnswers).section

        section.rows.size mustBe 8
        section.sectionTitle mustNot be(defined)

      }
    }

    "when 'Y' Authorisation  number page" - {
      "must return 8 rows" in {
        val userAnswers = emptyUserAnswers
          .setValue(LocationOfGoodsTypePage, arbitrary[LocationType].sample.value)
          .setValue(LocationOfGoodsIdentificationPage, arbitrary[LocationOfGoodsIdentification].sample.value)
          .setValue(LocationOfGoodsAuthorisationNumberPage, arbitrary[String].sample.value)
          .setValue(LocationOfGoodsAddIdentifierYesNoPage, true)
          .setValue(AdditionalIdentifierPage, arbitrary[String].sample.value)
          .setValue(AddContactYesNoPage, true)
          .setValue(contact.LocationOfGoodsContactNamePage, arbitrary[String].sample.value)
          .setValue(contact.TelephoneNumberPage, arbitrary[String].sample.value)

        val section = LocationOfGoodsViewModel.apply(userAnswers).section

        section.rows.size mustBe 8
        section.sectionTitle mustNot be(defined)

      }
    }

    "when 'W' coordinates page" - {
      "must return 6 rows" in {
        val userAnswers = emptyUserAnswers
          .setValue(LocationOfGoodsTypePage, arbitrary[LocationType].sample.value)
          .setValue(LocationOfGoodsIdentificationPage, arbitrary[LocationOfGoodsIdentification].sample.value)
          .setValue(LocationOfGoodsCoordinatesPage, arbitrary[Coordinates].sample.value)
          .setValue(AddContactYesNoPage, true)
          .setValue(contact.LocationOfGoodsContactNamePage, arbitrary[String].sample.value)
          .setValue(contact.TelephoneNumberPage, arbitrary[String].sample.value)

        val section = LocationOfGoodsViewModel.apply(userAnswers).section

        section.rows.size mustBe 6
        section.sectionTitle mustNot be(defined)

      }
    }

    "when 'U' Un-locode page" - {
      "must return 6 rows" in {
        val userAnswers = emptyUserAnswers
          .setValue(LocationOfGoodsTypePage, arbitrary[LocationType].sample.value)
          .setValue(LocationOfGoodsIdentificationPage, arbitrary[LocationOfGoodsIdentification].sample.value)
          .setValue(LocationOfGoodsUnLocodePage, arbitrary[UnLocode].sample.value)
          .setValue(AddContactYesNoPage, true)
          .setValue(contact.LocationOfGoodsContactNamePage, arbitrary[String].sample.value)
          .setValue(contact.TelephoneNumberPage, arbitrary[String].sample.value)

        val section = LocationOfGoodsViewModel.apply(userAnswers).section

        section.rows.size mustBe 6
        section.sectionTitle mustNot be(defined)

      }
    }

    "when 'Z' Location of goods address page" - {
      "must return 6 rows" in {
        val userAnswers = emptyUserAnswers
          .setValue(LocationOfGoodsTypePage, arbitrary[LocationType].sample.value)
          .setValue(LocationOfGoodsIdentificationPage, arbitrary[LocationOfGoodsIdentification].sample.value)
          .setValue(LocationOfGoodsAddressPage, arbitrary[Address].sample.value)
          .setValue(AddContactYesNoPage, true)
          .setValue(contact.LocationOfGoodsContactNamePage, arbitrary[String].sample.value)
          .setValue(contact.TelephoneNumberPage, arbitrary[String].sample.value)

        val section = LocationOfGoodsViewModel.apply(userAnswers).section

        section.rows.size mustBe 6
        section.sectionTitle mustNot be(defined)

      }
    }

    "when 'T' Location of goods postcode page" - {
      "must return 6 rows" in {
        val userAnswers = emptyUserAnswers
          .setValue(LocationOfGoodsTypePage, arbitrary[LocationType].sample.value)
          .setValue(LocationOfGoodsIdentificationPage, arbitrary[LocationOfGoodsIdentification].sample.value)
          .setValue(LocationOfGoodsPostalCodePage, arbitrary[PostalCodeAddress].sample.value)
          .setValue(AddContactYesNoPage, true)
          .setValue(contact.LocationOfGoodsContactNamePage, arbitrary[String].sample.value)
          .setValue(contact.TelephoneNumberPage, arbitrary[String].sample.value)

        val section = LocationOfGoodsViewModel.apply(userAnswers).section

        section.rows.size mustBe 6
        section.sectionTitle mustNot be(defined)

      }
    }
  }
}
