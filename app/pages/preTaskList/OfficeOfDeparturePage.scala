/*
 * Copyright 2023 HM Revenue & Customs
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

package pages.preTaskList

import config.Constants.{GB, TIR}
import models.reference.CustomsOffice
import models.{DeclarationType, Mode, UserAnswers}
import pages.QuestionPage
import pages.sections.PreTaskListSection
import play.api.libs.json.JsPath
import play.api.mvc.Call

import scala.util.Try

case object OfficeOfDeparturePage extends QuestionPage[CustomsOffice] {

  override def path: JsPath = PreTaskListSection.path \ toString

  override def toString: String = "officeOfDeparture"

  override def cleanup(value: Option[CustomsOffice], userAnswers: UserAnswers): Try[UserAnswers] =
    (value.map(_.countryCode), userAnswers.get(DeclarationTypePage)) match {
      case (Some(GB), Some(DeclarationType(TIR, _))) => userAnswers.remove(DeclarationTypePage).flatMap(_.remove(TIRCarnetReferencePage))
      case _                                         => super.cleanup(value, userAnswers)
    }

  override def route(userAnswers: UserAnswers, mode: Mode): Option[Call] =
    Some(controllers.preTaskList.routes.OfficeOfDepartureController.onPageLoad(userAnswers.lrn, mode))
}

case object OfficeOfDepartureInCL112Page extends QuestionPage[Boolean] {

  override def path: JsPath = OfficeOfDeparturePage.path \ toString

  override def toString: String = "isInCL112"
}

case object OfficeOfDepartureInCL147Page extends QuestionPage[Boolean] {

  override def path: JsPath = OfficeOfDeparturePage.path \ toString

  override def toString: String = "isInCL147"
}

case object OfficeOfDepartureInCL010Page extends QuestionPage[Boolean] {

  override def path: JsPath = OfficeOfDeparturePage.path \ toString

  override def toString: String = "isInCL010"
}
