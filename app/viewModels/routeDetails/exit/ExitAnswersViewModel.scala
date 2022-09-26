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

package viewModels.routeDetails.exit

import models.{Index, Mode, RichOptionalJsArray, UserAnswers}
import pages.sections.routeDetails.exit.OfficesOfExitSection
import play.api.i18n.Messages
import utils.cyaHelpers.routeDetails.exit.ExitCheckYourAnswersHelper
import viewModels.Link
import viewModels.sections.Section

import javax.inject.Inject

case class ExitAnswersViewModel(sections: Seq[Section])

object ExitAnswersViewModel {

  class ExitAnswersViewModelProvider @Inject() () {

    def apply(userAnswers: UserAnswers, mode: Mode)(implicit messages: Messages): ExitAnswersViewModel = {

      val helper = new ExitCheckYourAnswersHelper(userAnswers, mode)

      val officesOfExitSection = Section(
        sectionTitle = messages("routeDetails.checkYourAnswers.exit.subheading"),
        rows = userAnswers
          .get(OfficesOfExitSection)
          .mapWithIndex {
            (_, index) => helper.officeOfExit(Index(index))
          },
        addAnotherLink = Link(
          id = "add-or-remove-offices-of-exit",
          text = messages("routeDetails.checkYourAnswers.exit.addOrRemove"),
          href = controllers.routeDetails.exit.routes.AddAnotherOfficeOfExitController.onPageLoad(userAnswers.lrn, mode).url
        )
      )

      new ExitAnswersViewModel(Seq(officesOfExitSection))
    }
  }
}
