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
import models.transport.transportMeans.departure.{Identification, InlandMode}
import pages.transport.transportMeans.departure.{IdentificationPage, InlandModePage, MeansIdentificationNumberPage, VehicleCountryPage}

sealed trait TransportMeansDepartureDomain extends JourneyDomainModel

sealed trait TransportMeansDomainWithIdentification extends TransportMeansDepartureDomain {
  val identification: Identification
  val identificationNumber: String
  val nationality: Nationality
}

object TransportMeansDepartureDomain {

  implicit val userAnswersReader: UserAnswersReader[TransportMeansDepartureDomain] =
    InlandModePage.reader.flatMap {
      case InlandMode.Mail    => UserAnswersReader.fail(InlandModePage)
      case InlandMode.Unknown => UserAnswersReader[TransportMeansDomainWithUnknownInlandMode].widen[TransportMeansDepartureDomain]
      case _                  => UserAnswersReader[TransportMeansDomainWithAnyOtherInlandMode].widen[TransportMeansDepartureDomain]
    }
}

case class TransportMeansDomainWithUnknownInlandMode(
  identificationNumber: String,
  nationality: Nationality
) extends TransportMeansDomainWithIdentification {
  override val identification: Identification = Identification.Unknown
}

object TransportMeansDomainWithUnknownInlandMode {

  implicit val userAnswersReader: UserAnswersReader[TransportMeansDomainWithUnknownInlandMode] =
    (
      MeansIdentificationNumberPage.reader,
      VehicleCountryPage.reader
    ).tupled.map((TransportMeansDomainWithUnknownInlandMode.apply _).tupled)
}

case class TransportMeansDomainWithAnyOtherInlandMode(
  identification: Identification,
  identificationNumber: String,
  nationality: Nationality
) extends TransportMeansDomainWithIdentification

object TransportMeansDomainWithAnyOtherInlandMode {

  implicit val userAnswersReader: UserAnswersReader[TransportMeansDomainWithAnyOtherInlandMode] =
    (
      IdentificationPage.reader,
      MeansIdentificationNumberPage.reader,
      VehicleCountryPage.reader
    ).tupled.map((TransportMeansDomainWithAnyOtherInlandMode.apply _).tupled)
}
