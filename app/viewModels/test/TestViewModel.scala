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

package viewModels.test

import models.{Index, UserAnswers}
import play.api.i18n.Messages
import utils.cyaHelpers.test.TestCheckYourAnswersHelper
import viewModels.sections.Section
import viewModels.{SectionViewModel, SubSectionViewModel}

sealed trait TestViewModel {
  self: SectionViewModel =>

  def apply(userAnswers: UserAnswers, fooIndex: Index, barIndex: Index)(implicit messages: Messages): Seq[Section] = {
    val helper = new TestCheckYourAnswersHelper(userAnswers, mode, fooIndex, barIndex)

    Seq(
      Section(
        rows = Seq(
          helper.test1,
          helper.test2
        ).flatten
      )
    )
  }
}

object TestViewModel {
  class TestSectionViewModel extends SectionViewModel with TestViewModel
  class TestSubSectionViewModel extends SubSectionViewModel with TestViewModel
}
