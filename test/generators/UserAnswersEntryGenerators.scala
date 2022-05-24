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
import pages.traderDetails.holderOfTransit
import pages.traderDetails.holderOfTransit.EoriYesNoPage
import play.api.libs.json.{JsValue, Json}

trait UserAnswersEntryGenerators {
  self: Generators =>

  implicit lazy val arbitraryAddContactUserAnswersEntry: Arbitrary[(pages.traderDetails.holderOfTransit.AddContactPage.type, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[pages.traderDetails.holderOfTransit.AddContactPage.type#Data].map(Json.toJson(_))
      } yield (pages.traderDetails.holderOfTransit.AddContactPage, value)
    }

  implicit lazy val arbitraryTransitHolderEoriYesNoUserAnswersEntry: Arbitrary[(EoriYesNoPage.type, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[EoriYesNoPage.type#Data].map(Json.toJson(_))
      } yield (holderOfTransit.EoriYesNoPage, value)
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
