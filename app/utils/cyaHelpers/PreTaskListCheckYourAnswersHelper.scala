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

package utils.cyaHelpers

import controllers.preTaskList.routes._
import models.reference.CustomsOffice
import models.{AdditionalDeclarationType, DeclarationType, Mode, ProcedureType, SecurityDetailsType, UserAnswers}
import pages.preTaskList._
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.Aliases.SummaryListRow

class PreTaskListCheckYourAnswersHelper(userAnswers: UserAnswers, mode: Mode)(implicit messages: Messages) extends AnswersHelper(userAnswers, mode) {

  def localReferenceNumber: SummaryListRow = buildRow(
    prefix = "localReferenceNumber",
    answer = formatAsText(lrn),
    id = None,
    call = LocalReferenceNumberController.onPageLoad()
  )

  def additionalDeclarationType: Option[SummaryListRow] = getAnswerAndBuildRow[AdditionalDeclarationType](
    page = AdditionalDeclarationTypePage,
    formatAnswer = formatEnumAsText(AdditionalDeclarationType.messageKeyPrefix),
    prefix = "additionalDeclarationType",
    id = None
  )

  def officeOfDeparture: Option[SummaryListRow] = getAnswerAndBuildRow[CustomsOffice](
    page = OfficeOfDeparturePage,
    formatAnswer = formatAsText,
    prefix = "officeOfDeparture",
    id = None
  )

  def procedureType: Option[SummaryListRow] = getAnswerAndBuildRow[ProcedureType](
    page = ProcedureTypePage,
    formatAnswer = formatEnumAsText(ProcedureType.messageKeyPrefix),
    prefix = "procedureType",
    id = None
  )

  def declarationType: Option[SummaryListRow] = getAnswerAndBuildRow[DeclarationType](
    page = DeclarationTypePage,
    formatAnswer = formatAsText,
    prefix = "declarationType",
    id = None
  )

  def tirCarnet: Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = TIRCarnetReferencePage,
    formatAnswer = formatAsText,
    prefix = "tirCarnetReference",
    id = None
  )

  def securityType: Option[SummaryListRow] = getAnswerAndBuildRow[SecurityDetailsType](
    page = SecurityDetailsTypePage,
    formatAnswer = formatEnumAsText(SecurityDetailsType.messageKeyPrefix),
    prefix = "securityDetailsType",
    id = None
  )
}
