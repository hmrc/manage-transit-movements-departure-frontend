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
import navigation.UserAnswersNavigator
import javax.inject.{Inject, Singleton}
import models.journeyDomain.routeDetails.transit.{OfficeOfTransitCountryDomain, TransitDomain}

@Singleton
class OfficeOfTransitCountryNavigatorProviderImpl @Inject() () extends OfficeOfTransitCountryNavigatorProvider {

  def apply(index: Index): OfficeOfTransitCountryNavigator =
    new OfficeOfTransitCountryNavigator(index)
}

trait OfficeOfTransitCountryNavigatorProvider {

  def apply(index: Index): OfficeOfTransitCountryNavigator
}

class OfficeOfTransitCountryNavigator(
  index: Index
) extends UserAnswersNavigator[OfficeOfTransitCountryDomain, TransitDomain]()(
      OfficeOfTransitCountryDomain.userAnswersReader(index),
      TransitDomain.userAnswersReader
    )
