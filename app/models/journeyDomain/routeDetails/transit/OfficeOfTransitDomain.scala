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

package models.journeyDomain.routeDetails.transit

import java.time.LocalDateTime

import cats.implicits._
import models.Index
import models.domain.{GettableAsFilterForNextReaderOps, GettableAsReaderOps, UserAnswersReader}
import models.journeyDomain.JourneyDomainModel
import models.reference.{Country, CustomsOffice}
import pages.routeDetails.transit.{AddOfficeOfTransitETAYesNoPage, _}

case class OfficeOfTransitDomain(
  country: Country,
  customsOffice: CustomsOffice,
  officeOfTransitETA: Option[LocalDateTime]
)(index: Index)
    extends JourneyDomainModel {}

object OfficeOfTransitDomain {

  implicit def userAnswersReader(index: Index): UserAnswersReader[OfficeOfTransitDomain] =
    (
      OfficeOfTransitCountryPage(index).reader,
      OfficeOfTransitPage(index).reader,
      AddOfficeOfTransitETAYesNoPage(index).filterOptionalDependent(identity)(OfficeOfTransitETAPage(index).reader)
    ).mapN {
      (officeOfTransitCountry, officeOfTransit, officeOfTransitETA) =>
        OfficeOfTransitDomain(officeOfTransitCountry, officeOfTransit, officeOfTransitETA)(index)
    }
}
