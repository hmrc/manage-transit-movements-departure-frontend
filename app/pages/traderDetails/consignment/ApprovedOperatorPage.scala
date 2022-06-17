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

package pages.traderDetails.consignment

import models.UserAnswers
import play.api.libs.json.JsPath
import pages.QuestionPage
import pages.sections.TraderDetailsConsignmentSection
import pages.traderDetails.consignment.consignor._

import scala.util.Try

case object ApprovedOperatorPage extends QuestionPage[Boolean] {

  override def path: JsPath = TraderDetailsConsignmentSection.path \ toString

  override def toString: String = "approvedOperator"

  override def cleanup(value: Option[Boolean], userAnswers: UserAnswers): Try[UserAnswers] =
    value match {
      case Some(false) =>
        for {
          noEoriYesNo <- userAnswers.remove(EoriYesNoPage)
          noEori      <- noEoriYesNo.remove(EoriPage)
          noName      <- noEori.remove(NamePage)
          noAddress   <- noName.remove(AddressPage)
        } yield noAddress

      case _ => super.cleanup(value, userAnswers)
    }
}
