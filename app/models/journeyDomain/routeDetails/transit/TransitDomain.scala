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

import models.domain.{GettableAsFilterForNextReaderOps, GettableAsReaderOps, JsArrayGettableAsReaderOps, UserAnswersReader}
import models.journeyDomain.routeDetails.routing.CountryOfRoutingDomain
import models.journeyDomain.routeDetails.transit.TransitDomain.OfficesOfTransit
import models.journeyDomain.{JourneyDomainModel, Stage}
import models.reference.CountryCode
import models.{DeclarationType, Index, RichJsArray, UserAnswers}
import pages.preTaskList.{DeclarationTypePage, OfficeOfDeparturePage}
import pages.routeDetails.routing.OfficeOfDestinationPage
import pages.routeDetails.transit.index.OfficeOfTransitCountryPage
import pages.routeDetails.transit.{AddOfficeOfTransitYesNoPage, T2DeclarationTypeYesNoPage}
import pages.sections.routeDetails.OfficeOfTransitCountriesSection
import play.api.mvc.Call

case class TransitDomain(
  isT2DeclarationType: Option[Boolean],
  officesOfTransit: OfficesOfTransit
) extends JourneyDomainModel {

  override def routeIfCompleted(userAnswers: UserAnswers, stage: Stage): Option[Call] =
    Some(controllers.routes.TaskListController.onPageLoad(userAnswers.lrn))
}

object TransitDomain {

  type OfficesOfTransit = Seq[OfficeOfTransitDomain]

  // scalastyle:off cyclomatic.complexity
  // scalastyle:off method.length
  implicit def userAnswersReader(
    ctcCountryCodes: Seq[CountryCode],
    euCountryCodes: Seq[CountryCode],
    customsSecurityAgreementAreaCountryCodes: Seq[CountryCode]
  ): UserAnswersReader[TransitDomain] = {

    implicit val officeOfTransitCountriesReader: UserAnswersReader[OfficesOfTransit] =
      OfficeOfTransitCountriesSection.reader.flatMap {
        case x if x.isEmpty =>
          UserAnswersReader.fail[Seq[OfficeOfTransitDomain]](OfficeOfTransitCountryPage(Index(0)))
        case x =>
          x.traverse[OfficeOfTransitDomain](
            OfficeOfTransitDomain.userAnswersReader(_, ctcCountryCodes, euCountryCodes, customsSecurityAgreementAreaCountryCodes)
          ).map(_.toSeq)
      }

    OfficeOfDeparturePage.reader.flatMap {
      officeOfDeparture =>
        OfficeOfDestinationPage.reader.flatMap {
          officeOfDestination =>
            if (
              ctcCountryCodes.contains(officeOfDeparture.countryId) &&
              ctcCountryCodes.contains(officeOfDestination.countryId) &&
              officeOfDeparture.countryId.code == officeOfDestination.countryId.code
            ) {
              AddOfficeOfTransitYesNoPage
                .filterOptionalDependent(identity)(officeOfTransitCountriesReader)
                .map(_.getOrElse(Nil))
                .map(TransitDomain(None, _))
            } else {
              DeclarationTypePage.reader.flatMap {
                case DeclarationType.Option2 =>
                  UserAnswersReader[OfficesOfTransit].map(TransitDomain(None, _))
                case DeclarationType.Option5 =>
                  T2DeclarationTypeYesNoPage.reader.flatMap {
                    case true =>
                      UserAnswersReader[OfficesOfTransit].map(TransitDomain(Some(true), _))
                    case false =>
                      if (ctcCountryCodes.contains(officeOfDeparture.countryId) || ctcCountryCodes.contains(officeOfDestination.countryId)) {
                        UserAnswersReader[OfficesOfTransit].map(TransitDomain(Some(false), _))
                      } else {
                        UserAnswersReader[Seq[CountryOfRoutingDomain]]
                          .map(_.map(_.country.code))
                          .flatMap {
                            _.filter(ctcCountryCodes.contains(_)) match {
                              case Nil =>
                                AddOfficeOfTransitYesNoPage
                                  .filterOptionalDependent(identity)(officeOfTransitCountriesReader)
                                  .map(_.getOrElse(Nil))
                                  .map(TransitDomain(Some(false), _))
                              case _ =>
                                UserAnswersReader[OfficesOfTransit].map(TransitDomain(Some(false), _))
                            }
                          }
                      }
                  }
                case _ =>
                  if (ctcCountryCodes.contains(officeOfDeparture.countryId) || ctcCountryCodes.contains(officeOfDestination.countryId)) {
                    UserAnswersReader[OfficesOfTransit].map(TransitDomain(None, _))
                  } else {
                    UserAnswersReader[Seq[CountryOfRoutingDomain]]
                      .map(_.map(_.country.code))
                      .flatMap {
                        _.filter(ctcCountryCodes.contains(_)) match {
                          case Nil =>
                            AddOfficeOfTransitYesNoPage
                              .filterOptionalDependent(identity)(officeOfTransitCountriesReader)
                              .map(_.getOrElse(Nil))
                              .map(TransitDomain(None, _))
                          case _ =>
                            UserAnswersReader[OfficesOfTransit].map(TransitDomain(None, _))
                        }
                      }
                  }
              }
            }
        }
    }
  }
  // scalastyle:on cyclomatic.complexity
  // scalastyle:on method.length
}
