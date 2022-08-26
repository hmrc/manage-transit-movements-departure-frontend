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

package models.journeyDomain.routeDetails

import cats.implicits._
import models.DeclarationType.Option4
import models.domain.{GettableAsReaderOps, UserAnswersReader}
import models.journeyDomain.JourneyDomainModel
import models.journeyDomain.routeDetails.routing.RoutingDomain
import models.journeyDomain.routeDetails.transit.TransitDomain
import pages.preTaskList.DeclarationTypePage

case class RouteDetailsDomain(
  routing: RoutingDomain,
  transit: Option[TransitDomain]
) extends JourneyDomainModel

object RouteDetailsDomain {

  implicit def userAnswersReader(
    ctcCountryCodes: Seq[String],
    euCountryCodes: Seq[String],
    customsSecurityAgreementAreaCountryCodes: Seq[String]
  ): UserAnswersReader[RouteDetailsDomain] = {

    implicit val transitReads: UserAnswersReader[Option[TransitDomain]] =
      DeclarationTypePage.reader.flatMap {
        case Option4 =>
          none[TransitDomain].pure[UserAnswersReader]
        case _ =>
          implicit val reads: UserAnswersReader[TransitDomain] = TransitDomain.userAnswersReader(
            ctcCountryCodes,
            euCountryCodes,
            customsSecurityAgreementAreaCountryCodes
          )
          UserAnswersReader[TransitDomain].map(Some(_))
      }

    for {
      routing <- UserAnswersReader[RoutingDomain]
      transit <- UserAnswersReader[Option[TransitDomain]]
    } yield RouteDetailsDomain(
      routing,
      transit
    )
  }
}
