/*
 * Copyright 2024 HM Revenue & Customs
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

package viewModels.preTaskList

import models.{CheckMode, UserAnswers}
import play.api.i18n.Messages
import utils.cyaHelpers.PreTaskListCheckYourAnswersHelper
import viewModels.sections.Section

import javax.inject.Inject

case class PreTaskListViewModel(section: Section)

object PreTaskListViewModel {

  class PreTaskListViewModelProvider @Inject() () {

    def apply(userAnswers: UserAnswers)(implicit messages: Messages): PreTaskListViewModel = {
      val helper = new PreTaskListCheckYourAnswersHelper(userAnswers, CheckMode)

      val rows = Seq(
        Some(helper.localReferenceNumber),
        helper.additionalDeclarationType,
        helper.officeOfDeparture,
        helper.procedureType,
        helper.declarationType,
        helper.tirCarnet,
        helper.securityType
      ).flatten

      new PreTaskListViewModel(Section(rows))
    }
  }
}
