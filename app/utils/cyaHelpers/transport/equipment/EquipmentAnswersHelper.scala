/*
 * Copyright 2023 HM Revenue & Customs
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

package utils.cyaHelpers.transport.equipment

import models.{Index, Mode, UserAnswers}
import pages.transport.equipment.index._
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import utils.cyaHelpers.AnswersHelper

class EquipmentAnswersHelper(
  userAnswers: UserAnswers,
  mode: Mode,
  index: Index
)(implicit messages: Messages)
    extends AnswersHelper(userAnswers, mode) {

  def containerIdentificationNumberYesNo: Option[SummaryListRow] = getAnswerAndBuildRow[Boolean](
    page = AddContainerIdentificationNumberYesNoPage(index),
    formatAnswer = formatAsYesOrNo,
    prefix = "transport.equipment.index.addContainerIdentificationNumberYesNo",
    id = Some("change-add-container-identification-number")
  )

  def containerIdentificationNumber: Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = ContainerIdentificationNumberPage(index),
    formatAnswer = formatAsText,
    prefix = "transport.equipment.index.containerIdentificationNumber",
    id = Some("change-container-identification-number")
  )

  def sealsYesNo: Option[SummaryListRow] = getAnswerAndBuildRow[Boolean](
    page = AddSealYesNoPage(index),
    formatAnswer = formatAsYesOrNo,
    prefix = "transport.equipment.index.addSealYesNo",
    id = Some("change-add-seals")
  )

  def itemNumbersYesNo: Option[SummaryListRow] = getAnswerAndBuildRow[Boolean](
    page = AddGoodsItemNumberYesNoPage(index),
    formatAnswer = formatAsYesOrNo,
    prefix = "transport.equipment.index.addGoodsItemNumberYesNo",
    id = Some("change-add-item-numbers")
  )
}
