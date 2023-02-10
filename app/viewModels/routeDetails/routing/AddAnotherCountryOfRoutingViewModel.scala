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

package viewModels.routeDetails.routing

import config.FrontendAppConfig
import controllers.routeDetails.routing.routes
import models.{Mode, UserAnswers}
import play.api.i18n.Messages
import play.api.mvc.Call
import utils.cyaHelpers.routeDetails.routing.RoutingCheckYourAnswersHelper
import viewModels.{AddAnotherViewModel, ListItem}

import javax.inject.Inject

case class AddAnotherCountryOfRoutingViewModel(
  override val listItems: Seq[ListItem],
  onSubmitCall: Call
) extends AddAnotherViewModel {
  override val prefix: String = "routeDetails.routing.addAnotherCountryOfRouting"

  override def maxCount(implicit config: FrontendAppConfig): Int = config.maxCountriesOfRouting
}

object AddAnotherCountryOfRoutingViewModel {

  class AddAnotherCountryOfRoutingViewModelProvider @Inject() () {

    def apply(userAnswers: UserAnswers, mode: Mode)(implicit messages: Messages): AddAnotherCountryOfRoutingViewModel = {
      val helper = new RoutingCheckYourAnswersHelper(userAnswers, mode)

      val listItems = helper.listItems.collect {
        case Right(value) => value
      }

      new AddAnotherCountryOfRoutingViewModel(
        listItems,
        onSubmitCall = routes.AddAnotherCountryOfRoutingController.onSubmit(userAnswers.lrn, mode)
      )
    }
  }
}
