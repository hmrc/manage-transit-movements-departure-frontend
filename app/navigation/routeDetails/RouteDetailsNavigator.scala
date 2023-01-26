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
import models.journeyDomain.routeDetails.RouteDetailsDomain
import models.{CountryList, Mode}
import navigation.UserAnswersNavigator

import javax.inject.{Inject, Singleton}

@Singleton
class RouteDetailsNavigatorProviderImpl @Inject() () extends RouteDetailsNavigatorProvider {

  def apply(mode: Mode, ctcCountries: CountryList, customsSecurityAgreementAreaCountries: CountryList): UserAnswersNavigator =
    RouteDetailsNavigatorProvider(mode, ctcCountries, customsSecurityAgreementAreaCountries)
}

trait RouteDetailsNavigatorProvider {

  def apply(mode: Mode, ctcCountries: CountryList, customsSecurityAgreementAreaCountries: CountryList): UserAnswersNavigator
}

object RouteDetailsNavigatorProvider {

  def apply(
    mode: Mode,
    ctcCountries: CountryList,
    customsSecurityAgreementAreaCountries: CountryList
  ): UserAnswersNavigator =
    new RouteDetailsNavigator(
      mode,
      ctcCountries,
      customsSecurityAgreementAreaCountries
    )
}

class RouteDetailsNavigator(
  override val mode: Mode,
  ctcCountries: CountryList,
  customsSecurityAgreementAreaCountries: CountryList
) extends UserAnswersNavigator {

  override type T = RouteDetailsDomain

  implicit override val reader: UserAnswersReader[RouteDetailsDomain] =
    RouteDetailsDomain.userAnswersReader(ctcCountries.countryCodes, customsSecurityAgreementAreaCountries.countryCodes)
}
