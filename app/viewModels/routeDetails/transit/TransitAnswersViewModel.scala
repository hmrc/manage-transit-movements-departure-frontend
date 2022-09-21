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

package viewModels.routeDetails.transit

import models.{Index, Mode, RichOptionalJsArray, UserAnswers}
import pages.sections.routeDetails.transit.OfficesOfTransitSection
import play.api.i18n.Messages
import utils.cyaHelpers.routeDetails.TransitCheckYourAnswersHelper
import viewModels.sections.Section
import viewModels.{Link, RichSummaryListRowOption}

import javax.inject.Inject

case class TransitAnswersViewModel(sections: Seq[Section])

object TransitAnswersViewModel {

  class TransitAnswersViewModelProvider @Inject() () {

    def apply(userAnswers: UserAnswers, mode: Mode)(
      ctcCountryCodes: Seq[String],
      customsSecurityAgreementAreaCountryCodes: Seq[String]
    )(implicit messages: Messages): TransitAnswersViewModel = {

      val helper = new TransitCheckYourAnswersHelper(userAnswers, mode)(ctcCountryCodes, customsSecurityAgreementAreaCountryCodes)

      val officesOfTransitSection = Section(
        sectionTitle = messages("routeDetails.transit.checkYourAnswers.offices.subheading"),
        rows = userAnswers
          .get(OfficesOfTransitSection)
          .mapWithIndex {
            (_, index) => helper.officeOfTransit(Index(index))
          },
        addAnotherLink = Link(
          id = "add-or-remove-offices-of-transit",
          text = messages("routeDetails.transit.checkYourAnswers.addOrRemove"),
          href = controllers.routeDetails.transit.routes.AddAnotherOfficeOfTransitController.onPageLoad(userAnswers.lrn, mode).url
        )
      )

      new TransitAnswersViewModel(Seq(helper.addOfficeOfTransit.toSection, officesOfTransitSection))
    }
  }
}
