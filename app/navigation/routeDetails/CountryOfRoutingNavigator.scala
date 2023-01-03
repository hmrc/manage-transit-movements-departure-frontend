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

  def apply(mode: Mode, index: Index)(implicit hc: HeaderCarrier): Future[UserAnswersNavigator] =
    mode match {
      case NormalMode =>
        Future.successful(new CountryOfRoutingNavigator(mode, index))
      case CheckMode =>
        RouteDetailsNavigatorProvider(countriesService, mode)
    }
}

trait CountryOfRoutingNavigatorProvider {

  def apply(mode: Mode, index: Index)(implicit hc: HeaderCarrier): Future[UserAnswersNavigator]
}

class CountryOfRoutingNavigator(
  override val mode: Mode,
  index: Index
) extends UserAnswersNavigator {

  override type T = CountryOfRoutingDomain

  implicit override val reader: UserAnswersReader[CountryOfRoutingDomain] =
    CountryOfRoutingDomain.userAnswersReader(index)
}
