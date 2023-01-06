/*
 * Copyright 2023 HM Revenue & Customs
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

import models.Mode
import models.domain.UserAnswersReader
import models.journeyDomain.routeDetails.RouteDetailsDomain
import navigation.UserAnswersNavigator
import services.CountriesService
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class RouteDetailsNavigatorProviderImpl @Inject() (
  countriesService: CountriesService
)(implicit ec: ExecutionContext)
    extends RouteDetailsNavigatorProvider {

  def apply(mode: Mode)(implicit hc: HeaderCarrier): Future[UserAnswersNavigator] =
    RouteDetailsNavigatorProvider(countriesService, mode)
}

trait RouteDetailsNavigatorProvider {

  def apply(mode: Mode)(implicit hc: HeaderCarrier): Future[UserAnswersNavigator]
}

object RouteDetailsNavigatorProvider {

  def apply(
    countriesService: CountriesService,
    mode: Mode
  )(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[UserAnswersNavigator] =
    for {
      ctcCountries                          <- countriesService.getCountryCodesCTC()
      customsSecurityAgreementAreaCountries <- countriesService.getCustomsSecurityAgreementAreaCountries()
    } yield new RouteDetailsNavigator(
      mode,
      ctcCountries.countryCodes,
      customsSecurityAgreementAreaCountries.countryCodes
    )
}

class RouteDetailsNavigator(
  override val mode: Mode,
  ctcCountryCodes: Seq[String],
  customsSecurityAgreementAreaCountryCodes: Seq[String]
) extends UserAnswersNavigator {

  override type T = RouteDetailsDomain

  implicit override val reader: UserAnswersReader[RouteDetailsDomain] =
    RouteDetailsDomain.userAnswersReader(ctcCountryCodes, customsSecurityAgreementAreaCountryCodes)
}
