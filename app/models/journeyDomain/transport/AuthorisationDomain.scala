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

package models.journeyDomain.transport

import cats.implicits.catsSyntaxTuple2Semigroupal
import controllers.transport.authorisationsAndLimit.authorisations.index.{routes => authorisationRoutes}
import controllers.transport.authorisationsAndLimit.authorisations.{routes => authorisationsRoutes}
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
import pages.transport.authorisationsAndLimit.authorisations.index.{AuthorisationReferenceNumberPage, AuthorisationTypePage}
import pages.transport.transportMeans.departure.InlandModePage
import play.api.i18n.Messages
import play.api.mvc.Call

case class AuthorisationDomain(authorisationType: AuthorisationType, referenceNumber: String)(index: Index) extends JourneyDomainModel {

  def asString()(implicit messages: Messages): String =
    AuthorisationDomain.asString(authorisationType, referenceNumber)

  override def routeIfCompleted(userAnswers: UserAnswers, mode: Mode, stage: Stage): Option[Call] = Some {
    stage match {
      case AccessingJourney =>
        // User cannot change authorisation type, they have to remove it when they want to make a change.
        authorisationRoutes.AuthorisationReferenceNumberController.onPageLoad(userAnswers.lrn, mode, index)
      case CompletingJourney =>
        authorisationsRoutes.AddAnotherAuthorisationController.onPageLoad(userAnswers.lrn, mode)
    }
  }
}

object AuthorisationDomain {

  def asString(authorisationType: AuthorisationType, referenceNumber: String)(implicit messages: Messages): String =
    s"${authorisationType.forDisplay} - $referenceNumber"

  // scalastyle:off cyclomatic.complexity
  def userAnswersReader(index: Index): UserAnswersReader[AuthorisationDomain] = {

    val authorisationTypeReads: UserAnswersReader[AuthorisationType] = {
      if (index.isFirst) {
        for {
          reducedDataSetIndicator <- ApprovedOperatorPage.reader
          inlandMode              <- InlandModePage.reader
          procedureType           <- ProcedureTypePage.reader

          reader <- (reducedDataSetIndicator, inlandMode, procedureType) match {
            case (true, Maritime | Rail | Air, _)                             => UserAnswersReader.apply(TRD)
            case (true, Road | Mail | Fixed | Unknown | Waterway, Simplified) => UserAnswersReader.apply(ACR)
            case _                                                            => AuthorisationTypePage(index).reader
          }
        } yield reader
      } else {
        AuthorisationTypePage(index).reader
      }
    }

    (
      authorisationTypeReads,
      AuthorisationReferenceNumberPage(index).reader
    ).tupled.map((AuthorisationDomain.apply _).tupled).map(_(index))
  }

}
