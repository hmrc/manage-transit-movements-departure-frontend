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

import controllers.preTaskList.routes._
import models._
import pages.preTaskList._
import play.api.mvc.Call

import javax.inject.{Inject, Singleton}

@Singleton
class PreTaskListNavigator @Inject() () extends Navigator {

  override val normalRoutes: RouteMapping = routes(NormalMode)

  override val checkRoutes: RouteMapping = routes(CheckMode)

  override def routes(mode: Mode): RouteMapping = {
    case LocalReferenceNumberPage => ua => Some(OfficeOfDepartureController.onPageLoad(ua.lrn, mode))
    case OfficeOfDeparturePage    => ua => Some(ProcedureTypeController.onPageLoad(ua.lrn, mode))
    case ProcedureTypePage        => ua => Some(DeclarationTypeController.onPageLoad(ua.lrn, mode))
    case DeclarationTypePage      => ua => declarationTypeRoute(ua, mode)
    case TIRCarnetReferencePage   => ua => Some(SecurityDetailsTypeController.onPageLoad(ua.lrn, mode))
    case SecurityDetailsTypePage  => ua => Some(CheckYourAnswersController.onPageLoad(ua.lrn))
  }

  private def declarationTypeRoute(ua: UserAnswers, mode: Mode): Option[Call] =
    (ua.get(ProcedureTypePage), ua.get(DeclarationTypePage)) match {
      case (Some(ProcedureType.Normal), Some(DeclarationType.Option4)) =>
        Some(TIRCarnetReferenceController.onPageLoad(ua.lrn, mode))
      case _ =>
        Some(SecurityDetailsTypeController.onPageLoad(ua.lrn, mode))
    }

}
