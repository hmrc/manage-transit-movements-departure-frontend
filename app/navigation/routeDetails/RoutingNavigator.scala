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

import models.domain.UserAnswersReader
import models.journeyDomain.routeDetails.routing.RoutingDomain
import models.{CheckMode, Mode, NormalMode}
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

  def apply(mode: Mode)(implicit hc: HeaderCarrier): Future[UserAnswersNavigator] =
    mode match {
      case NormalMode =>
        Future.successful(new RoutingNavigator(mode))
      case CheckMode =>
        RouteDetailsNavigatorProvider(countriesService, mode)
    }
}

trait RoutingNavigatorProvider {

  def apply(mode: Mode)(implicit hc: HeaderCarrier): Future[UserAnswersNavigator]
}

class RoutingNavigator(override val mode: Mode) extends UserAnswersNavigator {

  override type T = RoutingDomain

  implicit override val reader: UserAnswersReader[RoutingDomain] =
    RoutingDomain.userAnswersReader
}
