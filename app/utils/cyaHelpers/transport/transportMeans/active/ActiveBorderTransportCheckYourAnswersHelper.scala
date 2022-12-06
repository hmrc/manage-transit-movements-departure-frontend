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

import controllers.guaranteeDetails.guarantee.{routes => guaranteeRoutes}
import models.journeyDomain.guaranteeDetails.GuaranteeDomain
import models.journeyDomain.transport.TransportMeansActiveDomain
import models.transport.transportMeans.active.Identification
import models.{GuaranteeType, Index, Mode, UserAnswers}
import pages.guaranteeDetails.guarantee.GuaranteeTypePage
import pages.sections.TransportSection
import pages.sections.guaranteeDetails.GuaranteeDetailsSection
import pages.sections.transport.TransportMeansActiveListSection
import pages.transport.transportMeans.active.{IdentificationNumberPage, IdentificationPage}
import uk.gov.hmrc.govukfrontend.views.Aliases.SummaryListRow
import play.api.i18n.Messages
import utils.cyaHelpers.AnswersHelper
import viewModels.ListItem

class ActiveBorderTransportCheckYourAnswersHelper(userAnswers: UserAnswers, mode: Mode)(implicit messages: Messages) extends AnswersHelper(userAnswers, mode) {

  def listItems: Seq[Either[ListItem, ListItem]] =
    buildListItems(TransportMeansActiveListSection) {
      index =>
        buildListItem[TransportMeansActiveDomain, String](
          page = IdentificationNumberPage(index),
          formatJourneyDomainModel = _.identificationNumber
          formatType = formatAsText,
          removeRoute = Some(guaranteeRoutes.RemoveGuaranteeYesNoController.onPageLoad(lrn, index))
        )(TransportMeansActiveDomain.userAnswersReader(index), implicitly)
    }

}
