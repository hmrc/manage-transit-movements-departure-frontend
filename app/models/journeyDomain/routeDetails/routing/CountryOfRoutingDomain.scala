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

package models.journeyDomain.routeDetails.routing

import models.domain.{GettableAsReaderOps, UserAnswersReader}
import models.journeyDomain.Stage._
import models.journeyDomain.{JourneyDomainModel, Stage}
import models.reference.Country
import models.{Index, NormalMode, UserAnswers}
import pages.routeDetails.routing.index.CountryOfRoutingPage
import play.api.mvc.Call

case class CountryOfRoutingDomain(
  country: Country
)(index: Index)
    extends JourneyDomainModel {

  override def routeIfCompleted(userAnswers: UserAnswers, stage: Stage): Option[Call] = Some {
    stage match {
      case AccessingJourney =>
        controllers.routeDetails.routing.index.routes.CountryOfRoutingController.onPageLoad(userAnswers.lrn, NormalMode, index)
      case CompletingJourney =>
        controllers.routeDetails.routing.routes.AddAnotherCountryOfRoutingController.onPageLoad(userAnswers.lrn)
    }
  }
}

object CountryOfRoutingDomain {

  implicit def userAnswersReader(index: Index): UserAnswersReader[CountryOfRoutingDomain] =
    CountryOfRoutingPage(index).reader.map(CountryOfRoutingDomain(_)(index))
}
