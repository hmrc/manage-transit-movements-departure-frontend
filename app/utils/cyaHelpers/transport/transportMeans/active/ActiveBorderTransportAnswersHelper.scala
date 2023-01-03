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

package utils.cyaHelpers.transport.transportMeans.active

import models.reference.{CustomsOffice, Nationality}
import models.transport.transportMeans.active.Identification
import models.{Index, Mode, UserAnswers}
import pages.transport.transportMeans.active._
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.Aliases.SummaryListRow
import utils.cyaHelpers.AnswersHelper

class ActiveBorderTransportAnswersHelper(
  userAnswers: UserAnswers,
  mode: Mode,
  index: Index
)(implicit messages: Messages)
    extends AnswersHelper(userAnswers, mode) {

  def activeBorderIdentificationType: Option[SummaryListRow] = getAnswerAndBuildRow[Identification](
    page = IdentificationPage(index),
    formatAnswer = formatEnumAsText(Identification.messageKeyPrefix),
    prefix = "transport.transportMeans.active.identification",
    id = Some("change-transport-means-identification")
  )

  def activeBorderIdentificationNumber: Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = IdentificationNumberPage(index),
    formatAnswer = formatAsText,
    prefix = "transport.transportMeans.active.identificationNumber",
    id = Some("change-transport-means-identification-number")
  )

  def activeBorderAddNationality: Option[SummaryListRow] = getAnswerAndBuildRow[Boolean](
    page = AddNationalityYesNoPage(index),
    formatAnswer = formatAsYesOrNo,
    prefix = "transport.transportMeans.active.addNationalityYesNo",
    id = Some("change-add-transport-means-vehicle-nationality")
  )

  def activeBorderNationality: Option[SummaryListRow] = getAnswerAndBuildRow[Nationality](
    page = NationalityPage(index),
    formatAnswer = formatAsText,
    prefix = "transport.transportMeans.active.nationality",
    id = Some("change-transport-means-vehicle-nationality")
  )

  def customsOfficeAtBorder: Option[SummaryListRow] = getAnswerAndBuildRow[CustomsOffice](
    page = CustomsOfficeActiveBorderPage(index),
    formatAnswer = formatAsText,
    prefix = "transport.transportMeans.active.customsOfficeActiveBorder",
    id = Some("change-transport-means-customs-office-at-border")
  )

  def activeBorderConveyanceReferenceNumberYesNo: Option[SummaryListRow] = getAnswerAndBuildRow[Boolean](
    page = ConveyanceReferenceNumberYesNoPage(index),
    formatAnswer = formatAsYesOrNo,
    prefix = "transport.transportMeans.active.conveyanceReferenceNumberYesNo",
    id = Some("change-add-transport-means-conveyance-reference-number")
  )

  def conveyanceReferenceNumber: Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = ConveyanceReferenceNumberPage(index),
    formatAnswer = formatAsText,
    prefix = "transport.transportMeans.active.conveyanceReferenceNumber",
    id = Some("change-transport-means-conveyance-reference-number")
  )

}

object ActiveBorderTransportAnswersHelper {

  def apply(userAnswers: UserAnswers, mode: Mode, index: Index)(implicit messages: Messages): Seq[SummaryListRow] = {
    val helper = new ActiveBorderTransportAnswersHelper(userAnswers, mode, index)
    Seq(
      helper.activeBorderIdentificationType,
      helper.activeBorderIdentificationNumber,
      helper.activeBorderAddNationality,
      helper.activeBorderNationality,
      helper.customsOfficeAtBorder,
      helper.activeBorderConveyanceReferenceNumberYesNo,
      helper.conveyanceReferenceNumber
    ).flatten
  }

}
