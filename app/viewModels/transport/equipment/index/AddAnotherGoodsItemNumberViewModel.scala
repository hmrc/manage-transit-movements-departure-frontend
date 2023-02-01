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

package viewModels.transport.equipment.index

import config.FrontendAppConfig
import controllers.transport.equipment.index.routes
import models.{Index, Mode, UserAnswers}
import play.api.i18n.Messages
import play.api.mvc.Call
import utils.cyaHelpers.transport.equipment.GoodsItemNumbersAnswersHelper
import viewModels.ListItem

import javax.inject.Inject

case class AddAnotherGoodsItemNumberViewModel(
  listItems: Seq[ListItem],
  onSubmitCall: Call
) {

  val goodsItemNumbersCount: Int = listItems.length
  val singularOrPlural: String   = if (goodsItemNumbersCount == 1) "singular" else "plural"

  val prefix: String = "transport.equipment.index.addAnotherGoodsItemNumber"

  def title(implicit messages: Messages): String         = messages(s"$prefix.$singularOrPlural.title", goodsItemNumbersCount)
  def heading(implicit messages: Messages): String       = messages(s"$prefix.$singularOrPlural.heading", goodsItemNumbersCount)
  def legend(implicit messages: Messages): String        = messages(s"$prefix.label")
  def maxLimitLabel(implicit messages: Messages): String = messages(s"$prefix.maxLimit.label")

  def allowMoreGoodsItemNumbers(implicit config: FrontendAppConfig): Boolean =
    goodsItemNumbersCount < config.maxGoodsItemNumbers
}

object AddAnotherGoodsItemNumberViewModel {

  class AddAnotherGoodsItemNumberViewModelProvider @Inject() () {

    def apply(userAnswers: UserAnswers, mode: Mode, equipmentIndex: Index)(implicit messages: Messages): AddAnotherGoodsItemNumberViewModel = {
      val helper = new GoodsItemNumbersAnswersHelper(userAnswers, mode, equipmentIndex)

      val listItems = helper.listItems.collect {
        case Left(value)  => value
        case Right(value) => value
      }

      new AddAnotherGoodsItemNumberViewModel(
        listItems,
        onSubmitCall = routes.AddAnotherGoodsItemNumberController.onSubmit(userAnswers.lrn, mode, equipmentIndex)
      )
    }
  }
}