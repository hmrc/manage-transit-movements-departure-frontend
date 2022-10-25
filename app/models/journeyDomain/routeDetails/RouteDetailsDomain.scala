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
import models.journeyDomain.routeDetails.exit.ExitDomain
import models.journeyDomain.routeDetails.loadingAndUnloading.LoadingAndUnloadingDomain
import models.journeyDomain.routeDetails.locationOfGoods.LocationOfGoodsDomain
import models.journeyDomain.routeDetails.routing.RoutingDomain
import models.journeyDomain.routeDetails.transit.TransitDomain
import models.journeyDomain.{JourneyDomainModel, Stage}
import models.{Mode, UserAnswers}
import pages.preTaskList.{DeclarationTypePage, OfficeOfDeparturePage, SecurityDetailsTypePage}
import pages.routeDetails.locationOfGoods.AddLocationOfGoodsPage
import pages.routeDetails.routing.CountriesOfRoutingInSecurityAgreement
import play.api.mvc.Call

case class RouteDetailsDomain(
  routing: RoutingDomain,
  transit: Option[TransitDomain],
  exit: Option[ExitDomain],
  locationOfGoods: Option[LocationOfGoodsDomain],
  loadingAndUnloading: LoadingAndUnloadingDomain
) extends JourneyDomainModel {

  override def routeIfCompleted(userAnswers: UserAnswers, mode: Mode, stage: Stage): Option[Call] =
    Some(controllers.routeDetails.routes.RouteDetailsAnswersController.onPageLoad(userAnswers.lrn))
}

object RouteDetailsDomain {

  implicit def userAnswersReader(
    ctcCountryCodes: Seq[String],
    customsSecurityAgreementAreaCountryCodes: Seq[String]
  ): UserAnswersReader[RouteDetailsDomain] =
    for {
      routing             <- UserAnswersReader[RoutingDomain]
      transit             <- UserAnswersReader[Option[TransitDomain]](transitReader(ctcCountryCodes, customsSecurityAgreementAreaCountryCodes))
      exit                <- UserAnswersReader[Option[ExitDomain]](exitReader(transit))
      locationOfGoods     <- UserAnswersReader[Option[LocationOfGoodsDomain]](locationOfGoodsReader(customsSecurityAgreementAreaCountryCodes))
      loadingAndUnloading <- UserAnswersReader[LoadingAndUnloadingDomain]
    } yield RouteDetailsDomain(
      routing,
      transit,
      exit,
      locationOfGoods,
      loadingAndUnloading
    )

  implicit def transitReader(
    ctcCountryCodes: Seq[String],
    customsSecurityAgreementAreaCountryCodes: Seq[String]
  ): UserAnswersReader[Option[TransitDomain]] =
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

  implicit def exitReader(transit: Option[TransitDomain]): UserAnswersReader[Option[ExitDomain]] =
    for {
      declarationType           <- DeclarationTypePage.reader
      securityDetails           <- SecurityDetailsTypePage.reader
      isInSecurityAgreementArea <- CountriesOfRoutingInSecurityAgreement.optionalReader
      result <- {
        (declarationType, securityDetails, isInSecurityAgreementArea, transit) match {
          case (Option4, _, _, _) =>
            none[ExitDomain].pure[UserAnswersReader]
          case (_, NoSecurityDetails | EntrySummaryDeclarationSecurityDetails, _, _) =>
            none[ExitDomain].pure[UserAnswersReader]
          case (_, _, Some(false), Some(TransitDomain(_, _ :: _))) =>
            none[ExitDomain].pure[UserAnswersReader]
          case _ =>
            UserAnswersReader[ExitDomain].map(Some(_))
        }
      }
    } yield result

  implicit def locationOfGoodsReader(customsSecurityAgreementAreaCountryCodes: Seq[String]): UserAnswersReader[Option[LocationOfGoodsDomain]] =
    // additional declaration type is currently always normal (A) as we aren't doing pre-lodge (D) yet
    OfficeOfDeparturePage.reader.flatMap {
      case x if customsSecurityAgreementAreaCountryCodes.contains(x.countryCode) =>
        AddLocationOfGoodsPage.filterOptionalDependent(identity)(UserAnswersReader[LocationOfGoodsDomain])
      case _ => UserAnswersReader[LocationOfGoodsDomain].map(Some(_))
    }
}
