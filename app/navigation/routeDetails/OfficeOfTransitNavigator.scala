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

package navigation.routeDetails

import models._
import models.journeyDomain.routeDetails.RouteDetailsDomain
import models.journeyDomain.routeDetails.transit.OfficeOfTransitDomain
import navigation.UserAnswersNavigator
import services.CountriesService
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class OfficeOfTransitNavigatorProviderImpl @Inject() (
  countriesService: CountriesService
)(implicit ec: ExecutionContext)
    extends OfficeOfTransitNavigatorProvider {

  def apply(index: Index)(implicit hc: HeaderCarrier): Future[OfficeOfTransitNavigator] =
    for {
      ctcCountries                             <- countriesService.getCountryCodesCTC()
      euCountries                              <- countriesService.getCommunityCountries()
      customsSecurityAgreementAreaCountryCodes <- countriesService.getCustomsSecurityAgreementAreaCountries()
    } yield new OfficeOfTransitNavigator(
      index,
      ctcCountries.countryCodes,
      euCountries.countryCodes,
      customsSecurityAgreementAreaCountryCodes.countryCodes
    )
}

trait OfficeOfTransitNavigatorProvider {

  def apply(index: Index)(implicit hc: HeaderCarrier): Future[OfficeOfTransitNavigator]
}

class OfficeOfTransitNavigator(
  index: Index,
  ctcCountryCodes: Seq[String],
  euCountryCodes: Seq[String],
  customsSecurityAgreementAreaCountryCodes: Seq[String]
) extends UserAnswersNavigator[OfficeOfTransitDomain, RouteDetailsDomain]()(
      OfficeOfTransitDomain.userAnswersReader(index, ctcCountryCodes, euCountryCodes, customsSecurityAgreementAreaCountryCodes),
      RouteDetailsDomain.userAnswersReader(ctcCountryCodes, euCountryCodes, customsSecurityAgreementAreaCountryCodes)
    )
