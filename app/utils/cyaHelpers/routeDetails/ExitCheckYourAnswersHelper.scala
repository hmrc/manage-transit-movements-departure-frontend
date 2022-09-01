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

import controllers.routeDetails.officeOfExit.routes
import models.journeyDomain.routeDetails.exit.OfficeOfExitDomain
import models.reference.Country
import models.{Index, Mode, UserAnswers}
import pages.routeDetails.officeOfExit.index.OfficeOfExitCountryPage
import pages.sections.routeDetails.exit.OfficesOfExitSection
import play.api.i18n.Messages
import play.api.libs.json.Reads
import utils.cyaHelpers.AnswersHelper
import viewModels.ListItem

class ExitCheckYourAnswersHelper(
  userAnswers: UserAnswers,
  mode: Mode
)(implicit messages: Messages)
    extends AnswersHelper(userAnswers, mode) {

  def listItems: Seq[Either[ListItem, ListItem]] =
    buildListItems(OfficesOfExitSection) {
      position =>
        val index = Index(position)
        buildListItem[OfficeOfExitDomain, Country](
          page = OfficeOfExitCountryPage(index),
          formatJourneyDomainModel = _.label,
          formatType = _.toString,
          removeRoute = Some(routes.ConfirmRemoveOfficeOfExitController.onPageLoad(userAnswers.lrn, index))
        )(
          OfficeOfExitDomain.userAnswersReader(index),
          implicitly[Reads[Country]]
        )
    }
}
