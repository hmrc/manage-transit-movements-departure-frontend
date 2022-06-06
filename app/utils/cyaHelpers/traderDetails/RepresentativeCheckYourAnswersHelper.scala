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

package utils.cyaHelpers.traderDetails

import controllers.traderDetails.representative.routes._
import models.traderDetails.representative.RepresentativeCapacity
import models.{Mode, UserAnswers}
import pages.traderDetails.representative._
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.Aliases.SummaryListRow
import utils.cyaHelpers.AnswersHelper

class RepresentativeCheckYourAnswersHelper(userAnswers: UserAnswers, mode: Mode)(implicit messages: Messages) extends AnswersHelper(userAnswers) {

  def actingRepresentative: Option[SummaryListRow] = getAnswerAndBuildRow[Boolean](
    page = ActingAsRepresentativePage,
    formatAnswer = formatAsYesOrNo,
    prefix = "traderDetails.representative.actingRepresentative",
    id = None,
    call = ActingAsRepresentativeController.onPageLoad(lrn, mode)
  )

  def representativeEori: Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = EoriPage,
    formatAnswer = formatAsLiteral,
    prefix = "traderDetails.representative.representativeEori",
    id = None,
    call = EoriController.onPageLoad(lrn, mode)
  )

  def representativeName: Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = NamePage,
    formatAnswer = formatAsLiteral,
    prefix = "traderDetails.representative.representativeName",
    id = None,
    call = NameController.onPageLoad(lrn, mode)
  )

  def representativeCapacity: Option[SummaryListRow] = getAnswerAndBuildRow[RepresentativeCapacity](
    page = CapacityPage,
    formatAnswer = formatAsEnum(RepresentativeCapacity.messageKeyPrefix),
    prefix = "traderDetails.representative.representativeCapacity",
    id = None,
    call = CapacityController.onPageLoad(lrn, mode)
  )

  def representativePhone: Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = TelephoneNumberPage,
    formatAnswer = formatAsLiteral,
    prefix = "traderDetails.representative.representativePhone",
    id = None,
    call = TelephoneNumberController.onPageLoad(lrn, mode)
  )
}
