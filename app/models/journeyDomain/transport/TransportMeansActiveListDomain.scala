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

import cats.implicits._
import controllers.transport.transportMeans.active.routes
import models.domain.{JsArrayGettableAsReaderOps, UserAnswersReader}
import models.journeyDomain.{JourneyDomainModel, Stage}
import models.{Index, Mode, RichJsArray, UserAnswers}
import pages.sections.transport.transportMeans.TransportMeansActiveListSection
import play.api.mvc.Call

case class TransportMeansActiveListDomain(
  transportMeansActiveListDomain: Seq[TransportMeansActiveDomain]
) extends JourneyDomainModel {

  override def routeIfCompleted(userAnswers: UserAnswers, mode: Mode, stage: Stage): Option[Call] =
    Some(routes.AddAnotherBorderTransportController.onPageLoad(userAnswers.lrn, mode))
}

object TransportMeansActiveListDomain {

  implicit val userAnswersReader: UserAnswersReader[TransportMeansActiveListDomain] = {

    val activeListReader: UserAnswersReader[Seq[TransportMeansActiveDomain]] =
      TransportMeansActiveListSection.arrayReader.flatMap {
        case x if x.isEmpty =>
          UserAnswersReader[TransportMeansActiveDomain](
            TransportMeansActiveDomain.userAnswersReader(Index(0))
          ).map(Seq(_))

        case x =>
          x.traverse[TransportMeansActiveDomain](
            TransportMeansActiveDomain.userAnswersReader
          ).map(_.toSeq)
      }

    UserAnswersReader[Seq[TransportMeansActiveDomain]](activeListReader).map(TransportMeansActiveListDomain(_))

  }
}
