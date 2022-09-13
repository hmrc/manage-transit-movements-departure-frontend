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
import models.journeyDomain.routeDetails.loading.LoadingDomain
import navigation.UserAnswersNavigator
import services.CountriesService
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class LoadingNavigatorProviderImpl @Inject() (
  countriesService: CountriesService
)(implicit ec: ExecutionContext)
    extends LoadingNavigatorProvider {

  def apply()(implicit hc: HeaderCarrier): Future[LoadingNavigator] =
    for {
      ctcCountries                             <- countriesService.getCountryCodesCTC()
      customsSecurityAgreementAreaCountryCodes <- countriesService.getCustomsSecurityAgreementAreaCountries()
    } yield new LoadingNavigator(
      ctcCountries.countryCodes,
      customsSecurityAgreementAreaCountryCodes.countryCodes
    )
}

trait LoadingNavigatorProvider {

  def apply()(implicit hc: HeaderCarrier): Future[LoadingNavigator]
}

class LoadingNavigator(
  ctcCountryCodes: Seq[String],
  customsSecurityAgreementAreaCountryCodes: Seq[String]
) extends UserAnswersNavigator[LoadingDomain, RouteDetailsDomain]()(
      LoadingDomain.userAnswersReader,
      RouteDetailsDomain.userAnswersReader(ctcCountryCodes, customsSecurityAgreementAreaCountryCodes)
    )