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

import controllers.traderDetails.{routes => tdRoutes}
import models._
import models.domain.UserAnswersReader
import models.journeyDomain.traderDetails.TraderDetailsDomain
import navigation.Navigator
import pages._
import play.api.mvc.Call

trait TraderDetailsNavigator extends Navigator {

  override val checkRoutes: RouteMapping = {
    case page =>
      ua =>
        UserAnswersReader[TraderDetailsDomain].run(ua) match {
          case Left(_)  => routes(CheckMode).applyOrElse[Page, UserAnswers => Option[Call]](page, _ => _ => None)(ua)
          case Right(_) => Some(tdRoutes.CheckYourAnswersController.onPageLoad(ua.lrn))
        }
  }

  def routes(mode: Mode): RouteMapping

}
