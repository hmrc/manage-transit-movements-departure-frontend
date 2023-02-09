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

import config.FrontendAppConfig
import controllers.transport.equipment.routes
import models.{Mode, UserAnswers}
import play.api.i18n.Messages
import play.api.mvc.Call
import utils.cyaHelpers.transport.equipment.EquipmentsAnswersHelper
import viewModels.ListItem

import javax.inject.Inject

case class AddAnotherEquipmentViewModel(
  listItems: Seq[ListItem],
  onSubmitCall: Call
) {

  val equipmentsCount: Int     = listItems.length
  val singularOrPlural: String = if (equipmentsCount == 1) "singular" else "plural"

  val prefix: String = "transport.equipment.addAnotherEquipment"

  def title(implicit messages: Messages): String         = messages(s"$prefix.$singularOrPlural.title", equipmentsCount)
  def heading(implicit messages: Messages): String       = messages(s"$prefix.$singularOrPlural.heading", equipmentsCount)
  def legend(implicit messages: Messages): String        = messages(s"$prefix.label")
  def maxLimitLabel(implicit messages: Messages): String = messages(s"$prefix.maxLimit.label")

  def allowMoreEquipments(implicit config: FrontendAppConfig): Boolean =
    equipmentsCount < config.maxEquipmentNumbers
}

object AddAnotherEquipmentViewModel {

  class AddAnotherEquipmentViewModelProvider @Inject() () {

    def apply(userAnswers: UserAnswers, mode: Mode)(implicit messages: Messages): AddAnotherEquipmentViewModel = {
      val helper = new EquipmentsAnswersHelper(userAnswers, mode)

      val listItems = helper.listItems.collect {
        case Left(value)  => value
        case Right(value) => value
      }

      new AddAnotherEquipmentViewModel(
        listItems,
        onSubmitCall = routes.AddAnotherEquipmentController.onSubmit(userAnswers.lrn, mode)
      )
    }
  }
}
