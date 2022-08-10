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
import models.reference.Country
import models.{Index, Mode, UserAnswers}
import pages.QuestionPage
import pages.sections.routeDetails.OfficeOfTransitCountrySection
import play.api.libs.json.JsPath
import play.api.mvc.Call

import scala.util.Try

case class OfficeOfTransitCountryPage(index: Index) extends QuestionPage[Country] {

  override def path: JsPath = OfficeOfTransitCountrySection(index).path \ toString

  override def toString: String = "officeOfTransitCountry"

  override def cleanup(updatedValue: Option[Country], previousValue: Option[Country], userAnswers: UserAnswers): Try[UserAnswers] =
    (previousValue, updatedValue) match {
      case (Some(x), Some(y)) if x != y => userAnswers.remove(OfficeOfTransitPage(index))
      case _                            => super.cleanup(updatedValue, previousValue, userAnswers)
    }

  override def route(userAnswers: UserAnswers, mode: Mode): Option[Call] =
    Some(routes.OfficeOfTransitCountryController.onPageLoad(userAnswers.lrn, mode, index))
}
