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

package utils.cyaHelpers.routeDetails.exit

import controllers.routeDetails.exit.index.routes
import models.journeyDomain.routeDetails.exit.OfficeOfExitDomain
import models.reference.{Country, CustomsOffice}
import models.{Index, Mode, UserAnswers}
import pages.routeDetails.exit.index.{OfficeOfExitCountryPage, OfficeOfExitPage}
import pages.sections.routeDetails.exit.OfficesOfExitSection
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.Aliases.SummaryListRow
import utils.cyaHelpers.AnswersHelper
import viewModels.ListItem

class ExitCheckYourAnswersHelper(
  userAnswers: UserAnswers,
  mode: Mode
)(implicit messages: Messages)
    extends AnswersHelper(userAnswers, mode) {

  def officeOfExit(index: Index): Option[SummaryListRow] = getAnswerAndBuildSectionRow[OfficeOfExitDomain, CustomsOffice](
    page = OfficeOfExitPage(index),
    formatAnswer = formatAsText,
    prefix = "routeDetails.checkYourAnswers.exit.officeOfExit",
    id = Some(s"change-office-of-exit-${index.display}"),
    args = index.display
  )(OfficeOfExitDomain.userAnswersReader(index), implicitly)

  def listItems: Seq[Either[ListItem, ListItem]] =
    buildListItems(OfficesOfExitSection) {
      index =>
        buildListItem[OfficeOfExitDomain, Country](
          page = OfficeOfExitCountryPage(index),
          formatJourneyDomainModel = _.label,
          formatType = _.toString,
          removeRoute = Some(routes.ConfirmRemoveOfficeOfExitController.onPageLoad(userAnswers.lrn, index, mode))
        )(OfficeOfExitDomain.userAnswersReader(index), implicitly)
    }
}
