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
import utils.cyaHelpers.traderDetails.RepresentativeCheckYourAnswersHelper
import viewModels.sections.Section

class RepresentativeViewModel {

  def apply(userAnswers: UserAnswers)(implicit messages: Messages): Seq[Section] = {
    val helper = new RepresentativeCheckYourAnswersHelper(userAnswers, CheckMode)

    val representativeSection = Section(
      sectionTitle = messages("traderDetails.representative.checkYourAnswers.representative"),
      rows = Seq(
        helper.actingRepresentative,
        helper.representativeEori,
        helper.representativeName,
        helper.representativeCapacity,
        helper.representativePhone
      ).flatten
    )

    Seq(representativeSection)
  }
}
