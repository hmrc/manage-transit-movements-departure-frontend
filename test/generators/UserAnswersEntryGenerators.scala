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

package generators

import config.Constants._
import models.DeclarationType.Option4
import models._
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Arbitrary, Gen}
import pages.preTaskList._
import play.api.libs.json.{JsValue, Json}

trait UserAnswersEntryGenerators {
  self: Generators =>

  implicit lazy val arbitraryRepresentativePhoneUserAnswersEntry: Arbitrary[(pages.traderDetails.representative.RepresentativePhonePage.type, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[pages.traderDetails.representative.RepresentativePhonePage.type#Data].map(Json.toJson(_))
      } yield (pages.traderDetails.representative.RepresentativePhonePage, value)
    }

  implicit lazy val arbitraryRepresentativeCapacityUserAnswersEntry: Arbitrary[(pages.traderDetails.representative.RepresentativeCapacityPage.type, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[pages.traderDetails.representative.RepresentativeCapacityPage.type#Data].map(Json.toJson(_))
      } yield (pages.traderDetails.representative.RepresentativeCapacityPage, value)
    }

  implicit lazy val arbitraryRepresentativeNameUserAnswersEntry: Arbitrary[(pages.traderDetails.representative.RepresentativeNamePage.type, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[pages.traderDetails.representative.RepresentativeNamePage.type#Data].map(Json.toJson(_))
      } yield (pages.traderDetails.representative.RepresentativeNamePage, value)
    }

  implicit lazy val arbitraryRepresentativeEoriUserAnswersEntry: Arbitrary[(pages.traderDetails.representative.RepresentativeEoriPage.type, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[pages.traderDetails.representative.RepresentativeEoriPage.type#Data].map(Json.toJson(_))
      } yield (pages.traderDetails.representative.RepresentativeEoriPage, value)
    }

  implicit lazy val arbitraryRepresentativeActingRepresentativeUserAnswersEntry
    : Arbitrary[(pages.traderDetails.representative.ActingRepresentativePage.type, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[pages.traderDetails.representative.ActingRepresentativePage.type#Data].map(Json.toJson(_))
      } yield (pages.traderDetails.representative.ActingRepresentativePage, value)
    }

  implicit lazy val arbitraryTransitHolderAdditionalContactTelephoneNumberUserAnswersEntry
    : Arbitrary[(pages.traderDetails.holderOfTransit.ContactTelephoneNumberPage.type, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[pages.traderDetails.holderOfTransit.ContactTelephoneNumberPage.type#Data].map(Json.toJson(_))
      } yield (pages.traderDetails.holderOfTransit.ContactTelephoneNumberPage, value)
    }

  implicit lazy val arbitraryTransitHolderTirIdentificationUserAnswersEntry
    : Arbitrary[(pages.traderDetails.holderOfTransit.TirIdentificationPage.type, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[pages.traderDetails.holderOfTransit.TirIdentificationPage.type#Data].map(Json.toJson(_))
      } yield (pages.traderDetails.holderOfTransit.TirIdentificationPage, value)
    }

  implicit lazy val arbitraryTransitHolderTirIdentificationYesNoUserAnswersEntry
    : Arbitrary[(pages.traderDetails.holderOfTransit.TirIdentificationYesNoPage.type, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[pages.traderDetails.holderOfTransit.TirIdentificationYesNoPage.type#Data].map(Json.toJson(_))
      } yield (pages.traderDetails.holderOfTransit.TirIdentificationYesNoPage, value)
    }

  implicit lazy val arbitraryTransitHolderAddressUserAnswersEntry: Arbitrary[(pages.traderDetails.holderOfTransit.AddressPage.type, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[pages.traderDetails.holderOfTransit.AddressPage.type#Data].map(Json.toJson(_))
      } yield (pages.traderDetails.holderOfTransit.AddressPage, value)
    }

  implicit lazy val arbitraryTransitHolderAdditionalContactNameUserAnswersEntry
    : Arbitrary[(pages.traderDetails.holderOfTransit.ContactNamePage.type, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[pages.traderDetails.holderOfTransit.ContactNamePage.type#Data].map(Json.toJson(_))
      } yield (pages.traderDetails.holderOfTransit.ContactNamePage, value)
    }

  implicit lazy val arbitraryTransitHolderNameUserAnswersEntry: Arbitrary[(pages.traderDetails.holderOfTransit.NamePage.type, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[pages.traderDetails.holderOfTransit.NamePage.type#Data].map(Json.toJson(_))
      } yield (pages.traderDetails.holderOfTransit.NamePage, value)
    }

  implicit lazy val arbitraryTransitHolderAddContactUserAnswersEntry: Arbitrary[(pages.traderDetails.holderOfTransit.AddContactPage.type, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[pages.traderDetails.holderOfTransit.AddContactPage.type#Data].map(Json.toJson(_))
      } yield (pages.traderDetails.holderOfTransit.AddContactPage, value)
    }

  implicit lazy val arbitraryTransitHolderEoriUserAnswersEntry: Arbitrary[(pages.traderDetails.holderOfTransit.EoriPage.type, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[pages.traderDetails.holderOfTransit.EoriPage.type#Data].map(Json.toJson(_))
      } yield (pages.traderDetails.holderOfTransit.EoriPage, value)
    }

  implicit lazy val arbitraryTransitHolderEoriYesNoUserAnswersEntry: Arbitrary[(pages.traderDetails.holderOfTransit.EoriYesNoPage.type, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[pages.traderDetails.holderOfTransit.EoriYesNoPage.type#Data].map(Json.toJson(_))
      } yield (pages.traderDetails.holderOfTransit.EoriYesNoPage, value)
    }

  implicit lazy val arbitraryProcedureTypeUserAnswersEntry: Arbitrary[(ProcedureTypePage.type, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[ProcedureType].map(Json.toJson(_))
      } yield (ProcedureTypePage, value)
    }

  implicit lazy val arbitraryDeclarationTypeUserAnswersEntry: Arbitrary[(DeclarationTypePage.type, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[DeclarationType].map(Json.toJson(_))
      } yield (DeclarationTypePage, value)
    }

  implicit lazy val arbitraryNonOption4DeclarationTypeUserAnswersEntry: Arbitrary[(DeclarationTypePage.type, JsValue)] =
    Arbitrary {
      for {
        value <- Gen.oneOf[DeclarationType](DeclarationType.values.filterNot(_ == Option4)).map(Json.toJson(_))
      } yield (DeclarationTypePage, value)
    }

  implicit lazy val arbitraryAddSecurityDetailsUserAnswersEntry: Arbitrary[(SecurityDetailsTypePage.type, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[SecurityDetailsType].map(Json.toJson(_))
      } yield (SecurityDetailsTypePage, value)
    }

  implicit lazy val arbitraryTIRCarnetReferenceUserAnswersEntry: Arbitrary[(TIRCarnetReferencePage.type, JsValue)] =
    Arbitrary {
      for {
        value <- nonEmptyString.map(Json.toJson(_))
      } yield (TIRCarnetReferencePage, value)
    }

  implicit lazy val arbitraryOfficeOfDepartureUserAnswersEntry: Arbitrary[(OfficeOfDeparturePage.type, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[OfficeOfDeparturePage.type#Data].map(Json.toJson(_))
      } yield (OfficeOfDeparturePage, value)
    }

  implicit lazy val arbitraryGbOfficeOfDepartureUserAnswersEntry: Arbitrary[(OfficeOfDeparturePage.type, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[OfficeOfDeparturePage.type#Data].map(
          x => Json.toJson(x.copy(countryId = x.countryId.copy(code = GB)))
        )
      } yield (OfficeOfDeparturePage, value)
    }

  implicit lazy val arbitraryXiOfficeOfDepartureUserAnswersEntry: Arbitrary[(OfficeOfDeparturePage.type, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[OfficeOfDeparturePage.type#Data].map(
          x => Json.toJson(x.copy(countryId = x.countryId.copy(code = XI)))
        )
      } yield (OfficeOfDeparturePage, value)
    }
}
