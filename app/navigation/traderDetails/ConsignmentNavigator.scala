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

package navigation.traderDetails

import models._
import models.journeyDomain.traderDetails.RepresentativeDomain
import pages.traderDetails.holderOfTransit.EoriPage
import controllers.traderDetails.holderOfTransit.{routes => hotRoutes}
import play.api.mvc.Call

import javax.inject.{Inject, Singleton}

@Singleton
class ConsignmentNavigator @Inject() () extends TraderDetailsNavigator[RepresentativeDomain] {

  override def routes(mode: Mode): RouteMapping = {
    case EoriPage => ua => Some(hotRoutes.NameController.onPageLoad(ua.lrn, mode)) //todo when new pages built add nav
  }

  override def checkYourAnswersRoute(userAnswers: UserAnswers): Call =
    hotRoutes.CheckYourAnswersController.onPageLoad(userAnswers.lrn) //todo update when new pages built
}
