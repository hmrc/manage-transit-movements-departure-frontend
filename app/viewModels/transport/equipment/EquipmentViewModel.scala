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

package viewModels.transport.equipment

import models.{Index, Mode, UserAnswers}
import play.api.i18n.Messages
import utils.cyaHelpers.transport.equipment.EquipmentAnswersHelper
import viewModels.sections.Section

import javax.inject.Inject

case class EquipmentViewModel(sections: Seq[Section])

object EquipmentViewModel {

  class EquipmentViewModelProvider @Inject() () {

    def apply(userAnswers: UserAnswers, mode: Mode, index: Index)(implicit messages: Messages): EquipmentViewModel = {
      val helper = new EquipmentAnswersHelper(userAnswers, mode, index)

      val preSection = Section(
        rows = Seq(
          helper.containerIdentificationNumberYesNo,
          helper.containerIdentificationNumber
        ).flatten
      )

      val sealsSection = Section(
        sectionTitle = messages("transport.equipment.index.checkYourAnswers.seals"),
        rows = Seq(
          helper.sealsYesNo
        ).flatten
      )

      val itemNumbersSection = Section(
        sectionTitle = messages("transport.equipment.index.checkYourAnswers.itemNumbers"),
        rows = Seq(
          helper.itemNumbersYesNo
        ).flatten
      )

      new EquipmentViewModel(Seq(preSection, sealsSection, itemNumbersSection))
    }
  }
}
