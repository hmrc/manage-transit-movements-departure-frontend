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

import models.{CheckMode, Mode, NormalMode, UserAnswers}
import pages.{Page, QuestionPage}
import play.api.mvc.Call

trait Navigator {
  protected type Route        = UserAnswers => Call
  protected type RouteMapping = PartialFunction[Page, Route]

  protected def normalRoutes: RouteMapping = routes(NormalMode)

  protected def checkRoutes: RouteMapping = routes(CheckMode)

  def routes(mode: Mode): RouteMapping

  def nextPage(page: Page, mode: Mode, userAnswers: UserAnswers): Call = mode match {
    case NormalMode =>
      normalRoutes.lift(page) match {
        case None    => controllers.preTaskList.routes.LocalReferenceNumberController.onPageLoad()
        case Some(f) => f(userAnswers)
      }
    case CheckMode =>
      checkRoutes.lift(page) match {
        case None    => controllers.routes.SessionExpiredController.onPageLoad()
        case Some(f) => f(userAnswers)
      }
  }

  protected def yesNoRoute(userAnswers: UserAnswers, page: QuestionPage[Boolean])(yesCall: Call)(noCall: Call): Option[Call] =
    Some {
      userAnswers.get(page) match {
        case Some(true)  => yesCall
        case Some(false) => noCall
        case None        => controllers.routes.SessionExpiredController.onPageLoad()
      }
    }
}
