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

import controllers.traderDetails.holderOfTransit.{routes => hotRoutes}
import models._
import pages._
import pages.traderDetails.holderOfTransit._
import play.api.mvc.Call

import javax.inject.{Inject, Singleton}

@Singleton
class TraderDetailsNavigator @Inject() () extends Navigator {

  override val normalRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = routes(NormalMode)

  override val checkRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = routes(CheckMode)

  private def routes(mode: Mode): PartialFunction[Page, UserAnswers => Option[Call]] = {
    case EoriYesNoPage => ua => eoriYesNoRoute(ua, mode)
  }

  private def eoriYesNoRoute(userAnswers: UserAnswers, mode: Mode): Option[Call] = Some {
    userAnswers.get(EoriYesNoPage) match {
      case Some(true)  => hotRoutes.EoriController.onPageLoad(userAnswers.lrn, mode)
      case Some(false) => ??? // TODO - redirect to HoT name once built
      case None        => controllers.routes.SessionExpiredController.onPageLoad()
    }
  }

}
