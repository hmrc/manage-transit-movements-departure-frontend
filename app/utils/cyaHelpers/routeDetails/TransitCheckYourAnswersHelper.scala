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

import controllers.routeDetails.transit.index.routes
import models.journeyDomain.routeDetails.transit.OfficeOfTransitDomain
import models.reference.{Country, CustomsOffice}
import models.{Index, Mode, UserAnswers}
import pages.routeDetails.transit.AddOfficeOfTransitYesNoPage
import pages.routeDetails.transit.index.{OfficeOfTransitCountryPage, OfficeOfTransitPage}
import pages.sections.routeDetails.transit.OfficesOfTransitSection
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.Aliases.SummaryListRow
import utils.cyaHelpers.AnswersHelper
import viewModels.ListItem

class TransitCheckYourAnswersHelper(
  userAnswers: UserAnswers,
  mode: Mode
)(
  ctcCountryCodes: Seq[String],
  customsSecurityAgreementAreaCountryCodes: Seq[String]
)(implicit messages: Messages)
    extends AnswersHelper(userAnswers, mode) {

  def addOfficeOfTransit: Option[SummaryListRow] = getAnswerAndBuildRow[Boolean](
    page = AddOfficeOfTransitYesNoPage,
    formatAnswer = formatAsYesOrNo,
    prefix = "routeDetails.transit.addOfficeOfTransitYesNo",
    id = Some("add-office-of-transit")
  )

  def officeOfTransit(index: Index): Option[SummaryListRow] = getAnswerAndBuildSectionRow[OfficeOfTransitDomain, CustomsOffice](
    page = OfficeOfTransitPage(index),
    formatAnswer = formatAsText,
    prefix = "routeDetails.transit.checkYourAnswers.countryOfRouting",
    id = Some(s"change-office-of-transit-${index.display}"),
    args = index.display
  )(OfficeOfTransitDomain.userAnswersReader(index, ctcCountryCodes, customsSecurityAgreementAreaCountryCodes), implicitly)

  def listItems: Seq[Either[ListItem, ListItem]] =
    buildListItems(OfficesOfTransitSection) {
      position =>
        val index = Index(position)
        buildListItem[OfficeOfTransitDomain, Country](
          page = OfficeOfTransitCountryPage(index),
          formatJourneyDomainModel = _.label,
          formatType = _.toString,
          removeRoute = if (position == 0) None else Some(routes.ConfirmRemoveOfficeOfTransitController.onPageLoad(userAnswers.lrn, mode, index))
        )(
          OfficeOfTransitDomain.userAnswersReader(index, ctcCountryCodes, customsSecurityAgreementAreaCountryCodes),
          implicitly
        )
    }
}
