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
import models.DeclarationType._
import models.SecurityDetailsType._
import models.UserAnswers
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Arbitrary, Gen}
import pages.preTaskList.{DeclarationTypePage, SecurityDetailsTypePage}
import pages.traderDetails.consignment._
import play.api.libs.json.JsBoolean

trait ConsignmentAnswersGenerator extends UserAnswersGenerator {
  self: Generators with SpecBase =>

  def arbitraryConsignmentAnswers(userAnswers: UserAnswers): Gen[UserAnswers] =
    arbitraryConsignmentAnswers(userAnswers, arbitraryConsigneeAnswers)

  def arbitraryConsignmentAnswersWithConsignor(userAnswers: UserAnswers): Gen[UserAnswers] = {
    val declarationType = userAnswers.getValue(DeclarationTypePage)
    val securityType    = userAnswers.getValue(SecurityDetailsTypePage)

    combineUserAnswers(
      Gen.const(userAnswers),
      declarationType match {
        case Option4 =>
          combineUserAnswers(
            arbitraryConsignorAnswers,
            arbitraryConsigneeAnswers
          )
        case _ =>
          securityType match {
            case NoSecurityDetails =>
              combineUserAnswers(
                arbitraryUserAnswers(Arbitrary((ApprovedOperatorPage, JsBoolean(false))).arbitrary :: Nil),
                arbitraryConsignorAnswers,
                arbitraryConsigneeAnswers
              )
            case _ =>
              val reducedDataSet = arbitrary[Boolean].sample.value
              combineUserAnswers(
                arbitraryUserAnswers(Arbitrary((ApprovedOperatorPage, JsBoolean(reducedDataSet))).arbitrary :: Nil),
                arbitraryConsignorAnswers,
                arbitraryConsigneeAnswers
              )
          }
      }
    )
  }

  def arbitraryConsignmentAnswersWithOneConsignee(userAnswers: UserAnswers): Gen[UserAnswers] =
    arbitraryConsignmentAnswers(userAnswers, arbitraryConsigneeAnswersWithOneConsignee)

  private def arbitraryConsignmentAnswers(userAnswers: UserAnswers, arbitraryConsigneeAnswers: Gen[UserAnswers]): Gen[UserAnswers] = {
    val declarationType = userAnswers.getValue(DeclarationTypePage)
    val securityType    = userAnswers.getValue(SecurityDetailsTypePage)
    val reducedDataSet  = arbitrary[Boolean].sample.value

    combineUserAnswers(
      Gen.const(userAnswers),
      declarationType match {
        case Option4 =>
          combineUserAnswers(
            arbitraryConsignorAnswers,
            arbitraryConsigneeAnswers
          )
        case _ =>
          (securityType, reducedDataSet) match {
            case (NoSecurityDetails, true) =>
              combineUserAnswers(
                arbitraryUserAnswers(Arbitrary((ApprovedOperatorPage, JsBoolean(reducedDataSet))).arbitrary :: Nil),
                arbitraryConsigneeAnswers
              )
            case _ =>
              combineUserAnswers(
                arbitraryUserAnswers(Arbitrary((ApprovedOperatorPage, JsBoolean(reducedDataSet))).arbitrary :: Nil),
                arbitraryConsignorAnswers,
                arbitraryConsigneeAnswers
              )
          }
      }
    )
  }

  private lazy val arbitraryConsignorAnswers: Gen[UserAnswers] = combineUserAnswers(
    Gen.oneOf(
      arbitraryUserAnswers(
        Arbitrary((consignor.EoriYesNoPage, JsBoolean(true))).arbitrary ::
          arbitraryTraderDetailsConsignmentConsignorEoriUserAnswersEntry.arbitrary ::
          Nil
      ),
      arbitraryUserAnswers(
        Arbitrary((consignor.EoriYesNoPage, JsBoolean(false))).arbitrary ::
          Nil
      )
    ),
    arbitraryUserAnswers(
      arbitraryTraderDetailsConsignmentConsignorNameUserAnswersEntry.arbitrary ::
        arbitraryTraderDetailsConsignmentConsignorAddressUserAnswersEntry.arbitrary ::
        Nil
    ),
    Gen.oneOf(
      arbitraryUserAnswers(
        Arbitrary((consignor.AddContactPage, JsBoolean(true))).arbitrary ::
          arbitraryTraderDetailsConsignmentConsignorContactNameUserAnswersEntry.arbitrary ::
          arbitraryTraderDetailsConsignmentConsignorContactTelephoneNumberUserAnswersEntry.arbitrary ::
          Nil
      ),
      arbitraryUserAnswers(
        Arbitrary((consignor.AddContactPage, JsBoolean(false))).arbitrary ::
          Nil
      )
    )
  )

  private lazy val arbitraryConsigneeAnswers: Gen[UserAnswers] = Gen.oneOf(
    arbitraryConsigneeAnswersWithMoreThanOneConsignee,
    arbitraryConsigneeAnswersWithOneConsignee
  )

  private lazy val arbitraryConsigneeAnswersWithMoreThanOneConsignee: Gen[UserAnswers] = arbitraryUserAnswers(
    Arbitrary((consignee.MoreThanOneConsigneePage, JsBoolean(true))).arbitrary ::
      Nil
  )

  private lazy val arbitraryConsigneeAnswersWithOneConsignee: Gen[UserAnswers] = arbitraryUserAnswers(
    Arbitrary((consignee.MoreThanOneConsigneePage, JsBoolean(false))).arbitrary ::
      Arbitrary((consignee.EoriYesNoPage, JsBoolean(true))).arbitrary ::
      arbitraryTraderDetailsConsignmentConsigneeEoriNumberUserAnswersEntry.arbitrary ::
      arbitraryTraderdetailsConsignmentConsigneeNameUserAnswersEntry.arbitrary ::
      arbitraryTraderdetailsConsignmentConsigneeAddressUserAnswersEntry.arbitrary ::
      Nil
  )
}
