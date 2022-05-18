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

package viewModels

import models.{CheckMode, UserAnswers}
import play.api.i18n.Messages
import utils.PreTaskListCheckYourAnswersHelper
import viewModels.sections.Section

class PreTaskListViewModel {

  def apply(userAnswers: UserAnswers)(implicit messages: Messages): Section = {
    val helper = new PreTaskListCheckYourAnswersHelper(userAnswers, CheckMode)

    val rows = Seq(
      Some(helper.localReferenceNumber),
      helper.officeOfDeparture,
      helper.procedureType,
      helper.declarationType,
      helper.tirCarnet,
      helper.securityType
    ).flatten

    Section(rows)
  }
}
