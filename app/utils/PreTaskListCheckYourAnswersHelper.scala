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

import controllers.goodsSummary.routes
import models.reference.CustomsOffice
import models.{Mode, UserAnswers}
import pages._
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.Aliases.SummaryListRow

class PreTaskListCheckYourAnswersHelper(userAnswers: UserAnswers, mode: Mode)(implicit messages: Messages) extends AnswersHelper(userAnswers) {

  def localReferenceNumber: Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = AgreedLocationOfGoodsPage,
    formatAnswer = formatAsLiteral,
    prefix = "agreedLocationOfGoods",
    id = None,
    call = routes.AgreedLocationOfGoodsController.onPageLoad(lrn, mode)
  )

  def officeOfDeparture: Option[SummaryListRow] = getAnswerAndBuildRow[CustomsOffice](
    page = OfficeOfDeparturePage,
    formatAnswer = formatAsLiteral,
    prefix = "agreedLocationOfGoods",
    id = None,
    call = routes.AgreedLocationOfGoodsController.onPageLoad(lrn, mode)
  )

  def procedureType: Option[SummaryListRow] = ???

  def declarationType: Option[SummaryListRow] = ???

  def tirCarnet: Option[SummaryListRow] = ???

  def securityType: Option[SummaryListRow] = ???
}
