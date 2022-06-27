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

import base.SpecBase
import models.DeclarationType.Option4
import models.UserAnswers
import org.scalacheck.{Arbitrary, Gen}
import pages.preTaskList.DeclarationTypePage
import pages.traderDetails.holderOfTransit._
import play.api.libs.json.JsBoolean

trait HolderOfTransitUserAnswersGenerator extends UserAnswersGenerator {
  self: Generators with SpecBase =>

  def arbitraryHolderOfTransitAnswers(userAnswers: UserAnswers): Gen[UserAnswers] = {
    val declarationType = userAnswers.getValue(DeclarationTypePage)

    combineUserAnswers(
      Gen.const(userAnswers),
      declarationType match {
        case Option4 => arbitraryOption4HolderOfTransitAnswers
        case _       => arbitraryNonOption4HolderOfTransitAnswers
      }
    )
  }

  private lazy val arbitraryOption4HolderOfTransitAnswers: Gen[UserAnswers] = combineUserAnswers(
    Gen.oneOf(arbitraryHolderOfTransitWithTirId, arbitraryHolderOfTransitWithoutTirId),
    arbitraryUserAnswers(
      arbitraryTransitHolderNameUserAnswersEntry.arbitrary ::
        arbitraryTransitHolderAddressUserAnswersEntry.arbitrary ::
        Nil
    ),
    Gen.oneOf(arbitraryHolderOfTransitWithContact, arbitraryHolderOfTransitWithoutContact)
  )

  private lazy val arbitraryNonOption4HolderOfTransitAnswers: Gen[UserAnswers] = combineUserAnswers(
    Gen.oneOf(arbitraryHolderOfTransitWithEori, arbitraryHolderOfTransitWithoutEori),
    arbitraryUserAnswers(
      arbitraryTransitHolderNameUserAnswersEntry.arbitrary ::
        arbitraryTransitHolderAddressUserAnswersEntry.arbitrary ::
        Nil
    ),
    Gen.oneOf(arbitraryHolderOfTransitWithContact, arbitraryHolderOfTransitWithoutContact)
  )

  private lazy val arbitraryHolderOfTransitWithEori: Gen[UserAnswers] = arbitraryUserAnswers(
    Arbitrary((EoriYesNoPage, JsBoolean(true))).arbitrary ::
      arbitraryTransitHolderEoriUserAnswersEntry.arbitrary ::
      Nil
  )

  private lazy val arbitraryHolderOfTransitWithoutEori: Gen[UserAnswers] = arbitraryUserAnswers(
    Arbitrary((EoriYesNoPage, JsBoolean(false))).arbitrary ::
      Nil
  )

  private lazy val arbitraryHolderOfTransitWithTirId: Gen[UserAnswers] = arbitraryUserAnswers(
    Arbitrary((TirIdentificationYesNoPage, JsBoolean(true))).arbitrary ::
      arbitraryTransitHolderTirIdentificationUserAnswersEntry.arbitrary ::
      Nil
  )

  private lazy val arbitraryHolderOfTransitWithoutTirId: Gen[UserAnswers] = arbitraryUserAnswers(
    Arbitrary((TirIdentificationYesNoPage, JsBoolean(false))).arbitrary ::
      Nil
  )

  private lazy val arbitraryHolderOfTransitWithContact: Gen[UserAnswers] = arbitraryUserAnswers(
    Arbitrary((AddContactPage, JsBoolean(true))).arbitrary ::
      arbitraryTransitHolderAdditionalContactNameUserAnswersEntry.arbitrary ::
      arbitraryTransitHolderAdditionalContactTelephoneNumberUserAnswersEntry.arbitrary ::
      Nil
  )

  private lazy val arbitraryHolderOfTransitWithoutContact: Gen[UserAnswers] = arbitraryUserAnswers(
    Arbitrary((AddContactPage, JsBoolean(false))).arbitrary ::
      Nil
  )
}
