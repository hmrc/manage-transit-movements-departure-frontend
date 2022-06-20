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

import models.{SecurityDetailsType, UserAnswers}
import org.scalacheck.{Arbitrary, Gen}
import pages.preTaskList.SecurityDetailsTypePage
import pages.traderDetails.consignment._
import play.api.libs.json.{JsBoolean, JsString}

trait TraderDetailsConsignmentAnswersGenerator extends UserAnswersGenerator {
  self: Generators =>

  lazy val arbitraryTraderDetailsConsignmentAnswersWithConsignorAndConsignee: Gen[UserAnswers] = arbitraryUserAnswers(
    Arbitrary((SecurityDetailsTypePage, JsString(SecurityDetailsType.EntrySummaryDeclarationSecurityDetails.toString))).arbitrary ::
      Arbitrary((ApprovedOperatorPage, JsBoolean(true))).arbitrary ::
      Arbitrary((consignor.EoriYesNoPage, JsBoolean(true))).arbitrary ::
      arbitraryTraderDetailsConsignmentConsignorEoriUserAnswersEntry.arbitrary ::
      arbitraryTraderDetailsConsignmentConsignorNameUserAnswersEntry.arbitrary ::
      arbitraryTraderDetailsConsignmentConsignorAddressUserAnswersEntry.arbitrary ::
      Arbitrary((consignor.AddContactPage, JsBoolean(true))).arbitrary ::
      arbitraryTraderDetailsConsignmentConsignorContactNameUserAnswersEntry.arbitrary ::
      arbitraryTraderDetailsConsignmentConsignorContactTelephoneNumberUserAnswersEntry.arbitrary ::
      Arbitrary((consignee.MoreThanOneConsigneePage, JsBoolean(false))).arbitrary ::
      Arbitrary((consignee.EoriYesNoPage, JsBoolean(true))).arbitrary ::
      arbitraryTraderDetailsConsignmentConsigneeEoriNumberUserAnswersEntry.arbitrary ::
      arbitraryTraderdetailsConsignmentConsigneeNameUserAnswersEntry.arbitrary ::
      arbitraryTraderdetailsConsignmentConsigneeAddressUserAnswersEntry.arbitrary ::
      Nil
  )

  lazy val arbitraryTraderDetailsConsignmentAnswersWithoutConsignorOrConsignee: Gen[UserAnswers] = arbitraryUserAnswers(
    Arbitrary((SecurityDetailsTypePage, JsString(SecurityDetailsType.EntrySummaryDeclarationSecurityDetails.toString))).arbitrary ::
      Arbitrary((ApprovedOperatorPage, JsBoolean(false))).arbitrary ::
      arbitraryTraderdetailsConsignmentApprovedOperatorUserAnswersEntry.arbitrary ::
      Arbitrary((consignee.MoreThanOneConsigneePage, JsBoolean(true))).arbitrary ::
      Nil
  )

  lazy val arbitraryTraderDetailsConsignmentAnswersWithoutConsignorEori: Gen[UserAnswers] = arbitraryUserAnswers(
    Arbitrary((SecurityDetailsTypePage, JsString(SecurityDetailsType.EntrySummaryDeclarationSecurityDetails.toString))).arbitrary ::
      Arbitrary((ApprovedOperatorPage, JsBoolean(true))).arbitrary ::
      arbitraryTraderdetailsConsignmentApprovedOperatorUserAnswersEntry.arbitrary ::
      Arbitrary((consignor.EoriYesNoPage, JsBoolean(false))).arbitrary ::
      arbitraryTraderDetailsConsignmentConsignorNameUserAnswersEntry.arbitrary ::
      arbitraryTraderDetailsConsignmentConsignorAddressUserAnswersEntry.arbitrary ::
      Arbitrary((consignee.MoreThanOneConsigneePage, JsBoolean(true))).arbitrary ::
      Nil
  )

  lazy val arbitraryTraderDetailsConsignmentAnswersWithConsignorWithoutContact: Gen[UserAnswers] = arbitraryUserAnswers(
    Arbitrary((SecurityDetailsTypePage, JsString(SecurityDetailsType.EntrySummaryDeclarationSecurityDetails.toString))).arbitrary ::
      Arbitrary((ApprovedOperatorPage, JsBoolean(true))).arbitrary ::
      Arbitrary((consignor.EoriYesNoPage, JsBoolean(true))).arbitrary ::
      arbitraryTraderDetailsConsignmentConsignorEoriUserAnswersEntry.arbitrary ::
      arbitraryTraderDetailsConsignmentConsignorNameUserAnswersEntry.arbitrary ::
      arbitraryTraderDetailsConsignmentConsignorAddressUserAnswersEntry.arbitrary ::
      Arbitrary((consignor.AddContactPage, JsBoolean(false))).arbitrary ::
      Arbitrary((consignee.MoreThanOneConsigneePage, JsBoolean(true))).arbitrary ::
      Nil
  )

  lazy val arbitraryTraderDetailsConsignmentAnswers: Gen[UserAnswers] = Gen.oneOf(
    arbitraryTraderDetailsConsignmentAnswersWithConsignorAndConsignee,
    arbitraryTraderDetailsConsignmentAnswersWithoutConsignorOrConsignee,
    arbitraryTraderDetailsConsignmentAnswersWithoutConsignorEori,
    arbitraryTraderDetailsConsignmentAnswersWithConsignorWithoutContact
  )
}
