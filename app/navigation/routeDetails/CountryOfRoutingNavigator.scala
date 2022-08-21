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
import models.journeyDomain.routeDetails.routing.CountryOfRoutingDomain
import navigation.UserAnswersNavigator
import services.CountriesService
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CountryOfRoutingNavigatorProviderImpl @Inject() (
  countriesService: CountriesService
)(implicit ec: ExecutionContext)
    extends CountryOfRoutingNavigatorProvider {

  def apply(index: Index)(implicit hc: HeaderCarrier): Future[CountryOfRoutingNavigator] =
    for {
      ctcCountries                             <- countriesService.getCountryCodesCTC()
      euCountries                              <- countriesService.getCommunityCountries()
      customsSecurityAgreementAreaCountryCodes <- countriesService.getCustomsSecurityAgreementAreaCountries()
    } yield new CountryOfRoutingNavigator(
      index,
      ctcCountries.countryCodes,
      euCountries.countryCodes,
      customsSecurityAgreementAreaCountryCodes.countryCodes
    )
}

trait CountryOfRoutingNavigatorProvider {

  def apply(index: Index)(implicit hc: HeaderCarrier): Future[CountryOfRoutingNavigator]
}

class CountryOfRoutingNavigator(
  index: Index,
  ctcCountryCodes: Seq[String],
  euCountryCodes: Seq[String],
  customsSecurityAgreementAreaCountryCodes: Seq[String]
) extends UserAnswersNavigator[CountryOfRoutingDomain, RouteDetailsDomain]()(
      CountryOfRoutingDomain.userAnswersReader(index),
      RouteDetailsDomain.userAnswersReader(ctcCountryCodes, euCountryCodes, customsSecurityAgreementAreaCountryCodes)
    )
