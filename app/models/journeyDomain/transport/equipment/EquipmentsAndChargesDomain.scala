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

package models.journeyDomain.transport.equipment

import models.domain.{GettableAsFilterForNextReaderOps, GettableAsReaderOps, UserAnswersReader}
import models.journeyDomain.transport.equipment.seal.EquipmentsDomain
import pages.transport.equipment.AddTransportEquipmentYesNoPage
import pages.transport.preRequisites.ContainerIndicatorPage

case class EquipmentsAndChargesDomain(
  equipments: Option[EquipmentsDomain]
)

object EquipmentsAndChargesDomain {

  implicit def userAnswersReader: UserAnswersReader[EquipmentsAndChargesDomain] = {
    lazy val equipmentsReads: UserAnswersReader[Option[EquipmentsDomain]] = ContainerIndicatorPage.reader.flatMap {
      case true =>
        UserAnswersReader[EquipmentsDomain].map(Option(_))
      case false =>
        AddTransportEquipmentYesNoPage.filterOptionalDependent(identity) {
          UserAnswersReader[EquipmentsDomain]
        }
    }

    equipmentsReads.map(EquipmentsAndChargesDomain(_))
  }
}
