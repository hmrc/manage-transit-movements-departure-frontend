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

package navigation

import models._
import pages._
import pages.traderDetails.representative.{ActingRepresentativePage, RepresentativeEoriPage}
import play.api.mvc.Call
import controllers.traderDetails.representative.{routes => repRoutes}

import javax.inject.{Inject, Singleton}

@Singleton
class RepresentativeNavigator @Inject() () extends Navigator {

  override val normalRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = routes(NormalMode)

  private def routes(mode: Mode): PartialFunction[Page, UserAnswers => Option[Call]] = {
    case ActingRepresentativePage => ua => actingRepresentativeRoute(ua, mode)
    case RepresentativeEoriPage   => ua => Some(repRoutes.RepresentativeNameController.onPageLoad(ua.lrn, mode))
  }

  override protected def checkRoutes: RouteMapping = ???

  private def actingRepresentativeRoute(userAnswers: UserAnswers, mode: Mode): Option[Call] =
    yesNoRoute(userAnswers, ActingRepresentativePage)(
      yesCall = controllers.traderDetails.representative.routes.RepresentativeEoriController.onPageLoad(userAnswers.lrn, mode)
    )(
      noCall = //TODO REDIRECT TO CORRECT PAGE WHEN BUILT
        controllers.routes.SessionExpiredController.onPageLoad()
    )

}
