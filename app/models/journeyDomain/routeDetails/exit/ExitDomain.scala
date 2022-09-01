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

package models.journeyDomain.routeDetails.exit

import models.domain.{JsArrayGettableAsReaderOps, UserAnswersReader}
import models.journeyDomain.{JourneyDomainModel, Stage}
import models.{Index, RichJsArray, UserAnswers}
import pages.sections.routeDetails.exit.OfficesOfExitSection
import play.api.mvc.Call

case class ExitDomain(
  officesOfExit: Seq[OfficeOfExitDomain]
) extends JourneyDomainModel {

  override def routeIfCompleted(userAnswers: UserAnswers, stage: Stage): Option[Call] =
    Some(controllers.routeDetails.exit.routes.AddAnotherOfficeOfExitController.onPageLoad(userAnswers.lrn))
}

object ExitDomain {

  implicit val userAnswersReader: UserAnswersReader[ExitDomain] = {

    implicit val officesOfExitReader: UserAnswersReader[Seq[OfficeOfExitDomain]] =
      OfficesOfExitSection.reader.flatMap {
        case x if x.isEmpty =>
          UserAnswersReader[OfficeOfExitDomain](
            OfficeOfExitDomain.userAnswersReader(Index(0))
          ).map(Seq(_))
        case x =>
          x.traverse[OfficeOfExitDomain](
            OfficeOfExitDomain.userAnswersReader
          ).map(_.toSeq)
      }

    UserAnswersReader[Seq[OfficeOfExitDomain]].map(ExitDomain(_))
  }
}
