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

package pages.addItems

import models.{Index, UserAnswers}
import pages.QuestionPage
import play.api.libs.json.JsPath
import queries.Constants.{items, packages}

import scala.util.{Success, Try}

case class AddMarkPage(itemIndex: Index, packageIndex: Index) extends QuestionPage[Boolean] {

  override def path: JsPath = JsPath \ items \ itemIndex.position \ packages \ packageIndex.position \ toString

  override def toString: String = "addMark"

  override def cleanup(value: Option[Boolean], userAnswers: UserAnswers): Try[UserAnswers] =
    value match {
      case Some(false) => userAnswers.remove(DeclareMarkPage(itemIndex, packageIndex))
      case _           => Success(userAnswers)
    }
}
