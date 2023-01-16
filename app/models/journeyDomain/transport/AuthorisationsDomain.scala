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

import models.domain.{GettableAsFilterForNextReaderOps, GettableAsReaderOps, JsArrayGettableAsReaderOps, UserAnswersReader}
import models.journeyDomain.{JourneyDomainModel, Stage}
import models.{Index, Mode, RichJsArray, UserAnswers}
import pages.sections.transport.authorisationsAndLimit.AuthorisationsSection
import pages.traderDetails.consignment.ApprovedOperatorPage
import pages.transport.authorisationsAndLimit.authorisations.AddAuthorisationsYesNoPage
import play.api.mvc.Call

case class AuthorisationsDomain(authorisationsDomain: Option[Seq[AuthorisationDomain]]) extends JourneyDomainModel {

  override def routeIfCompleted(userAnswers: UserAnswers, mode: Mode, stage: Stage): Option[Call] =
    Some(controllers.transport.authorisationsAndLimit.authorisations.routes.AddAnotherAuthorisationController.onPageLoad(userAnswers.lrn, mode))
}

object AuthorisationsDomain {

  implicit val userAnswersReader: UserAnswersReader[AuthorisationsDomain] = {

    val authReader: UserAnswersReader[Seq[AuthorisationDomain]] =
      AuthorisationsSection.arrayReader.flatMap {
        case x if x.isEmpty =>
          UserAnswersReader[AuthorisationDomain](
            AuthorisationDomain.userAnswersReader(Index(0))
          ).map(Seq(_))

        case x =>
          x.traverse[AuthorisationDomain](
            AuthorisationDomain.userAnswersReader
          ).map(_.toSeq)
      }


    ApprovedOperatorPage.reader.flatMap {
      case true => UserAnswersReader[Seq[AuthorisationDomain]](authReader).map(x => AuthorisationsDomain(Some(x)))
      case false => AddAuthorisationsYesNoPage.filterOptionalDependent(identity)(
        UserAnswersReader[Seq[AuthorisationDomain]](authReader).map(x => AuthorisationsDomain(Some(x)))
      )
    }

  }

}
