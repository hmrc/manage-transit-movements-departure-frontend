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

import controllers.traderDetails.representative.{routes => repRoutes}
import controllers.traderDetails.{routes => tdRoutes}
import models._
import models.journeyDomain.traderDetails.RepresentativeDomain
import navigation.UserAnswersNavigator
import pages.traderDetails.representative._
import play.api.mvc.Call

import javax.inject.{Inject, Singleton}

@Singleton
class RepresentativeNavigator @Inject() () extends UserAnswersNavigator[RepresentativeDomain] {

  override def routes(mode: Mode): RouteMapping = {
    case ActingAsRepresentativePage => ua => repRoutes.ActingAsRepresentativeController.onPageLoad(ua.lrn, mode)
    case EoriPage                   => ua => repRoutes.EoriController.onPageLoad(ua.lrn, mode)
    case NamePage                   => ua => repRoutes.NameController.onPageLoad(ua.lrn, mode)
    case CapacityPage               => ua => repRoutes.CapacityController.onPageLoad(ua.lrn, mode)
    case TelephoneNumberPage        => ua => repRoutes.TelephoneNumberController.onPageLoad(ua.lrn, mode)
  }

  override def checkYourAnswersRoute(mode: Mode, userAnswers: UserAnswers): Call =
    mode match {
      case NormalMode => repRoutes.CheckYourAnswersController.onPageLoad(userAnswers.lrn)
      case CheckMode  => tdRoutes.CheckYourAnswersController.onPageLoad(userAnswers.lrn)
    }

}
