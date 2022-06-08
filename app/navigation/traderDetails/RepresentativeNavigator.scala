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
import pages.traderDetails.representative._
import play.api.mvc.Call

import javax.inject.{Inject, Singleton}

@Singleton
class RepresentativeNavigator @Inject() () extends TraderDetailsNavigator[RepresentativeDomain] {

  override def routes(mode: Mode): RouteMapping = {
    case ActingAsRepresentativePage => ua => actingRepresentativeRoute(ua, mode)
    case EoriPage                   => ua => Some(repRoutes.NameController.onPageLoad(ua.lrn, mode))
    case NamePage                   => ua => Some(repRoutes.CapacityController.onPageLoad(ua.lrn, mode))
    case CapacityPage               => ua => Some(repRoutes.TelephoneNumberController.onPageLoad(ua.lrn, mode))
    case TelephoneNumberPage        => ua => Some(checkYourAnswersRoute(ua))
  }

  override def checkYourAnswersRoute(userAnswers: UserAnswers): Call =
    repRoutes.CheckYourAnswersController.onPageLoad(userAnswers.lrn)

  private def actingRepresentativeRoute(userAnswers: UserAnswers, mode: Mode): Option[Call] =
    yesNoRoute(userAnswers, ActingAsRepresentativePage)(
      yesCall = repRoutes.EoriController.onPageLoad(userAnswers.lrn, mode)
    )(
      noCall = tdRoutes.CheckYourAnswersController.onPageLoad(userAnswers.lrn) //TODO REDIRECT TO CORRECT PAGE WHEN BUILT
    )

}
