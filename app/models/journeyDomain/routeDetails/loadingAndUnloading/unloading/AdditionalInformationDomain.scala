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

package models.journeyDomain.routeDetails.loadingAndUnloading.unloading

import cats.implicits._
import models.domain._
import models.journeyDomain.JourneyDomainModel
import models.reference.Country
import pages.routeDetails.loadingAndUnloading.unloading.{CountryPage, LocationPage}

case class AdditionalInformationDomain(
  country: Country,
  location: String
) extends JourneyDomainModel

object AdditionalInformationDomain {

  implicit val userAnswersReader: UserAnswersReader[AdditionalInformationDomain] =
    (
      CountryPage.reader,
      LocationPage.reader
    ).tupled.map((AdditionalInformationDomain.apply _).tupled)

}
