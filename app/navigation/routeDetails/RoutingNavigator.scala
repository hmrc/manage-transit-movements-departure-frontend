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

import models.journeyDomain.routeDetails.RouteDetailsDomain
import models.journeyDomain.routeDetails.routing.RoutingDomain
import models.reference.CountryCode
import navigation.UserAnswersNavigator
import services.CountriesService
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class RoutingNavigatorProviderImpl @Inject() (
  countriesService: CountriesService
)(implicit ec: ExecutionContext)
    extends RoutingNavigatorProvider {

  def apply()(implicit hc: HeaderCarrier): Future[RoutingNavigator] =
    for {
      ctcCountries                             <- countriesService.getTransitCountries()
      euCountries                              <- countriesService.getCommunityCountries()
      customsSecurityAgreementAreaCountryCodes <- countriesService.getCustomsSecurityAgreementAreaCountries()
    } yield new RoutingNavigator(
      ctcCountries.countryCodes,
      euCountries.countryCodes,
      customsSecurityAgreementAreaCountryCodes.countryCodes
    )
}

trait RoutingNavigatorProvider {

  def apply()(implicit hc: HeaderCarrier): Future[RoutingNavigator]
}

class RoutingNavigator(
  ctcCountryCodes: Seq[CountryCode],
  euCountryCodes: Seq[CountryCode],
  customsSecurityAgreementAreaCountryCodes: Seq[CountryCode]
) extends UserAnswersNavigator[RoutingDomain, RouteDetailsDomain]()(
      RoutingDomain.userAnswersReader,
      RouteDetailsDomain.userAnswersReader(ctcCountryCodes, euCountryCodes, customsSecurityAgreementAreaCountryCodes)
    )
