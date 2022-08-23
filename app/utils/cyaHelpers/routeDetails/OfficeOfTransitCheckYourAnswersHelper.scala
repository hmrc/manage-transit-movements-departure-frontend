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

package utils.cyaHelpers.routeDetails

import models.reference.{Country, CustomsOffice}
import models.{DateTime, Index, Mode, UserAnswers}
import pages.routeDetails.transit.index.{AddOfficeOfTransitETAYesNoPage, OfficeOfTransitCountryPage, OfficeOfTransitETAPage, OfficeOfTransitPage}
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.Aliases.SummaryListRow
import utils.cyaHelpers.AnswersHelper

class OfficeOfTransitCheckYourAnswersHelper(userAnswers: UserAnswers, mode: Mode, index: Index)(implicit messages: Messages)
    extends AnswersHelper(userAnswers, mode) {

  def officeOfTransitCountry: Option[SummaryListRow] = getAnswerAndBuildRow[Country](
    page = OfficeOfTransitCountryPage(index),
    formatAnswer = formatAsText,
    prefix = "routeDetails.transit.officeOfTransitCountry",
    id = Some("office-of-transit-country")
  )

  def officeOfTransit: Option[SummaryListRow] = getAnswerAndBuildRow[CustomsOffice](
    page = OfficeOfTransitPage(index),
    formatAnswer = formatAsText,
    prefix = "routeDetails.transit.officeOfExit",
    id = Some("office-of-transit")
  )

  def addOfficeOfTransitETA: Option[SummaryListRow] = getAnswerAndBuildRow[Boolean](
    page = AddOfficeOfTransitETAYesNoPage(index),
    formatAnswer = formatAsYesOrNo,
    prefix = "routeDetails.transit.addOfficeOfTransitETAYesNo",
    id = Some("office-of-transit-add-eta")
  )

  def officeOfTransitETA: Option[SummaryListRow] = getAnswerAndBuildRow[DateTime](
    page = OfficeOfTransitETAPage(index),
    formatAnswer = formatAsDateTime,
    prefix = "routeDetails.transit.officeOfTransitETA",
    id = Some("office-of-transit-eta")
  )

}
