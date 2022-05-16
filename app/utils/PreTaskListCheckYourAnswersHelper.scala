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

import models.reference.CustomsOffice
import models.{DeclarationType, Mode, ProcedureType, SecurityDetailsType, UserAnswers}
import pages._
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.Aliases.SummaryListRow

class PreTaskListCheckYourAnswersHelper(userAnswers: UserAnswers, mode: Mode)(implicit messages: Messages) extends AnswersHelper(userAnswers) {

  def localReferenceNumber: SummaryListRow = buildRow(
    prefix = "localReferenceNumber",
    answer = formatAsLiteral(lrn),
    id = None,
    call = controllers.routes.LocalReferenceNumberController.onPageLoad()
  )

  def officeOfDeparture: Option[SummaryListRow] = getAnswerAndBuildRow[CustomsOffice](
    page = OfficeOfDeparturePage,
    formatAnswer = formatAsLiteral,
    prefix = "officeOfDeparture",
    id = None,
    call = controllers.routes.OfficeOfDepartureController.onPageLoad(lrn, mode)
  )

  def procedureType: Option[SummaryListRow] = getAnswerAndBuildRow[ProcedureType](
    page = ProcedureTypePage,
    formatAnswer = formatAsEnum(ProcedureType.messageKeyPrefix),
    prefix = "procedureType",
    id = None,
    call = controllers.routes.ProcedureTypeController.onPageLoad(lrn, mode)
  )

  def declarationType: Option[SummaryListRow] = getAnswerAndBuildRow[DeclarationType](
    page = DeclarationTypePage,
    formatAnswer = formatAsEnum(DeclarationType.messageKeyPrefix),
    prefix = "declarationType",
    id = None,
    call = controllers.routes.DeclarationTypeController.onPageLoad(lrn, mode)
  )

  def tirCarnet: Option[SummaryListRow] = None // TODO

  def securityType: Option[SummaryListRow] = getAnswerAndBuildRow[SecurityDetailsType](
    page = SecurityDetailsTypePage,
    formatAnswer = formatAsEnum(SecurityDetailsType.messageKeyPrefix),
    prefix = "securityDetailsType",
    id = None,
    call = controllers.routes.SecurityDetailsTypeController.onPageLoad(lrn, mode)
  )
}
