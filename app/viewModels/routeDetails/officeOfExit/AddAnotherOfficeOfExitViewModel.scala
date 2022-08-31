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

package viewModels.routeDetails.officeOfExit

import models.{NormalMode, UserAnswers}
import play.api.i18n.Messages
import utils.cyaHelpers.routeDetails.ExitCheckYourAnswersHelper
import viewModels.ListItem

import javax.inject.Inject

case class AddAnotherOfficeOfExitViewModel(listItems: Seq[ListItem])

object AddAnotherOfficeOfExitViewModel {

  def apply(userAnswers: UserAnswers)(implicit messages: Messages): AddAnotherOfficeOfExitViewModel =
    new AddAnotherOfficeOfExitViewModelProvider()(userAnswers)

  class AddAnotherOfficeOfExitViewModelProvider @Inject() () {

    def apply(userAnswers: UserAnswers)(implicit messages: Messages): AddAnotherOfficeOfExitViewModel = {
      val helper = new ExitCheckYourAnswersHelper(userAnswers, NormalMode)

      val listItems = helper.listItems.collect {
        case Left(value)  => value
        case Right(value) => value
      }

      new AddAnotherOfficeOfExitViewModel(listItems)
    }
  }
}
