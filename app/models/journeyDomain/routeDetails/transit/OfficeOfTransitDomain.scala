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

package models.journeyDomain.routeDetails.transit

import cats.implicits._
import config.Constants._
import models.SecurityDetailsType._
import models.domain.{GettableAsFilterForNextReaderOps, GettableAsReaderOps, UserAnswersReader}
import models.journeyDomain.{JourneyDomainModel, Stage}
import models.reference.{Country, CountryCode, CustomsOffice}
import models.{DateTime, Index, UserAnswers}
import pages.preTaskList.{OfficeOfDeparturePage, SecurityDetailsTypePage}
import pages.routeDetails.routing.OfficeOfDestinationPage
import pages.routeDetails.transit.index._
import play.api.mvc.Call

case class OfficeOfTransitDomain(
  country: Option[Country],
  customsOffice: CustomsOffice,
  officeOfTransitETA: Option[DateTime]
)(index: Index)
    extends JourneyDomainModel {

  override def routeIfCompleted(userAnswers: UserAnswers, stage: Stage): Option[Call] =
    Some(
      controllers.routeDetails.transit.index.routes.CheckOfficeOfTransitAnswersController.onPageLoad(userAnswers.lrn, index)
    )

}

object OfficeOfTransitDomain {

  // scalastyle:off cyclomatic.complexity
  // scalastyle:off method.length
  implicit def userAnswersReader(
    index: Index,
    ctcCountryCodes: Seq[CountryCode],
    euCountryCodes: Seq[CountryCode],
    customsSecurityAgreementAreaCountryCodes: Seq[CountryCode]
  ): UserAnswersReader[OfficeOfTransitDomain] = {

    lazy val etaReads: UserAnswersReader[Option[DateTime]] =
      SecurityDetailsTypePage.reader.flatMap {
        case EntrySummaryDeclarationSecurityDetails | EntryAndExitSummaryDeclarationSecurityDetails =>
          OfficeOfTransitPage(index).reader.flatMap {
            case x if customsSecurityAgreementAreaCountryCodes.contains(x.countryId) =>
              OfficeOfTransitETAPage(index).reader.map(Some(_))
            case _ =>
              AddOfficeOfTransitETAYesNoPage(index).filterOptionalDependent(identity)(OfficeOfTransitETAPage(index).reader)
          }
        case _ =>
          AddOfficeOfTransitETAYesNoPage(index).filterOptionalDependent(identity)(OfficeOfTransitETAPage(index).reader)
      }

    lazy val readsWithoutCountry: UserAnswersReader[OfficeOfTransitDomain] =
      (
        OfficeOfTransitPage(index).reader,
        etaReads
      ).mapN {
        (office, eta) => OfficeOfTransitDomain(None, office, eta)(index)
      }

    lazy val readsWithCountry: UserAnswersReader[OfficeOfTransitDomain] =
      (
        OfficeOfTransitCountryPage(index).reader,
        OfficeOfTransitPage(index).reader,
        etaReads
      ).mapN {
        (country, office, eta) => OfficeOfTransitDomain(Some(country), office, eta)(index)
      }

    index.position match {
      case 0 =>
        OfficeOfDestinationPage.reader.flatMap {
          case x if ctcCountryCodes.contains(x.countryId) => readsWithoutCountry
          case _ =>
            OfficeOfDeparturePage.reader.map(_.countryId.code).flatMap {
              case GB =>
                OfficeOfDestinationPage.reader.flatMap {
                  case x if euCountryCodes.contains(x.countryId) => readsWithoutCountry
                  case x if x.countryId.code == AD               => readsWithoutCountry
                  case _                                         => readsWithCountry
                }
              case _ =>
                OfficeOfDestinationPage.reader.map(_.countryId.code).flatMap {
                  case AD => readsWithoutCountry
                  case _  => readsWithCountry
                }
            }
        }
      case _ => readsWithCountry
    }
  }
  // scalastyle:on cyclomatic.complexity
  // scalastyle:on method.length
}
