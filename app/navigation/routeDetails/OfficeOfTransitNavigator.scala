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

import models._
import models.domain.UserAnswersReader
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

  def apply(mode: Mode, index: Index)(implicit hc: HeaderCarrier): Future[UserAnswersNavigator] =
    for {
      ctcCountryCodes                          <- countriesService.getCountryCodesCTC().map(_.countryCodes)
      customsSecurityAgreementAreaCountryCodes <- countriesService.getCustomsSecurityAgreementAreaCountries().map(_.countryCodes)
    } yield mode match {
      case NormalMode =>
        new OfficeOfTransitNavigator(mode, index, ctcCountryCodes, customsSecurityAgreementAreaCountryCodes)
      case CheckMode =>
        new RouteDetailsNavigator(mode, ctcCountryCodes, customsSecurityAgreementAreaCountryCodes)
    }
}

trait OfficeOfTransitNavigatorProvider {

  def apply(mode: Mode, index: Index)(implicit hc: HeaderCarrier): Future[UserAnswersNavigator]
}

class OfficeOfTransitNavigator(
  override val mode: Mode,
  index: Index,
  ctcCountryCodes: Seq[String],
  customsSecurityAgreementAreaCountryCodes: Seq[String]
) extends UserAnswersNavigator {

  override type T = OfficeOfTransitDomain

  implicit override val reader: UserAnswersReader[OfficeOfTransitDomain] =
    OfficeOfTransitDomain.userAnswersReader(index, ctcCountryCodes, customsSecurityAgreementAreaCountryCodes)
}
