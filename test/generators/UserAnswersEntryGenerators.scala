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
import pages.traderDetails.holderOfTransit.{contact => holderOfTransitContact}
import pages.traderDetails.consignment.{consignor => traderDetailsConsignor}
import play.api.libs.json.{JsValue, Json}

trait UserAnswersEntryGenerators {
  self: Generators =>

  implicit lazy val arbitraryTraderDetailsConsignmentConsigneeEoriNumberUserAnswersEntry
    : Arbitrary[(pages.traderDetails.consignment.consignee.EoriNumberPage.type, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[pages.traderDetails.consignment.consignee.EoriNumberPage.type#Data].map(Json.toJson(_))
      } yield (pages.traderDetails.consignment.consignee.EoriNumberPage, value)
    }

  implicit lazy val arbitraryTraderdetailsConsignmentConsigneeEoriYesNoUserAnswersEntry
    : Arbitrary[(pages.traderDetails.consignment.consignee.EoriYesNoPage.type, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[pages.traderDetails.consignment.consignee.EoriYesNoPage.type#Data].map(Json.toJson(_))
      } yield (pages.traderDetails.consignment.consignee.EoriYesNoPage, value)
    }

  implicit lazy val arbitraryTraderDetailsConsignmentConsignorContactTelephoneNumberUserAnswersEntry
    : Arbitrary[(traderDetailsConsignor.contact.TelephoneNumberPage.type, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[traderDetailsConsignor.contact.TelephoneNumberPage.type#Data].map(Json.toJson(_))
      } yield (traderDetailsConsignor.contact.TelephoneNumberPage, value)
    }

  implicit lazy val arbitraryTraderDetailsConsignmentConsignorContactNameUserAnswersEntry: Arbitrary[(traderDetailsConsignor.contact.NamePage.type, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[traderDetailsConsignor.contact.NamePage.type#Data].map(Json.toJson(_))
      } yield (traderDetailsConsignor.contact.NamePage, value)
    }

  implicit lazy val arbitraryTraderDetailsConsignmentConsignorAddressUserAnswersEntry: Arbitrary[(traderDetailsConsignor.AddressPage.type, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[traderDetailsConsignor.AddressPage.type#Data].map(Json.toJson(_))
      } yield (traderDetailsConsignor.AddressPage, value)
    }

  implicit lazy val arbitraryTraderDetailsConsignmentConsignorNameUserAnswersEntry: Arbitrary[(traderDetailsConsignor.NamePage.type, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[traderDetailsConsignor.NamePage.type#Data].map(Json.toJson(_))
      } yield (traderDetailsConsignor.NamePage, value)
    }

  implicit lazy val arbitraryTraderDetailsConsignmentConsignorEoriUserAnswersEntry: Arbitrary[(traderDetailsConsignor.EoriPage.type, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[traderDetailsConsignor.EoriPage.type#Data].map(Json.toJson(_))
      } yield (traderDetailsConsignor.EoriPage, value)
    }

  implicit lazy val arbitraryTraderdetailsConsignmentConsigneeMoreThanOneConsigneeUserAnswersEntry
    : Arbitrary[(pages.traderDetails.consignment.consignee.MoreThanOneConsigneePage.type, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[pages.traderDetails.consignment.consignee.MoreThanOneConsigneePage.type#Data].map(Json.toJson(_))
      } yield (pages.traderDetails.consignment.consignee.MoreThanOneConsigneePage, value)
    }

  implicit lazy val arbitraryTraderdetailsConsignmentApprovedOperatorUserAnswersEntry
    : Arbitrary[(pages.traderDetails.consignment.ApprovedOperatorPage.type, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[pages.traderDetails.consignment.ApprovedOperatorPage.type#Data].map(Json.toJson(_))
      } yield (pages.traderDetails.consignment.ApprovedOperatorPage, value)
    }

  implicit lazy val arbitraryRepresentativePhoneUserAnswersEntry: Arbitrary[(pages.traderDetails.representative.TelephoneNumberPage.type, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[pages.traderDetails.representative.TelephoneNumberPage.type#Data].map(Json.toJson(_))
      } yield (pages.traderDetails.representative.TelephoneNumberPage, value)
    }

  implicit lazy val arbitraryRepresentativeCapacityUserAnswersEntry: Arbitrary[(pages.traderDetails.representative.CapacityPage.type, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[pages.traderDetails.representative.CapacityPage.type#Data].map(Json.toJson(_))
      } yield (pages.traderDetails.representative.CapacityPage, value)
    }

  implicit lazy val arbitraryRepresentativeNameUserAnswersEntry: Arbitrary[(pages.traderDetails.representative.NamePage.type, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[pages.traderDetails.representative.NamePage.type#Data].map(Json.toJson(_))
      } yield (pages.traderDetails.representative.NamePage, value)
    }

  implicit lazy val arbitraryRepresentativeEoriUserAnswersEntry: Arbitrary[(pages.traderDetails.representative.EoriPage.type, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[pages.traderDetails.representative.EoriPage.type#Data].map(Json.toJson(_))
      } yield (pages.traderDetails.representative.EoriPage, value)
    }

  implicit lazy val arbitraryRepresentativeActingRepresentativeUserAnswersEntry
    : Arbitrary[(pages.traderDetails.representative.ActingAsRepresentativePage.type, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[pages.traderDetails.representative.ActingAsRepresentativePage.type#Data].map(Json.toJson(_))
      } yield (pages.traderDetails.representative.ActingAsRepresentativePage, value)
    }

  implicit lazy val arbitraryTransitHolderAdditionalContactTelephoneNumberUserAnswersEntry
    : Arbitrary[(holderOfTransitContact.TelephoneNumberPage.type, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[holderOfTransitContact.TelephoneNumberPage.type#Data].map(Json.toJson(_))
      } yield (holderOfTransitContact.TelephoneNumberPage, value)
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

  implicit lazy val arbitraryTransitHolderAdditionalContactNameUserAnswersEntry: Arbitrary[(holderOfTransitContact.NamePage.type, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[holderOfTransitContact.NamePage.type#Data].map(Json.toJson(_))
      } yield (holderOfTransitContact.NamePage, value)
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
