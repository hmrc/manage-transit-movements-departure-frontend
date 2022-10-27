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

package models.journeyDomain.transport

import cats.implicits._
import models.domain.{GettableAsReaderOps, UserAnswersReader}
import models.journeyDomain.JourneyDomainModel
import models.reference.Nationality
import models.transport.transportMeans.departure.InlandMode
import pages.transport.transportMeans.departure.{InlandModePage, VehicleCountryPage}

case class TransportMeansDomain(
  inlandMode: InlandMode,
  vehicleCountry: Nationality
) extends JourneyDomainModel

object TransportMeansDomain {

  implicit val userAnswersReader: UserAnswersReader[TransportMeansDomain] =
    (
      InlandModePage.reader,
      VehicleCountryPage.reader
    ).tupled.map((TransportMeansDomain.apply _).tupled)
}
