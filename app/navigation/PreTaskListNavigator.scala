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
import pages._
import pages.preTaskList._
import play.api.mvc.Call

import javax.inject.{Inject, Singleton}

@Singleton
class PreTaskListNavigator @Inject() () extends Navigator {

  override val normalRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = {
    case LocalReferenceNumberPage => ua => Some(OfficeOfDepartureController.onPageLoad(ua.lrn, NormalMode))
    case OfficeOfDeparturePage    => ua => Some(ProcedureTypeController.onPageLoad(ua.lrn, NormalMode))
    case ProcedureTypePage        => ua => Some(DeclarationTypeController.onPageLoad(ua.lrn, NormalMode))
    case DeclarationTypePage      => ua => declarationTypeRoute(ua)
    case TIRCarnetReferencePage   => ua => Some(SecurityDetailsTypeController.onPageLoad(ua.lrn, NormalMode))
    case SecurityDetailsTypePage  => ua => Some(CheckYourAnswersController.onPageLoad(ua.lrn))
  }

  override val checkRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = {
    case _ => ua => Some(CheckYourAnswersController.onPageLoad(ua.lrn))
  }

  private def declarationTypeRoute(ua: UserAnswers): Option[Call] =
    (ua.get(ProcedureTypePage), ua.get(DeclarationTypePage)) match {
      case (Some(ProcedureType.Normal), Some(DeclarationType.Option4)) =>
        Some(TIRCarnetReferenceController.onPageLoad(ua.lrn, NormalMode))
      case _ =>
        Some(SecurityDetailsTypeController.onPageLoad(ua.lrn, NormalMode))
    }

}
