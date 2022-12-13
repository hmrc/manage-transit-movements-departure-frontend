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

import controllers.transport.transportMeans.active.routes
import models.journeyDomain.transport.TransportMeansActiveDomain
import models.{Mode, UserAnswers}
import pages.sections.transport.TransportMeansActiveListSection
import pages.transport.transportMeans.AnotherVehicleCrossingYesNoPage
import pages.transport.transportMeans.active._
import play.api.i18n.Messages
import play.api.mvc.Call
import utils.cyaHelpers.AnswersHelper
import viewModels.ListItem

class ActiveBorderTransportsAnswersHelper(userAnswers: UserAnswers, mode: Mode)(implicit messages: Messages) extends AnswersHelper(userAnswers, mode) {

  def listItems: Seq[Either[ListItem, ListItem]] =
    buildListItems(TransportMeansActiveListSection) {
      index =>
        val removeRoute: Option[Call] = if (userAnswers.get(AnotherVehicleCrossingYesNoPage).isEmpty && index.isFirst) {
          None
        } else {
          Some(routes.ConfirmRemoveBorderTransportController.onPageLoad(lrn, mode, index))
        }

        buildListItem[TransportMeansActiveDomain](
          nameWhenComplete = _.asString,
          nameWhenInProgress = (userAnswers.get(IdentificationPage(index)), userAnswers.get(IdentificationNumberPage(index))) match {
            case (Some(identification), Some(identificationNumber)) =>
              Some(TransportMeansActiveDomain.asString(identification, identificationNumber))
            case (Some(identification), None)       => Some(identification.asString)
            case (None, Some(identificationNumber)) => Some(identificationNumber)
            case _                                  => None
          },
          removeRoute = removeRoute
        )(TransportMeansActiveDomain.userAnswersReader(index))
    }

}
