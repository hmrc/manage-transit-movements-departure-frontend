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

import controllers.routes
import models._
import pages._
import play.api.mvc.Call

import javax.inject.{Inject, Singleton}

@Singleton
class PreTaskListNavigator @Inject() () extends Navigator {

  override val normalRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = {
    case LocalReferenceNumberPage => ua => Some(routes.OfficeOfDepartureController.onPageLoad(ua.lrn, NormalMode))
    case OfficeOfDeparturePage    => ua => Some(routes.ProcedureTypeController.onPageLoad(ua.lrn, NormalMode))
    case ProcedureTypePage        => ua => Some(routes.DeclarationTypeController.onPageLoad(ua.lrn, NormalMode))
    case DeclarationTypePage      => ua => Some(controllers.routes.AddSecurityDetailsController.onPageLoad(ua.lrn, NormalMode))
    case AddSecurityDetailsPage   => ua => Some(routes.DeclarationSummaryController.onPageLoad(ua.lrn))
  }

  override val checkRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = {
    case _ => _ => None
  }

}
