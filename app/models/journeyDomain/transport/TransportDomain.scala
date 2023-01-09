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

import models.domain.{GettableAsFilterForNextReaderOps, UserAnswersReader}
import models.journeyDomain.JourneyDomainModel
import pages.transport.supplyChainActors.SupplyChainActorYesNoPage

case class TransportDomain(
  preRequisites: PreRequisitesDomain,
  transportMeans: TransportMeansDomain,
  supplyChainActors: Option[SupplyChainActorsDomain]
) extends JourneyDomainModel

object TransportDomain {

  implicit val userAnswersReader: UserAnswersReader[TransportDomain] = {

//    implicit val authorisationsReads: UserAnswersReader[Option[AuthorisationsDomain]] =
//      ApprovedOperatorPage.reader.flatMap {
//
//        case true => UserAnswersReader[AuthorisationsDomain].map(Some(_))
//        case _   => none[AuthorisationsDomain].pure[UserAnswersReader] //TODO: Replace with AddAuthorisationYesNoPage reader filterOptionalDependant when created
//      }

    for {
      preRequisites     <- UserAnswersReader[PreRequisitesDomain]
      transportMeans    <- UserAnswersReader[TransportMeansDomain]
      supplyChainActors <- SupplyChainActorYesNoPage.filterOptionalDependent(identity)(UserAnswersReader[SupplyChainActorsDomain])
//      authorisations    <- UserAnswersReader[AuthorisationsDomain](authorisationsReads)
    } yield TransportDomain(preRequisites, transportMeans, supplyChainActors)
  }

}
