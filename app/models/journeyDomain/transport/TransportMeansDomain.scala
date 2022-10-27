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
import pages.transport.transportMeans.departure.{IdentificationPage, InlandModePage, VehicleCountryPage}

sealed trait TransportMeansDomain extends JourneyDomainModel {
  val inlandMode: InlandMode
}

sealed trait TransportMeansDomainWithIdentification extends TransportMeansDomain {
  val identification: Identification
}

object TransportMeansDomain {

  implicit val userAnswersReader: UserAnswersReader[TransportMeansDomain] =
    InlandModePage.reader.flatMap {
      case InlandMode.Mail    => UserAnswersReader.apply(TransportMeansDomainWithMailInlandMode)
      case InlandMode.Unknown => UserAnswersReader[TransportMeansDomainWithUnknownInlandMode].widen[TransportMeansDomain]
      case _                  => UserAnswersReader[TransportMeansDomainWithAnyOtherInlandMode].widen[TransportMeansDomain]
    }
}

case object TransportMeansDomainWithMailInlandMode extends TransportMeansDomain {
  override val inlandMode: InlandMode = InlandMode.Mail
}

case class TransportMeansDomainWithUnknownInlandMode(
  nationality: Nationality
) extends TransportMeansDomainWithIdentification {
  override val inlandMode: InlandMode         = InlandMode.Unknown
  override val identification: Identification = Identification.Unknown
}

object TransportMeansDomainWithUnknownInlandMode {

  implicit val userAnswersReader: UserAnswersReader[TransportMeansDomainWithUnknownInlandMode] =
    VehicleCountryPage.reader.map(
      x => TransportMeansDomainWithUnknownInlandMode(x)
    )
}

case class TransportMeansDomainWithAnyOtherInlandMode(
  override val inlandMode: InlandMode,
  override val identification: Identification,
  nationality: Nationality
) extends TransportMeansDomainWithIdentification

object TransportMeansDomainWithAnyOtherInlandMode {

  implicit val userAnswersReader: UserAnswersReader[TransportMeansDomainWithAnyOtherInlandMode] =
    (
      InlandModePage.reader,
      IdentificationPage.reader,
      VehicleCountryPage.reader
    ).tupled.map((TransportMeansDomainWithAnyOtherInlandMode.apply _).tupled)
}
