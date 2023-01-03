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

package viewModels.transport.supplyChainActors

import config.FrontendAppConfig
import models.{Mode, UserAnswers}
import play.api.i18n.Messages
import play.api.mvc.Call
import utils.cyaHelpers.transport.supplyChainActors.SupplyChainActorsAnswersHelper
import viewModels.ListItem

import javax.inject.Inject

case class AddAnotherSupplyChainActorViewModel(
  listItems: Seq[ListItem],
  onSubmitCall: Call
) {

  val supplyChainActors: Int   = listItems.length
  val singularOrPlural: String = if (supplyChainActors == 1) "singular" else "plural"

  val prefix: String = "transport.supplyChainActors.addAnotherSupplyChainActor"

  def title(implicit messages: Messages): String         = messages(s"$prefix.$singularOrPlural.title", supplyChainActors)
  def heading(implicit messages: Messages): String       = messages(s"$prefix.$singularOrPlural.heading", supplyChainActors)
  def legend(implicit messages: Messages): String        = messages(s"$prefix.label")
  def maxLimitLabel(implicit messages: Messages): String = messages(s"$prefix.maxLimit.label")

  def allowMoreSupplyChainActors(implicit config: FrontendAppConfig): Boolean =
    supplyChainActors < config.maxSupplyChainActors
}

object AddAnotherSupplyChainActorViewModel {

  class AddAnotherSupplyChainActorViewModelProvider @Inject() () {

    def apply(userAnswers: UserAnswers, mode: Mode)(implicit messages: Messages): AddAnotherSupplyChainActorViewModel = {
      val helper = new SupplyChainActorsAnswersHelper(userAnswers, mode)

      val listItems = helper.listItems.collect {
        case Left(value)  => value
        case Right(value) => value
      }

      new AddAnotherSupplyChainActorViewModel(
        listItems,
        onSubmitCall = controllers.transport.supplyChainActors.routes.AddAnotherSupplyChainActorController.onSubmit(userAnswers.lrn, mode)
      )
    }
  }
}
