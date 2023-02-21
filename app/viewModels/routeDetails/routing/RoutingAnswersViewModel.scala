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

package viewModels.routeDetails.routing

import models.{Mode, UserAnswers}
import play.api.i18n.Messages
import utils.cyaHelpers.routeDetails.routing.RoutingCheckYourAnswersHelper
import viewModels.sections.Section

import javax.inject.Inject

case class RoutingAnswersViewModel(sections: Seq[Section])

object RoutingAnswersViewModel {

  class RoutingAnswersViewModelProvider @Inject() () {

    def apply(userAnswers: UserAnswers, mode: Mode)(implicit messages: Messages): RoutingAnswersViewModel = {

      val helper = new RoutingCheckYourAnswersHelper(userAnswers, mode)

      val preQuestionsSection = Section(
        rows = Seq(
          helper.countryOfDestination,
          helper.officeOfDestination,
          helper.bindingItinerary,
          helper.addCountryOfRouting
        ).flatten
      )

      val countriesOfRoutingSection = Section(
        sectionTitle = messages("routeDetails.checkYourAnswers.routing.subheading"),
        rows = helper.countriesOfRouting,
        addAnotherLink = helper.addOrRemoveCountriesOfRouting
      )

      new RoutingAnswersViewModel(Seq(preQuestionsSection, countriesOfRoutingSection))
    }
  }
}
