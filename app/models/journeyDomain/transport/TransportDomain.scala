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

package models.journeyDomain.transport

import models.domain.{GettableAsFilterForNextReaderOps, GettableAsReaderOps, UserAnswersReader}
import models.journeyDomain.JourneyDomainModel
import models.journeyDomain.transport.carrierDetails.CarrierDetailsDomain
import pages.traderDetails.consignment.ApprovedOperatorPage
import pages.transport.authorisationsAndLimit.authorisations.AddAuthorisationsYesNoPage
import pages.transport.supplyChainActors.SupplyChainActorYesNoPage

case class TransportDomain(
  preRequisites: PreRequisitesDomain,
  transportMeans: TransportMeansDomain,
  supplyChainActors: Option[SupplyChainActorsDomain],
  authorisationsAndLimit: Option[AuthorisationsAndLimitDomain],
  carrierDetails: CarrierDetailsDomain
) extends JourneyDomainModel

object TransportDomain {

  implicit val userAnswersReader: UserAnswersReader[TransportDomain] = {

    implicit lazy val authorisationsAndLimitReads: UserAnswersReader[Option[AuthorisationsAndLimitDomain]] =
      ApprovedOperatorPage.reader.flatMap {
        case true  => UserAnswersReader[AuthorisationsAndLimitDomain].map(Some(_))
        case false => AddAuthorisationsYesNoPage.filterOptionalDependent(identity)(UserAnswersReader[AuthorisationsAndLimitDomain])
      }

    for {
      preRequisites          <- UserAnswersReader[PreRequisitesDomain]
      transportMeans         <- UserAnswersReader[TransportMeansDomain]
      supplyChainActors      <- SupplyChainActorYesNoPage.filterOptionalDependent(identity)(UserAnswersReader[SupplyChainActorsDomain])
      authorisationsAndLimit <- authorisationsAndLimitReads
      carrierDetails         <- UserAnswersReader[CarrierDetailsDomain]
    } yield TransportDomain(preRequisites, transportMeans, supplyChainActors, authorisationsAndLimit, carrierDetails)
  }

}
