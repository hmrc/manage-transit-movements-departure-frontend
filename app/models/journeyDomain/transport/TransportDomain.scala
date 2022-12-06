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

import models.Index
import models.domain.UserAnswersReader
import models.journeyDomain.{JourneyDomainModel, UserAnswersReader}
import pages.sections.transport.TransportMeansActiveListSection

case class TransportDomain(
  preRequisites: PreRequisitesDomain,
  transportMeans: TransportMeansDomain,
  transportMeansActiveBorder: TransportMeansActiveBorderDomain
) extends JourneyDomainModel

object TransportDomain {



  implicit val userAnswersReader: UserAnswersReader[TransportDomain] = {

    val transportMeansActiveBorderDomainReader: UserAnswersReader[Seq[TransportMeansActiveBorderDomain]] =
      TransportMeansActiveListSection.reader.flatMap {
        case x if x.isEmpty =>
          UserAnswersReader[TransportMeansActiveBorderDomain](
            TransportMeansActiveBorderDomain.userAnswersReader(Index(0))
          ).map(Seq(_))

        case x =>
          x.traverse[TransportMeansActiveBorderDomain](
            TransportMeansActiveBorderDomain.userAnswersReader
          ).map(_.toSeq)
      }

    UserAnswersReader[Seq[TransportMeansActiveBorderDomain]](transportMeansActiveBorderDomainReader).map(TransportMeansActiveBorderDomain(_))

    (
      preRequisites  <- UserAnswersReader[PreRequisitesDomain]
      transportMeans <- UserAnswersReader[TransportMeansDomain]
      transportMeansActiveBorder <- transportMeansActiveBorderDomainReader
    ).tupled.map((TransportDomain.apply _).tupled) yield
}
