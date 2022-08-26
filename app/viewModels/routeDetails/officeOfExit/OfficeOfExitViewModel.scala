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

package viewModels.routeDetails.officeOfExit

import models.{Index, NormalMode, UserAnswers}
import play.api.i18n.Messages
import utils.cyaHelpers.routeDetails.OfficeOfExitCheckYourAnswersHelper
import viewModels.sections.Section

import javax.inject.Inject

case class OfficeOfExitViewModel(section: Section)

object OfficeOfExitViewModel {

  def apply(userAnswers: UserAnswers, index: Index)(implicit messages: Messages): OfficeOfExitViewModel =
    new OfficeOfExitViewModelProvider()(userAnswers, index)

  class OfficeOfExitViewModelProvider @Inject() () {

    def apply(userAnswers: UserAnswers, index: Index)(implicit messages: Messages): OfficeOfExitViewModel = {
      val helper = new OfficeOfExitCheckYourAnswersHelper(userAnswers, NormalMode, index)

      val rows = Seq(
        helper.officeOfExitCountry,
        helper.officeOfExit
      ).flatten

      new OfficeOfExitViewModel(Section(rows))
    }
  }
}
