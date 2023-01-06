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

package models.journeyDomain.transport

import cats.implicits.catsSyntaxTuple2Semigroupal
import models.domain.{GettableAsReaderOps, UserAnswersReader}
import models.journeyDomain.Stage.{AccessingJourney, CompletingJourney}
import models.journeyDomain.{JourneyDomainModel, Stage}
import models.transport.authorisations.AuthorisationType
import models.{Index, Mode, UserAnswers}
import pages.transport.authorisation.index.{AuthorisationReferenceNumberPage, AuthorisationTypePage}
import play.api.i18n.Messages
import play.api.mvc.Call

case class AuthorisationDomain(authorisationType: AuthorisationType, referenceNumber: String)(index: Index) extends JourneyDomainModel {

  def asString(implicit messages: Messages): String =
    AuthorisationDomain.asString(authorisationType, referenceNumber)

  override def routeIfCompleted(userAnswers: UserAnswers, mode: Mode, stage: Stage): Option[Call] = Some {
    stage match {
      case AccessingJourney =>
        controllers.transport.authorisations.index.routes.AuthorisationTypeController.onPageLoad(userAnswers.lrn, mode, index)
      case CompletingJourney =>
        controllers.transport.authorisations.routes.AddAnotherAuthorisationController.onPageLoad(userAnswers.lrn, mode)
    }
  }
}

object AuthorisationDomain {

  def asString(authorisationType: AuthorisationType, referenceNumber: String)(implicit messages: Messages): String =
    s"${authorisationType.asString} - $referenceNumber"

  def userAnswersReader(index: Index): UserAnswersReader[AuthorisationDomain] = (
    AuthorisationTypePage(index).reader,
    AuthorisationReferenceNumberPage(index).reader
  ).mapN {
    (authType, authReferenceNumber) => AuthorisationDomain(authType, authReferenceNumber)(index)
  }

  //TODO: Add Implementation when nav ticket is in sprint

}
