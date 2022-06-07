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

package utils

import controllers.traderDetails.representative.routes._
import models.traderDetails.representative.RepresentativeCapacity
import models.{Mode, UserAnswers}
import pages.traderDetails.representative._
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.Aliases.SummaryListRow

class RepresentativeCheckYourAnswersHelper(userAnswers: UserAnswers, mode: Mode)(implicit messages: Messages) extends AnswersHelper(userAnswers) {

  def actingRepresentative: Option[SummaryListRow] = getAnswerAndBuildRow[Boolean](
    page = ActingRepresentativePage,
    formatAnswer = formatAsYesOrNo,
    prefix = "traderDetails.representative.actingRepresentative",
    id = None,
    call = ActingRepresentativeController.onPageLoad(lrn, mode)
  )

  def representativeEori: Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = RepresentativeEoriPage,
    formatAnswer = formatAsLiteral,
    prefix = "traderDetails.representative.representativeEori",
    id = None,
    call = RepresentativeEoriController.onPageLoad(lrn, mode)
  )

  def representativeName: Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = RepresentativeNamePage,
    formatAnswer = formatAsLiteral,
    prefix = "traderDetails.representative.representativeName",
    id = None,
    call = RepresentativeNameController.onPageLoad(lrn, mode)
  )

  def representativeCapacity: Option[SummaryListRow] = getAnswerAndBuildRow[RepresentativeCapacity](
    page = RepresentativeCapacityPage,
    formatAnswer = formatAsEnum(RepresentativeCapacity.messageKeyPrefix),
    prefix = "traderDetails.representative.representativeCapacity",
    id = None,
    call = RepresentativeCapacityController.onPageLoad(lrn, mode)
  )

  def representativePhone: Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = RepresentativePhonePage,
    formatAnswer = formatAsLiteral,
    prefix = "traderDetails.representative.representativePhone",
    id = None,
    call = RepresentativePhoneController.onPageLoad(lrn, mode)
  )
}
