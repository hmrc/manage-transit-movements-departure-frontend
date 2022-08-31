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

package pages.routeDetails.routing

import controllers.routeDetails.routing.routes
import models.reference.CustomsOffice
import models.{Mode, UserAnswers}
import pages.QuestionPage
import pages.sections.routeDetails.routing.RoutingSection
import play.api.libs.json.JsPath
import play.api.mvc.Call

case object OfficeOfDestinationPage extends QuestionPage[CustomsOffice] {

  override def path: JsPath = RoutingSection.path \ toString

  override def toString: String = "officeOfDestination"

  override def route(userAnswers: UserAnswers, mode: Mode): Option[Call] =
    Some(routes.OfficeOfDestinationController.onPageLoad(userAnswers.lrn, mode))
}
