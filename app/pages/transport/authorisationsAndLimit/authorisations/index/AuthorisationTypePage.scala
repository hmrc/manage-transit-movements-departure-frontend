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

package pages.transport.authorisationsAndLimit.authorisations.index

import controllers.transport.authorisationsAndLimit.authorisations.index.routes
import models.ProcedureType.Simplified
import models.domain.{GettableAsReaderOps, UserAnswersReader}
import models.transport.authorisations.AuthorisationType
import models.transport.authorisations.AuthorisationType.{ACR, TRD}
import models.transport.transportMeans.departure.InlandMode.{Air, Maritime, Rail}
import models.{Index, Mode, UserAnswers}
import pages.QuestionPage
import pages.preTaskList.ProcedureTypePage
import pages.sections.transport.authorisationsAndLimit.AuthorisationSection
import pages.traderDetails.consignment.ApprovedOperatorPage
import pages.transport.transportMeans.departure.InlandModePage
import play.api.libs.json.JsPath
import play.api.mvc.Call

import scala.util.Try

case class AuthorisationTypePage(authorisationIndex: Index) extends QuestionPage[AuthorisationType] {

  override def path: JsPath = AuthorisationSection(authorisationIndex).path \ toString

  override def toString: String = "authorisationType"

  override def route(userAnswers: UserAnswers, mode: Mode): Option[Call] =
    Some(routes.AuthorisationTypeController.onPageLoad(userAnswers.lrn, mode, authorisationIndex))

  // TODO - if user adds or removes a SSE type authorisation this affects the transport equipment seals and goods item numbers nav.
  override def cleanup(value: Option[AuthorisationType], userAnswers: UserAnswers): Try[UserAnswers] =
    value match {
      case Some(_) => userAnswers.remove(AuthorisationReferenceNumberPage(authorisationIndex))
      case _       => super.cleanup(value, userAnswers)
    }

  def inferredReader: UserAnswersReader[AuthorisationType] = {
    lazy val defaultReader = AuthorisationTypePage(authorisationIndex).reader

    if (authorisationIndex.isFirst) {
      for {
        procedureType           <- ProcedureTypePage.reader
        reducedDataSetIndicator <- ApprovedOperatorPage.inferredReader
        inlandMode              <- InlandModePage.reader
        reader <-
          if (reducedDataSetIndicator) {
            (inlandMode, procedureType) match {
              case (Maritime | Rail | Air, _) => UserAnswersReader.apply(TRD)
              case (_, Simplified)            => UserAnswersReader.apply(ACR)
              case _                          => defaultReader
            }
          } else {
            defaultReader
          }
      } yield reader
    } else {
      defaultReader
    }
  }
}
