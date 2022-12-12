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

import cats.implicits._
import models.domain.{GettableAsReaderOps, UserAnswersReader}
import models.journeyDomain.JourneyDomainModel
import models.transport.transportMeans.departure.InlandMode
import pages.transport.transportMeans.departure.InlandModePage

case class TransportDomain(
  preRequisites: PreRequisitesDomain,
  transportMeans: TransportMeansDomain
) extends JourneyDomainModel

object TransportDomain {

  implicit val userAnswersReader: UserAnswersReader[TransportDomain] = {

    def transportMeansReads(inlandMode: InlandMode): UserAnswersReader[Option[TransportMeansDomain]] =
      inlandMode match {
        case InlandMode.Mail => none[TransportMeansDomain].pure[UserAnswersReader]
        case _               => UserAnswersReader[TransportMeansDomain].map(Some(_))
      }

    for {
      preRequisites  <- UserAnswersReader[PreRequisitesDomain]
      transportMeans <- UserAnswersReader[TransportMeansDomain]
    } yield TransportDomain(preRequisites, transportMeans)
  }
}
