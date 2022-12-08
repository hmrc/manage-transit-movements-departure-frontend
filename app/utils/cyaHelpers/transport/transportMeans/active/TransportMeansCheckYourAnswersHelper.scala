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

package utils.cyaHelpers.transport.transportMeans.active

import models.reference.{CustomsOffice, Nationality}
import models.transport.transportMeans.active.{Identification => ActiveIdentification}
import models.transport.transportMeans.departure.{Identification, InlandMode, Identification => DepartureIdentification}
import models.{Index, Mode, UserAnswers}
import pages.transport.transportMeans.active._
import pages.transport.transportMeans.departure._
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.Aliases.SummaryListRow
import utils.cyaHelpers.AnswersHelper

class TransportMeansCheckYourAnswersHelper(userAnswers: UserAnswers, mode: Mode)(implicit messages: Messages) extends AnswersHelper(userAnswers, mode) {

  def inlandMode: Option[SummaryListRow] = getAnswerAndBuildRow[InlandMode](
    page = InlandModePage,
    formatAnswer =  formatEnumAsText(InlandMode.messageKeyPrefix),
    prefix = "transport.transportMeans.departure.inlandMode",
    id = Some("change-transport-means-inlandMode")
  )

  def  departureIdentificationType: Option[SummaryListRow] = getAnswerAndBuildRow[DepartureIdentification](
    page = IdentificationPage,
    formatAnswer = formatEnumAsText(Identification.messageKeyPrefix),
    prefix = "transport.transportMeans.departure.identification",
    id = Some("change-transport-means-identification")
  )

  def departureIdentificationNumber: Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = MeansIdentificationNumberPage,
    formatAnswer = formatEnumAsText(Identification.messageKeyPrefix),
    prefix = "transport.transportMeans.departure.meansIdentificationNumber",
    id = Some("change-transport-means-identification-number")
  )

  def departureNationality: Option[SummaryListRow] = getAnswerAndBuildRow[Nationality](
    page = VehicleCountryPage,
    formatAnswer = formatEnumAsText(Identification.messageKeyPrefix),
    prefix = "transport.transportMeans.departure.vehicleNationality",
    id = Some("change-transport-means-vehicle-nationality")
  )

  def activeBorderIdentificationType(index: Index): Option[SummaryListRow] = getAnswerAndBuildRow[ActiveIdentification](
    page = pages.transport.transportMeans.active.IdentificationPage(index),
    formatAnswer = formatEnumAsText(Identification.messageKeyPrefix),
    prefix = "transport.transportMeans.active.identification",
    id = Some("change-transport-means-identification")
  )

  def activeBorderIdentificationNumber(index: Index): Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = pages.transport.transportMeans.active.IdentificationNumberPage(index),
    formatAnswer = formatEnumAsText(Identification.messageKeyPrefix),
    prefix = "transport.transportMeans.active.meansIdentificationNumber",
    id = Some("change-transport-means-identification-number")
  )

  def activeBorderNationality(index: Index): Option[SummaryListRow] = getAnswerAndBuildRow[Nationality](
    page = pages.transport.transportMeans.active.NationalityPage(index),
    formatAnswer = formatEnumAsText(Identification.messageKeyPrefix),
    prefix = "transport.transportMeans.active.vehicleNationality",
    id = Some("change-transport-means-vehicle-nationality")
  )

  def activeBorderAddNationality(index: Index): Option[SummaryListRow] = getAnswerAndBuildRow[Boolean](
    page = pages.transport.transportMeans.active.AddNationalityYesNoPage(index),
    formatAnswer = formatEnumAsText(Identification.messageKeyPrefix),
    prefix = "transport.transportMeans.active.addNationalityYesNo",
    id = Some("change-add-transport-means-vehicle-nationality")
  )

  def customsOfficeAtBorder(index: Index): Option[SummaryListRow] = getAnswerAndBuildRow[CustomsOffice](
    page = pages.transport.transportMeans.active.CustomsOfficeActiveBorderPage(index),
    formatAnswer = formatEnumAsText(Identification.messageKeyPrefix),
    prefix = "transport.transportMeans.active.customsOfficeActiveBorder",
    id = Some("change-transport-means-customs-office-at-border")
  )

  def activeBorderConveyanceReferenceNumber(index: Index): Option[SummaryListRow] = getAnswerAndBuildRow[Boolean](
    page = pages.transport.transportMeans.active.ConveyanceReferenceNumberYesNoPage(index),
    formatAnswer = formatEnumAsText(Identification.messageKeyPrefix),
    prefix = "transport.transportMeans.active.conveyanceReferenceNumberYesNo",
    id = Some("change-add-transport-means-conveyance-reference-number")
  )

  def conveyanceReferenceNumber(index: Index): Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = pages.transport.transportMeans.active.ConveyanceReferenceNumberPage(index),
    formatAnswer = formatEnumAsText(Identification.messageKeyPrefix),
    prefix = "transport.transportMeans.active.conveyanceReferenceNumber",
    id = Some("change-transport-means-conveyance-reference-number")
  )

}
