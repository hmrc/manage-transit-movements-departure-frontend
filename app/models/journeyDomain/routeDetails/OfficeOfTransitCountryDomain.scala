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

package models.journeyDomain.routeDetails

import models.domain.{GettableAsReaderOps, UserAnswersReader}
import models.journeyDomain.Stage._
import models.journeyDomain.{JourneyDomainModel, Stage}
import models.reference.Country
import models.{Index, NormalMode, UserAnswers}
import pages.routeDetails.transit.OfficeOfTransitCountryPage
import play.api.mvc.Call

case class OfficeOfTransitCountryDomain(
  country: Country
)(index: Index)
    extends JourneyDomainModel {

  override def routeIfCompleted(userAnswers: UserAnswers, stage: Stage): Option[Call] = Some {
    stage match {
      case AccessingJourney =>
        controllers.routeDetails.transit.routes.OfficeOfTransitCountryController.onPageLoad(userAnswers.lrn, NormalMode, index)
      case CompletingJourney =>
        controllers.routes.TaskListController.onPageLoad(userAnswers.lrn) // TODO redirect to add another office of transit when built
    }
  }
}

object OfficeOfTransitCountryDomain {

  implicit def userAnswersReader(index: Index): UserAnswersReader[OfficeOfTransitCountryDomain] =
    OfficeOfTransitCountryPage(index).reader.map(OfficeOfTransitCountryDomain(_)(index))
}
