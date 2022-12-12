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

package viewModels.transport.transportMeans

import models.{Mode, UserAnswers}
import play.api.i18n.Messages
import utils.cyaHelpers.transport.transportMeans.TransportMeansCheckYourAnswersHelper
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

      //val borderMeansSection = ??? // TODO - an 'add or remove' section for the active transport means stuff

      new TransportMeansAnswersViewModel(Seq(inlandModeSection, departureMeansSection, borderModeSection))
    }
  }
}
