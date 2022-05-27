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

import models.DeclarationType.Option4
import models.{DeclarationType, UserAnswers}
import org.scalacheck.{Arbitrary, Gen}
import pages.preTaskList.DeclarationTypePage
import pages.traderDetails.holderOfTransit._
import play.api.libs.json.{JsBoolean, Json}

trait HolderOfTransitUserAnswersGenerator extends UserAnswersGenerator {
  self: Generators =>

  lazy val arbitraryCompletedAnswersWithEori: Gen[UserAnswers] = arbitraryUserAnswers(
    arbitraryNonOption4DeclarationTypeUserAnswersEntry.arbitrary ::
      Arbitrary((EoriYesNoPage, JsBoolean(true))).arbitrary ::
      arbitraryTransitHolderEoriUserAnswersEntry.arbitrary ::
      arbitraryTransitHolderNameUserAnswersEntry.arbitrary ::
      arbitraryTransitHolderAddressUserAnswersEntry.arbitrary ::
      Arbitrary((AddContactPage, JsBoolean(false))).arbitrary ::
      Nil
  )

  lazy val arbitraryCompletedAnswersWithoutEori: Gen[UserAnswers] = arbitraryUserAnswers(
    arbitraryNonOption4DeclarationTypeUserAnswersEntry.arbitrary ::
      Arbitrary((EoriYesNoPage, JsBoolean(false))).arbitrary ::
      arbitraryTransitHolderNameUserAnswersEntry.arbitrary ::
      arbitraryTransitHolderAddressUserAnswersEntry.arbitrary ::
      Arbitrary((AddContactPage, JsBoolean(false))).arbitrary ::
      Nil
  )

  lazy val arbitraryCompletedAnswersWithTirId: Gen[UserAnswers] = arbitraryUserAnswers(
    Arbitrary((DeclarationTypePage, Json.toJson[DeclarationType](Option4))).arbitrary ::
      Arbitrary((TirIdentificationYesNoPage, JsBoolean(true))).arbitrary ::
      arbitraryTransitHolderTirIdentificationUserAnswersEntry.arbitrary ::
      arbitraryTransitHolderNameUserAnswersEntry.arbitrary ::
      arbitraryTransitHolderAddressUserAnswersEntry.arbitrary ::
      Arbitrary((AddContactPage, JsBoolean(false))).arbitrary ::
      Nil
  )

  lazy val arbitraryCompletedAnswersWithoutTirId: Gen[UserAnswers] = arbitraryUserAnswers(
    Arbitrary((DeclarationTypePage, Json.toJson[DeclarationType](Option4))).arbitrary ::
      Arbitrary((TirIdentificationYesNoPage, JsBoolean(false))).arbitrary ::
      arbitraryTransitHolderNameUserAnswersEntry.arbitrary ::
      arbitraryTransitHolderAddressUserAnswersEntry.arbitrary ::
      Arbitrary((AddContactPage, JsBoolean(false))).arbitrary ::
      Nil
  )

  lazy val arbitraryCompletedAnswersWithAdditionalContact: Gen[UserAnswers] = arbitraryUserAnswers(
    arbitraryNonOption4DeclarationTypeUserAnswersEntry.arbitrary ::
      Arbitrary((EoriYesNoPage, JsBoolean(false))).arbitrary ::
      arbitraryTransitHolderNameUserAnswersEntry.arbitrary ::
      arbitraryTransitHolderAddressUserAnswersEntry.arbitrary ::
      Arbitrary((AddContactPage, JsBoolean(true))).arbitrary ::
      arbitraryTransitHolderAdditionalContactNameUserAnswersEntry.arbitrary ::
      arbitraryTransitHolderAdditionalContactTelephoneNumberUserAnswersEntry.arbitrary ::
      Nil
  )
}
