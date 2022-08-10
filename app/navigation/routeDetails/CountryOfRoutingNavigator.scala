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

import javax.inject.{Inject, Singleton}

@Singleton
class CountryOfRoutingNavigatorProviderImpl @Inject() () extends CountryOfRoutingNavigatorProvider {

  def apply(index: Index): CountryOfRoutingNavigator =
    new CountryOfRoutingNavigator(index)
}

trait CountryOfRoutingNavigatorProvider {

  def apply(index: Index): CountryOfRoutingNavigator
}

class CountryOfRoutingNavigator(
  index: Index
) extends UserAnswersNavigator[CountryOfRoutingDomain, RouteDetailsDomain]()(
      CountryOfRoutingDomain.userAnswersReader(index),
      RouteDetailsDomain.userAnswersReader
    )
