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

import models.UserAnswers
import org.scalacheck.{Arbitrary, Gen}
import pages.traderDetails.consignment._
import play.api.libs.json.JsBoolean

trait TraderDetailsConsignmentAnswersGenerator extends UserAnswersGenerator {
  self: Generators =>

  lazy val arbitraryTraderDetailsConsignmentAnswersWithConsignorEori: Gen[UserAnswers] = arbitraryUserAnswers(
    arbitraryTraderdetailsConsignmentApprovedOperatorUserAnswersEntry.arbitrary ::
      Arbitrary((consignor.EoriYesNoPage, JsBoolean(true))).arbitrary ::
      arbitraryTransitHolderConsignmentConsignorEoriUserAnswersEntry.arbitrary ::
      arbitraryTransitHolderConsignmentConsignorNameUserAnswersEntry.arbitrary ::
      arbitraryTransitHolderConsignmentConsignorAddressUserAnswersEntry.arbitrary ::
      Nil
  )

  lazy val arbitraryTraderDetailsConsignmentAnswersWithoutConsignor: Gen[UserAnswers] = arbitraryUserAnswers(
    arbitraryTraderdetailsConsignmentApprovedOperatorUserAnswersEntry.arbitrary ::
      Nil
  )

  lazy val arbitraryTraderDetailsConsignmentAnswersWithoutConsignorEori: Gen[UserAnswers] = arbitraryUserAnswers(
    arbitraryTraderdetailsConsignmentApprovedOperatorUserAnswersEntry.arbitrary ::
      Arbitrary((consignor.EoriYesNoPage, JsBoolean(false))).arbitrary ::
      arbitraryTransitHolderConsignmentConsignorNameUserAnswersEntry.arbitrary ::
      arbitraryTransitHolderConsignmentConsignorAddressUserAnswersEntry.arbitrary ::
      Nil
  )

  lazy val arbitraryTraderDetailsConsignmentAnswers: Gen[UserAnswers] = Gen.oneOf(
    arbitraryTraderDetailsConsignmentAnswersWithConsignorEori,
    arbitraryTraderDetailsConsignmentAnswersWithoutConsignor,
    arbitraryTraderDetailsConsignmentAnswersWithoutConsignorEori
  )
}
