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

import cats.implicits._
import models.domain.{GettableAsReaderOps, UserAnswersReader}
import models.journeyDomain.{JourneyDomainModel, Stage}
import models.reference.{Country, CustomsOffice}
import models.{Mode, UserAnswers}
import pages.routeDetails.routing._
import play.api.mvc.Call

case class RoutingDomain(
  countryOfDestination: Country,
  officeOfDestination: CustomsOffice,
  bindingItinerary: Boolean,
  countriesOfRouting: Seq[CountryOfRoutingDomain]
) extends JourneyDomainModel {

  override def routeIfCompleted(userAnswers: UserAnswers, mode: Mode, stage: Stage): Option[Call] =
    Some(controllers.routeDetails.routing.routes.CheckYourAnswersController.onPageLoad(userAnswers.lrn, mode))
}

object RoutingDomain {

  implicit val userAnswersReader: UserAnswersReader[RoutingDomain] =
    (
      CountryOfDestinationPage.reader,
      OfficeOfDestinationPage.reader,
      BindingItineraryPage.reader,
      UserAnswersReader[Seq[CountryOfRoutingDomain]]
    ).tupled.map((RoutingDomain.apply _).tupled)
}
