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
import models.SecurityDetailsType._
import models.domain.{GettableAsFilterForNextReaderOps, GettableAsReaderOps, UserAnswersReader}
import models.journeyDomain.JourneyDomainModel
import models.journeyDomain.routeDetails.exit.ExitDomain
import models.journeyDomain.routeDetails.locationOfGoods.LocationOfGoodsDomain
import models.journeyDomain.routeDetails.routing.{CountryOfRoutingDomain, RoutingDomain}
import models.journeyDomain.routeDetails.transit.TransitDomain
import pages.preTaskList.{DeclarationTypePage, OfficeOfDeparturePage, SecurityDetailsTypePage}
import pages.routeDetails.locationOfGoods.AddLocationOfGoodsPage

case class RouteDetailsDomain(
  routing: RoutingDomain,
  transit: Option[TransitDomain],
  exit: Option[ExitDomain],
  locationOfGoods: Option[LocationOfGoodsDomain]
) extends JourneyDomainModel

object RouteDetailsDomain {

  // scalastyle:off cyclomatic.complexity
  // scalastyle:off method.length
  implicit def userAnswersReader(
    ctcCountryCodes: Seq[String],
    customsSecurityAgreementAreaCountryCodes: Seq[String]
  ): UserAnswersReader[RouteDetailsDomain] = {

    implicit val transitReads: UserAnswersReader[Option[TransitDomain]] =
      DeclarationTypePage.reader.flatMap {
        case Option4 =>
          none[TransitDomain].pure[UserAnswersReader]
        case _ =>
          implicit val reads: UserAnswersReader[TransitDomain] = TransitDomain.userAnswersReader(
            ctcCountryCodes,
            customsSecurityAgreementAreaCountryCodes
          )
          UserAnswersReader[TransitDomain].map(Some(_))
      }

    implicit val exitReads: UserAnswersReader[Option[ExitDomain]] =
      DeclarationTypePage.reader.flatMap {
        case Option4 =>
          none[ExitDomain].pure[UserAnswersReader]
        case _ =>
          SecurityDetailsTypePage.reader.flatMap {
            case NoSecurityDetails | EntrySummaryDeclarationSecurityDetails =>
              none[ExitDomain].pure[UserAnswersReader]
            case _ =>
              UserAnswersReader[Seq[CountryOfRoutingDomain]]
                .map(_.map(_.country.code.code))
                .flatMap {
                  _.filter(customsSecurityAgreementAreaCountryCodes.contains(_)) match {
                    case Nil => UserAnswersReader[ExitDomain].map(Some(_))
                    case _   => none[ExitDomain].pure[UserAnswersReader]
                  }
                }
          }
      }

    implicit val locationOfGoodsReads: UserAnswersReader[Option[LocationOfGoodsDomain]] =
      // additional declaration type is currently always normal (A) as we aren't doing pre-lodge (D) yet
      OfficeOfDeparturePage.reader.flatMap {
        case x if customsSecurityAgreementAreaCountryCodes.contains(x.countryCode) =>
          AddLocationOfGoodsPage.filterOptionalDependent(identity)(UserAnswersReader[LocationOfGoodsDomain])
        case _ => UserAnswersReader[LocationOfGoodsDomain].map(Some(_))
      }

    for {
      routing         <- UserAnswersReader[RoutingDomain]
      transit         <- UserAnswersReader[Option[TransitDomain]]
      exit            <- UserAnswersReader[Option[ExitDomain]]
      locationOfGoods <- UserAnswersReader[Option[LocationOfGoodsDomain]]
    } yield RouteDetailsDomain(
      routing,
      transit,
      exit,
      locationOfGoods
    )
  }
  // scalastyle:on cyclomatic.complexity
  // scalastyle:on method.length
}
