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
import models.journeyDomain.routeDetails.transit.TransitDomain
import models.reference.CountryCode
import navigation.UserAnswersNavigator
import services.CountriesService
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class TransitNavigatorProviderImpl @Inject() (
  countriesService: CountriesService
)(implicit ec: ExecutionContext)
    extends TransitNavigatorProvider {

  def apply()(implicit hc: HeaderCarrier): Future[TransitNavigator] =
    for {
      ctcCountries <- countriesService.getTransitCountries()
      euCountries  <- countriesService.getCommunityCountries()
    } yield new TransitNavigator(
      ctcCountries.countries.map(_.code),
      euCountries.countries.map(_.code)
    )
}

trait TransitNavigatorProvider {

  def apply()(implicit hc: HeaderCarrier): Future[TransitNavigator]
}

class TransitNavigator(
  ctcCountryCodes: Seq[CountryCode],
  euCountryCodes: Seq[CountryCode]
) extends UserAnswersNavigator[TransitDomain, RouteDetailsDomain]()(
      TransitDomain.userAnswersReader(ctcCountryCodes, euCountryCodes),
      RouteDetailsDomain.userAnswersReader(ctcCountryCodes, euCountryCodes)
    )
