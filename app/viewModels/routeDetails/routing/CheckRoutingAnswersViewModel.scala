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

package viewModels.routeDetails.routing

import models.{Index, Mode, RichOptionalJsArray, UserAnswers}
import pages.sections.routeDetails.CountriesOfRoutingSection
import play.api.i18n.Messages
import utils.cyaHelpers.routeDetails.RoutingCheckYourAnswersHelper
import viewModels.Link
import viewModels.sections.Section

import javax.inject.Inject

case class CheckRoutingAnswersViewModel(sections: Seq[Section])

object CheckRoutingAnswersViewModel {

  def apply(userAnswers: UserAnswers, mode: Mode)(implicit messages: Messages): CheckRoutingAnswersViewModel =
    new CheckRoutingAnswersViewModelProvider().apply(userAnswers, mode)

  class CheckRoutingAnswersViewModelProvider @Inject() () {

    def apply(userAnswers: UserAnswers, mode: Mode)(implicit messages: Messages): CheckRoutingAnswersViewModel = {

      val helper = new RoutingCheckYourAnswersHelper(userAnswers, mode)

      val preQuestionsSection = Section(
        rows = Seq(
          helper.officeOfDestination,
          helper.bindingItinerary,
          helper.addCountryOfRouting
        ).flatten
      )

      val countriesOfRoutingSection = Section(
        sectionTitle = messages("routeDetails.routing.checkYourAnswers.countries.subheading"),
        rows = userAnswers
          .get(CountriesOfRoutingSection)
          .mapWithIndex {
            (_, index) => helper.countryOfRouting(Index(index))
          },
        addAnotherLink = Link(
          id = "add-or-remove",
          text = messages("routeDetails.routing.checkYourAnswers.addOrRemove"),
          href = controllers.routeDetails.routing.routes.AddAnotherCountryOfRoutingController.onPageLoad(userAnswers.lrn).url
        )
      )

      new CheckRoutingAnswersViewModel(Seq(preQuestionsSection, countriesOfRoutingSection))
    }
  }
}
