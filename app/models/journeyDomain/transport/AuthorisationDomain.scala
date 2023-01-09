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
import models.ProcedureType._
import models.domain.{GettableAsReaderOps, UserAnswersReader}
import models.journeyDomain.Stage.{AccessingJourney, CompletingJourney}
import models.journeyDomain.{JourneyDomainModel, Stage}
import models.transport.authorisations.AuthorisationType
import models.transport.authorisations.AuthorisationType.{ACR, TRD}
import models.transport.transportMeans.departure.InlandMode._
import models.{Index, Mode, UserAnswers}
import pages.preTaskList.ProcedureTypePage
import pages.traderDetails.consignment.ApprovedOperatorPage
import pages.transport.authorisation.index.{AuthorisationReferenceNumberPage, AuthorisationTypePage}
import pages.transport.transportMeans.departure.InlandModePage
import play.api.mvc.Call

case class AuthorisationDomain(authorisationType: AuthorisationType, referenceNumber: String)(index: Index) extends JourneyDomainModel {

  def asString: String =
    AuthorisationDomain.asString(authorisationType, referenceNumber)

  override def routeIfCompleted(userAnswers: UserAnswers, mode: Mode, stage: Stage): Option[Call] = Some {
    stage match {
      case AccessingJourney =>
        if (userAnswers.get(AuthorisationTypePage(index)).isEmpty && index.isFirst) {
          controllers.transport.authorisations.index.routes.AuthorisationReferenceNumberController.onPageLoad(userAnswers.lrn, mode, index)
        } else {
          controllers.transport.authorisations.index.routes.AuthorisationTypeController.onPageLoad(userAnswers.lrn, mode, index)
        }
      case CompletingJourney =>
        controllers.transport.authorisations.routes.AddAnotherAuthorisationController.onPageLoad(userAnswers.lrn, mode)
    }
  }
}

object AuthorisationDomain {

  def asString(authorisationType: AuthorisationType, referenceNumber: String): String =
    s"${authorisationType.toString} - $referenceNumber"

  // scalastyle:off cyclomatic.complexity
  def userAnswersReader(index: Index): UserAnswersReader[AuthorisationDomain] = {

    val authorisationTypeReads: UserAnswersReader[AuthorisationType] = {
      if (index.isFirst) {
        for {
          reducedDataSetIndicator <- ApprovedOperatorPage.reader
          inlandMode              <- InlandModePage.reader
          procedureType           <- ProcedureTypePage.reader

          reader <- (reducedDataSetIndicator, inlandMode, procedureType) match {
            case (true, Maritime | Rail | Air, _)                  => UserAnswersReader.apply(TRD)
            case (true, Road | Mail | Fixed | Unknown, Simplified) => UserAnswersReader.apply(ACR)
            case _                                                 => AuthorisationTypePage(index).reader
          }
        } yield reader
      } else {
        AuthorisationTypePage(index).reader
      }
    }

    (
      authorisationTypeReads,
      AuthorisationReferenceNumberPage(index).reader
    ).mapN {
      (authType, authReferenceNumber) => AuthorisationDomain(authType, authReferenceNumber)(index)
    }
  }

}
