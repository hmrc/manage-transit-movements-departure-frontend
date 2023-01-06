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

package viewModels.routeDetails

import models.{CheckMode, UserAnswers}
import play.api.i18n.Messages
import viewModels.routeDetails.exit.ExitAnswersViewModel.ExitAnswersViewModelProvider
import viewModels.routeDetails.loadingAndUnloading.LoadingAndUnloadingAnswersViewModel.LoadingAndUnloadingAnswersViewModelProvider
import viewModels.routeDetails.locationOfGoods.LocationOfGoodsAnswersViewModel.LocationOfGoodsAnswersViewModelProvider
import viewModels.routeDetails.routing.RoutingAnswersViewModel.RoutingAnswersViewModelProvider
import viewModels.routeDetails.transit.TransitAnswersViewModel.TransitAnswersViewModelProvider
import viewModels.sections.Section

import javax.inject.Inject

case class RouteDetailsAnswersViewModel(sections: Seq[Section])

object RouteDetailsAnswersViewModel {

  class RouteDetailsAnswersViewModelProvider @Inject() (
    routingAnswersViewModelProvider: RoutingAnswersViewModelProvider,
    transitAnswersViewModelProvider: TransitAnswersViewModelProvider,
    exitAnswersViewModelProvider: ExitAnswersViewModelProvider,
    locationOfGoodsAnswersViewModelProvider: LocationOfGoodsAnswersViewModelProvider,
    loadingAndUnloadingAnswersViewModelProvider: LoadingAndUnloadingAnswersViewModelProvider
  ) {

    def apply(userAnswers: UserAnswers)(
      ctcCountryCodes: Seq[String],
      customsSecurityAgreementAreaCountryCodes: Seq[String]
    )(implicit messages: Messages): RouteDetailsAnswersViewModel = {
      val mode = CheckMode
      new RouteDetailsAnswersViewModel(
        routingAnswersViewModelProvider.apply(userAnswers, mode).sections ++
          transitAnswersViewModelProvider.apply(userAnswers, mode)(ctcCountryCodes, customsSecurityAgreementAreaCountryCodes).sections ++
          exitAnswersViewModelProvider.apply(userAnswers, mode).sections ++
          locationOfGoodsAnswersViewModelProvider.apply(userAnswers, mode).section.toSeq ++
          loadingAndUnloadingAnswersViewModelProvider.apply(userAnswers, mode).sections
      )
    }

  }
}
