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
import models.journeyDomain.routeDetails.locationOfGoods.LocationOfGoodsDomain
import models.{CheckMode, CountryList, Mode, NormalMode}
import navigation.UserAnswersNavigator

import javax.inject.{Inject, Singleton}

@Singleton
class LocationOfGoodsNavigatorProviderImpl @Inject() () extends LocationOfGoodsNavigatorProvider {

  def apply(mode: Mode, ctcCountries: CountryList, customsSecurityAgreementAreaCountries: CountryList): UserAnswersNavigator =
    mode match {
      case NormalMode =>
        new LocationOfGoodsNavigator(mode)
      case CheckMode =>
        new RouteDetailsNavigator(mode, ctcCountries, customsSecurityAgreementAreaCountries)
    }
}

trait LocationOfGoodsNavigatorProvider {

  def apply(mode: Mode, ctcCountries: CountryList, customsSecurityAgreementAreaCountries: CountryList): UserAnswersNavigator
}

class LocationOfGoodsNavigator(override val mode: Mode) extends UserAnswersNavigator {

  override type T = LocationOfGoodsDomain

  implicit override val reader: UserAnswersReader[LocationOfGoodsDomain] =
    LocationOfGoodsDomain.userAnswersReader
}
