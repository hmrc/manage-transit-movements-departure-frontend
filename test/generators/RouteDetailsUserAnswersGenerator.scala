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

package generators

import models.journeyDomain.routeDetails._
import models.journeyDomain.routeDetails.transit.{OfficeOfTransitDomain, TransitDomain}
import models.{Index, UserAnswers}
import org.scalacheck.Gen

trait RouteDetailsUserAnswersGenerator extends UserAnswersGenerator {
  self: Generators =>

  def arbitraryRouteDetailsAnswers(userAnswers: UserAnswers): Gen[UserAnswers] =
    buildUserAnswers[RouteDetailsDomain](userAnswers)

  def arbitraryRoutingAnswers(userAnswers: UserAnswers): Gen[UserAnswers] =
    buildUserAnswers[RoutingDomain](userAnswers)

  def arbitraryCountryOfRoutingAnswers(userAnswers: UserAnswers, index: Index): Gen[UserAnswers] =
    buildUserAnswers[CountryOfRoutingDomain](userAnswers)(CountryOfRoutingDomain.userAnswersReader(index))

  def arbitraryTransitAnswers(userAnswers: UserAnswers): Gen[UserAnswers] =
    buildUserAnswers[TransitDomain](userAnswers)

  def arbitraryOfficeOfTransitAnswers(userAnswers: UserAnswers, index: Index): Gen[UserAnswers] =
    buildUserAnswers[OfficeOfTransitDomain](userAnswers)(OfficeOfTransitDomain.userAnswersReader(index))
}
