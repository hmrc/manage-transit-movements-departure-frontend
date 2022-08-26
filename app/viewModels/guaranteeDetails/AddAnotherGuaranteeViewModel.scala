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

package viewModels.guaranteeDetails

import models.{NormalMode, UserAnswers}
import play.api.i18n.Messages
import utils.cyaHelpers.guaranteeDetails.GuaranteeDetailsCheckYourAnswersHelper
import viewModels.ListItem

import javax.inject.Inject

case class AddAnotherGuaranteeViewModel(listItems: Seq[ListItem])

object AddAnotherGuaranteeViewModel {

  def apply(userAnswers: UserAnswers)(implicit messages: Messages): AddAnotherGuaranteeViewModel =
    new AddAnotherGuaranteeViewModelProvider()(userAnswers)

  class AddAnotherGuaranteeViewModelProvider @Inject() () {

    def apply(userAnswers: UserAnswers)(implicit messages: Messages): AddAnotherGuaranteeViewModel = {
      val helper = new GuaranteeDetailsCheckYourAnswersHelper(userAnswers, NormalMode)

      // TODO - decide what to do with in progress guarantees (Lefts). Currently lumping them together with the completed ones (Rights).
      val listItems = helper.listItems.collect {
        case Left(value)  => value
        case Right(value) => value
      }

      new AddAnotherGuaranteeViewModel(listItems)
    }
  }
}
