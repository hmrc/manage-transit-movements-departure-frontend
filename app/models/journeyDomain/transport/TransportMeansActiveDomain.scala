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
import models.Index
import models.domain.{GettableAsFilterForNextReaderOps, GettableAsReaderOps, UserAnswersReader}
import models.journeyDomain.JourneyDomainModel
import models.reference.{CustomsOffice, Nationality}
import models.transport.transportMeans.active.Identification
import pages.transport.transportMeans.active._

case class TransportMeansActiveDomain(
  identification: Identification,
  identificationNumber: String,
  nationality: Option[Nationality],
  customsOffice: CustomsOffice
) extends JourneyDomainModel {}

object TransportMeansActiveDomain {

  def userAnswersReader(index: Index): UserAnswersReader[TransportMeansActiveDomain] =
    (
      IdentificationPage(index).reader,
      IdentificationNumberPage(index).reader,
      AddNationalityYesNoPage(index).filterOptionalDependent(identity)(NationalityPage(index).reader),
      CustomsOfficeActiveBorderPage(index).reader
    ).tupled.map((TransportMeansActiveDomain.apply _).tupled)

}
