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

package models.journeyDomain

import controllers.preTaskList.routes

import cats.implicits._
import models.domain.{GettableAsReaderOps, UserAnswersReader}
import models.{Address, Mode, UserAnswers}
import pages.QuestionPage
import play.api.libs.json.JsPath
import play.api.mvc.Call

import java.time.LocalDate

case class PersonDomain(
  name: String,
  dateOfBirth: LocalDate,
  address: Address
) extends JourneyDomainModel {

  override def routeIfCompleted(userAnswers: UserAnswers, mode: Mode, stage: Stage): Option[Call] =
    Some(routes.CheckYourAnswersController.onPageLoad(userAnswers.lrn))
}

object PersonDomain {

  implicit val userAnswersReader: UserAnswersReader[PersonDomain] =
    (
      NamePage.reader,
      DateOfBirthPage.reader,
      AddressPage.reader
    ).tupled.map((PersonDomain.apply _).tupled)
}

case object NamePage extends QuestionPage[String] {
  override def path: JsPath = ???
}

case object DateOfBirthPage extends QuestionPage[LocalDate] {
  override def path: JsPath = ???
}

case object AddressPage extends QuestionPage[Address] {
  override def path: JsPath = ???
}
