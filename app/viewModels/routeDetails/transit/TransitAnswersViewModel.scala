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

package viewModels.routeDetails.transit

import models.{Mode, UserAnswers}
import play.api.i18n.Messages
import utils.cyaHelpers.routeDetails.transit.TransitCheckYourAnswersHelper
import viewModels.sections.Section

import javax.inject.Inject

case class TransitAnswersViewModel(sections: Seq[Section])

object TransitAnswersViewModel {

  class TransitAnswersViewModelProvider @Inject() () {

    def apply(userAnswers: UserAnswers, mode: Mode)(
      ctcCountryCodes: Seq[String],
      customsSecurityAgreementAreaCountryCodes: Seq[String]
    )(implicit messages: Messages): TransitAnswersViewModel = {

      val helper = new TransitCheckYourAnswersHelper(userAnswers, mode)(ctcCountryCodes, customsSecurityAgreementAreaCountryCodes)

      val preSection = Section(
        rows = Seq(
          helper.includesT2Declarations,
          helper.addOfficeOfTransit
        ).flatten
      )

      val officesOfTransitSection = Section(
        sectionTitle = messages("routeDetails.checkYourAnswers.transit.subheading"),
        rows = helper.officesOfTransit,
        addAnotherLink = helper.addOrRemoveOfficesOfTransit
      )

      new TransitAnswersViewModel(Seq(preSection, officesOfTransitSection))
    }
  }
}
