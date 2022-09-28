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

import models.{Index, Mode, UserAnswers}
import play.api.i18n.Messages
import utils.cyaHelpers.routeDetails.exit.OfficeOfExitCheckYourAnswersHelper
import viewModels.sections.Section

import javax.inject.Inject

case class OfficeOfExitAnswersViewModel(section: Section)

object OfficeOfExitAnswersViewModel {

  class OfficeOfExitAnswersViewModelProvider @Inject() () {

    def apply(userAnswers: UserAnswers, mode: Mode, index: Index)(implicit messages: Messages): OfficeOfExitAnswersViewModel = {
      val helper = new OfficeOfExitCheckYourAnswersHelper(userAnswers, mode, index)

      val rows = Seq(
        helper.officeOfExitCountry,
        helper.officeOfExit
      ).flatten

      new OfficeOfExitAnswersViewModel(Section(rows))
    }
  }
}
