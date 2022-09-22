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
import models.LocationOfGoodsIdentification._
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.routeDetails.locationOfGoods._

class LocationOfGoodsViewModelSpec extends SpecBase with ScalaCheckPropertyChecks with Generators with RouteDetailsUserAnswersGenerator {

  "apply" - {
    "when 'V' customs office" - {
      val qualifier = CustomsOfficeIdentifier

      "must return 3 rows" in {
        val initialAnswers = emptyUserAnswers
          .setValue(IdentificationPage, qualifier)

        forAll(arbitraryLocationOfGoodsAnswers(initialAnswers)) {
          userAnswers =>
            val section = LocationOfGoodsViewModel.apply(userAnswers).section
            section.rows.size mustBe 3
            section.sectionTitle mustNot be(defined)
        }
      }
    }

    "when 'X' EORI number" - {
      val qualifier = EoriNumber

      "when an additional identifier and a contact have been provided" - {
        "must return 8 rows" in {
          val initialAnswers = emptyUserAnswers
            .setValue(IdentificationPage, qualifier)
            .setValue(AddIdentifierYesNoPage, true)
            .setValue(AddContactYesNoPage, true)

          forAll(arbitraryLocationOfGoodsAnswers(initialAnswers)) {
            userAnswers =>
              val section = LocationOfGoodsViewModel.apply(userAnswers).section
              section.rows.size mustBe 8
              section.sectionTitle mustNot be(defined)
          }
        }
      }

      "when neither an additional identifier nor a contact have been provided" - {
        "must return 5 rows" in {
          val initialAnswers = emptyUserAnswers
            .setValue(IdentificationPage, qualifier)
            .setValue(AddIdentifierYesNoPage, false)
            .setValue(AddContactYesNoPage, false)

          forAll(arbitraryLocationOfGoodsAnswers(initialAnswers)) {
            userAnswers =>
              val section = LocationOfGoodsViewModel.apply(userAnswers).section
              section.rows.size mustBe 5
              section.sectionTitle mustNot be(defined)
          }
        }
      }
    }

    "when 'Y' authorisation number" - {
      val qualifier = AuthorisationNumber

      "when an additional identifier and a contact have been provided" - {
        "must return 8 rows" in {
          val initialAnswers = emptyUserAnswers
            .setValue(IdentificationPage, qualifier)
            .setValue(AddIdentifierYesNoPage, true)
            .setValue(AddContactYesNoPage, true)

          forAll(arbitraryLocationOfGoodsAnswers(initialAnswers)) {
            userAnswers =>
              val section = LocationOfGoodsViewModel.apply(userAnswers).section
              section.rows.size mustBe 8
              section.sectionTitle mustNot be(defined)
          }
        }
      }

      "when neither an additional identifier nor a contact have been provided" - {
        "must return 5 rows" in {
          val initialAnswers = emptyUserAnswers
            .setValue(IdentificationPage, qualifier)
            .setValue(AddIdentifierYesNoPage, false)
            .setValue(AddContactYesNoPage, false)

          forAll(arbitraryLocationOfGoodsAnswers(initialAnswers)) {
            userAnswers =>
              val section = LocationOfGoodsViewModel.apply(userAnswers).section
              section.rows.size mustBe 5
              section.sectionTitle mustNot be(defined)
          }
        }
      }
    }

    "when 'W' coordinates" - {
      val qualifier = CoordinatesIdentifier

      "when a contact has been provided" - {
        "must return 6 rows" in {
          val initialAnswers = emptyUserAnswers
            .setValue(IdentificationPage, qualifier)
            .setValue(AddContactYesNoPage, true)

          forAll(arbitraryLocationOfGoodsAnswers(initialAnswers)) {
            userAnswers =>
              val section = LocationOfGoodsViewModel.apply(userAnswers).section
              section.rows.size mustBe 6
              section.sectionTitle mustNot be(defined)
          }
        }
      }

      "when a contact has not been provided" - {
        "must return 4 rows" in {
          val initialAnswers = emptyUserAnswers
            .setValue(IdentificationPage, qualifier)
            .setValue(AddContactYesNoPage, false)

          forAll(arbitraryLocationOfGoodsAnswers(initialAnswers)) {
            userAnswers =>
              val section = LocationOfGoodsViewModel.apply(userAnswers).section
              section.rows.size mustBe 4
              section.sectionTitle mustNot be(defined)
          }
        }
      }
    }

    "when 'U' UN-LOCODE" - {
      val qualifier = UnlocodeIdentifier

      "when a contact has been provided" - {
        "must return 6 rows" in {
          val initialAnswers = emptyUserAnswers
            .setValue(IdentificationPage, qualifier)
            .setValue(AddContactYesNoPage, true)

          forAll(arbitraryLocationOfGoodsAnswers(initialAnswers)) {
            userAnswers =>
              val section = LocationOfGoodsViewModel.apply(userAnswers).section
              section.rows.size mustBe 6
              section.sectionTitle mustNot be(defined)
          }
        }
      }

      "when a contact has not been provided" - {
        "must return 4 rows" in {
          val initialAnswers = emptyUserAnswers
            .setValue(IdentificationPage, qualifier)
            .setValue(AddContactYesNoPage, false)

          forAll(arbitraryLocationOfGoodsAnswers(initialAnswers)) {
            userAnswers =>
              val section = LocationOfGoodsViewModel.apply(userAnswers).section
              section.rows.size mustBe 4
              section.sectionTitle mustNot be(defined)
          }
        }
      }
    }

    "when 'Z' address" - {
      val qualifier = AddressIdentifier

      "when a contact has been provided" - {
        "must return 6 rows" in {
          val initialAnswers = emptyUserAnswers
            .setValue(IdentificationPage, qualifier)
            .setValue(AddContactYesNoPage, true)

          forAll(arbitraryLocationOfGoodsAnswers(initialAnswers)) {
            userAnswers =>
              val section = LocationOfGoodsViewModel.apply(userAnswers).section
              section.rows.size mustBe 6
              section.sectionTitle mustNot be(defined)
          }
        }
      }

      "when a contact has not been provided" - {
        "must return 4 rows" in {
          val initialAnswers = emptyUserAnswers
            .setValue(IdentificationPage, qualifier)
            .setValue(AddContactYesNoPage, false)

          forAll(arbitraryLocationOfGoodsAnswers(initialAnswers)) {
            userAnswers =>
              val section = LocationOfGoodsViewModel.apply(userAnswers).section
              section.rows.size mustBe 4
              section.sectionTitle mustNot be(defined)
          }
        }
      }
    }

    "when 'T' postal code" - {
      val qualifier = PostalCode

      "when a contact has been provided" - {
        "must return 6 rows" in {
          val initialAnswers = emptyUserAnswers
            .setValue(IdentificationPage, qualifier)
            .setValue(AddContactYesNoPage, true)

          forAll(arbitraryLocationOfGoodsAnswers(initialAnswers)) {
            userAnswers =>
              val section = LocationOfGoodsViewModel.apply(userAnswers).section
              section.rows.size mustBe 6
              section.sectionTitle mustNot be(defined)
          }
        }
      }

      "when a contact has not been provided" - {
        "must return 4 rows" in {
          val initialAnswers = emptyUserAnswers
            .setValue(IdentificationPage, qualifier)
            .setValue(AddContactYesNoPage, false)

          forAll(arbitraryLocationOfGoodsAnswers(initialAnswers)) {
            userAnswers =>
              val section = LocationOfGoodsViewModel.apply(userAnswers).section
              section.rows.size mustBe 4
              section.sectionTitle mustNot be(defined)
          }
        }
      }
    }
  }
}
