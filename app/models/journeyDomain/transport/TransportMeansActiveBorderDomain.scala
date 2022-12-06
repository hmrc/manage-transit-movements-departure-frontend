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
import models.DeclarationType.Option4
import models.Index
import models.domain.{UserAnswersReader, _}
import models.reference.Country
import models.transport.transportMeans.active.Identification
import pages.transport.transportMeans.active._
import pages.transport.preRequisites._

case class TransportMeansActiveBorderDomain(
  identificationType: Identification,
  countryOfDispatch: Option[Country],
  itemsDestinationCountry: Option[Country],
  containerIndicator: Boolean
)(index: Index)

object TransportMeansActiveBorderDomain {

  implicit val userAnswersReader(index: Index): UserAnswersReader[TransportMeansActiveBorderDomain] = (
    IdentificationPage(index).reader,

    SameUcrYesNoPage.filterOptionalDependent(identity)(UniqueConsignmentReferencePage.reader),
    countryOfDispatchReader,
    TransportedToSameCountryYesNoPage.filterOptionalDependent(identity)(ItemsDestinationCountryPage.reader),
    ContainerIndicatorPage.reader
  ).tupled.map((TransportMeansActiveBorderDomain.apply _).tupled)
}
