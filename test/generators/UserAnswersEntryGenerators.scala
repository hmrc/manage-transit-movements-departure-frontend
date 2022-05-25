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

import models._
import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary.arbitrary
import pages.preTaskList._
import play.api.libs.json.{JsValue, Json}

trait UserAnswersEntryGenerators {
  self: Generators =>

  implicit lazy val arbitraryTraderdetailsHolderoftransitTirIdentificationNoControllerUserAnswersEntry
    : Arbitrary[(pages.traderDetails.holderOfTransit.TirIdentificationPage.type, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[pages.traderDetails.holderOfTransit.TirIdentificationPage.type#Data].map(Json.toJson(_))
      } yield (pages.traderDetails.holderOfTransit.TirIdentificationPage, value)
    }

  implicit lazy val arbitraryTraderdetailsHolderoftransitTirIdentificationYesNoUserAnswersEntry
    : Arbitrary[(pages.traderDetails.holderOfTransit.TirIdentificationYesNoPage.type, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[pages.traderDetails.holderOfTransit.TirIdentificationYesNoPage.type#Data].map(Json.toJson(_))
      } yield (pages.traderDetails.holderOfTransit.TirIdentificationYesNoPage, value)
    }

  implicit lazy val arbitraryAddressUserAnswersEntry: Arbitrary[(pages.traderDetails.holderOfTransit.AddressPage.type, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[pages.traderDetails.holderOfTransit.AddressPage.type#Data].map(Json.toJson(_))
      } yield (pages.traderDetails.holderOfTransit.AddressPage, value)
    }

  implicit lazy val arbitraryContactNameUserAnswersEntry: Arbitrary[(pages.traderDetails.holderOfTransit.ContactNamePage.type, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[pages.traderDetails.holderOfTransit.ContactNamePage.type#Data].map(Json.toJson(_))
      } yield (pages.traderDetails.holderOfTransit.ContactNamePage, value)
    }

  implicit lazy val arbitraryNameUserAnswersEntry: Arbitrary[(pages.traderDetails.holderOfTransit.NamePage.type, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[pages.traderDetails.holderOfTransit.NamePage.type#Data].map(Json.toJson(_))
      } yield (pages.traderDetails.holderOfTransit.NamePage, value)
    }

  implicit lazy val arbitraryAddContactUserAnswersEntry: Arbitrary[(pages.traderDetails.holderOfTransit.AddContactPage.type, JsValue)] =
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

  implicit lazy val arbitraryAddSecurityDetailsUserAnswersEntry: Arbitrary[(SecurityDetailsTypePage.type, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (SecurityDetailsTypePage, value)
    }

  implicit lazy val arbitraryLocalReferenceNumberUserAnswersEntry: Arbitrary[(LocalReferenceNumberPage.type, JsValue)] =
    Arbitrary {
      for {
        value <- nonEmptyString.map(Json.toJson(_))
      } yield (LocalReferenceNumberPage, value)
    }

  implicit lazy val arbitraryTIRCarnetReferenceUserAnswersEntry: Arbitrary[(TIRCarnetReferencePage.type, JsValue)] =
    Arbitrary {
      for {
        value <- nonEmptyString.map(Json.toJson(_))
      } yield (TIRCarnetReferencePage, value)
    }
}
