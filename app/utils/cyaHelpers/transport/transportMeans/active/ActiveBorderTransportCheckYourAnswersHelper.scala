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

import models.journeyDomain.transport.TransportMeansActiveDomain
import models.transport.transportMeans.active.Identification
import models.{Mode, UserAnswers}
import pages.sections.transport.TransportMeansActiveListSection
import pages.transport.transportMeans.active.IdentificationPage
import play.api.i18n.Messages
import utils.cyaHelpers.AnswersHelper
import viewModels.ListItem

class ActiveBorderTransportCheckYourAnswersHelper(userAnswers: UserAnswers, mode: Mode)(implicit messages: Messages) extends AnswersHelper(userAnswers, mode) {

  def listItems: Seq[Either[ListItem, ListItem]] =
    buildListItems(TransportMeansActiveListSection) {
      index =>
        buildListItem[TransportMeansActiveDomain, Identification](
          page = IdentificationPage(index),
          formatJourneyDomainModel = _.asString,
          formatType = x => formatEnumAsString(Identification.messageKeyPrefix)(x.toString),
          removeRoute = Some(controllers.routes.SessionExpiredController.onPageLoad()) // TODO: Add remove page
        )(TransportMeansActiveDomain.userAnswersReader(index), implicitly)
    }

}
