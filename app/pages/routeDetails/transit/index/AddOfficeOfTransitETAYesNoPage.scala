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

package pages.routeDetails.transit.index

import controllers.routeDetails.transit.index.routes
import models.{Index, Mode, UserAnswers}
import pages.QuestionPage
import pages.sections.routeDetails.transit.OfficeOfTransitSection
import play.api.libs.json.JsPath
import play.api.mvc.Call

import scala.util.Try

case class AddOfficeOfTransitETAYesNoPage(index: Index) extends QuestionPage[Boolean] {

  override def path: JsPath = OfficeOfTransitSection(index).path \ toString

  override def toString: String = "addOfficeOfTransitETAYesNo"

  override def cleanup(value: Option[Boolean], userAnswers: UserAnswers): Try[UserAnswers] =
    value match {
      case Some(false) => userAnswers.remove(OfficeOfTransitETAPage(index))
      case _           => super.cleanup(value, userAnswers)
    }

  override def route(userAnswers: UserAnswers, mode: Mode): Option[Call] =
    Some(routes.AddOfficeOfTransitETAYesNoController.onPageLoad(userAnswers.lrn, mode, index))
}
