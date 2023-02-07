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

package viewModels.transport.transportMeans

import controllers.transport.transportMeans.active.routes
import models.{Index, Mode, RichOptionalJsArray, UserAnswers}
import pages.sections.routeDetails.transit.OfficesOfTransitSection
import pages.sections.transport.transportMeans.TransportMeansActiveListSection
import play.api.i18n.Messages
import utils.cyaHelpers.transport.transportMeans.TransportMeansCheckYourAnswersHelper
import utils.cyaHelpers.transport.transportMeans.active.ActiveBorderTransportAnswersHelper
import viewModels.Link
import viewModels.sections.Section

import javax.inject.Inject

case class TransportMeansAnswersViewModel(sections: Seq[Section])

object TransportMeansAnswersViewModel {

  class TransportMeansAnswersViewModelProvider @Inject() () {

    def apply(userAnswers: UserAnswers, mode: Mode)(implicit messages: Messages): TransportMeansAnswersViewModel = {
      val helper = new TransportMeansCheckYourAnswersHelper(userAnswers, mode)

      val inlandModeSection = Section(
        sectionTitle = messages("transport.transportMeans.inlandMode.subheading"),
        rows = Seq(helper.inlandMode).flatten
      )

      val departureMeansSection = Section(
        sectionTitle = messages("transport.transportMeans.departureMeans.subheading"),
        rows = Seq(
          helper.departureIdentificationType,
          helper.departureIdentificationNumber,
          helper.departureNationality
        ).flatten
      )

      val borderModeSection = Section(
        sectionTitle = messages("transport.transportMeans.borderMode.subheading"),
        rows = Seq(helper.modeCrossingBorder, helper.anotherVehicleCrossing).flatten
      )

      val borderMeansSection = {
        if (userAnswers.get(OfficesOfTransitSection).isDefined) {
          val rows = userAnswers
            .get(TransportMeansActiveListSection)
            .mapWithIndex {
              (_, index) => helper.activeBorderTransportMeans(index)
            }

          Section(
            sectionTitle = messages("transport.transportMeans.borderMeans.subheading"),
            rows = rows,
            addAnotherLink = Link(
              id = "add-or-remove-border-means-of-transport",
              text = messages("transport.transportMeans.borderMeans.addOrRemove"),
              href = routes.AddAnotherBorderTransportController.onPageLoad(userAnswers.lrn, mode).url
            )
          )
        } else {
          Section(
            sectionTitle = messages("transport.transportMeans.borderMeans.subheading"),
            rows = ActiveBorderTransportAnswersHelper.apply(userAnswers, mode, Index(0))
          )
        }
      }

      new TransportMeansAnswersViewModel(Seq(inlandModeSection, departureMeansSection, borderModeSection, borderMeansSection))
    }
  }
}
