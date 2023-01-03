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

package viewModels.transport.transportMeans.active

import models.{Index, Mode, UserAnswers}
import play.api.i18n.Messages
import utils.cyaHelpers.transport.transportMeans.active.ActiveBorderTransportAnswersHelper
import viewModels.sections.Section

import javax.inject.Inject

case class CheckYourAnswersViewModel(sections: Seq[Section])

object CheckYourAnswersViewModel {

  class CheckYourAnswersViewModelProvider @Inject() () {

    def apply(userAnswers: UserAnswers, mode: Mode, index: Index)(implicit messages: Messages): CheckYourAnswersViewModel = {
      val helper = new ActiveBorderTransportAnswersHelper(userAnswers, mode, index)

      val activeBorderSection = Section(
        rows = Seq(
          helper.activeBorderIdentificationType,
          helper.activeBorderIdentificationNumber,
          helper.activeBorderAddNationality,
          helper.activeBorderNationality,
          helper.customsOfficeAtBorder,
          helper.activeBorderConveyanceReferenceNumberYesNo,
          helper.conveyanceReferenceNumber
        ).flatten
      )
      new CheckYourAnswersViewModel(Seq(activeBorderSection))
    }
  }
}
