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

package models.journeyDomain.transport.equipment.seal

import models.domain.{GettableAsReaderOps, UserAnswersReader}
import models.journeyDomain.Stage._
import models.journeyDomain.{JourneyDomainModel, Stage}
import models.{Index, Mode, UserAnswers}
import pages.transport.equipment.index.seals.IdentificationNumberPage
import play.api.mvc.Call

case class SealDomain(
  identificationNumber: String
)(equipmentIndex: Index, sealIndex: Index)
    extends JourneyDomainModel {

  override def toString: String = identificationNumber

  override def routeIfCompleted(userAnswers: UserAnswers, mode: Mode, stage: Stage): Option[Call] = Some {
    stage match {
      case AccessingJourney =>
        controllers.transport.equipment.index.seals.routes.IdentificationNumberController.onPageLoad(userAnswers.lrn, mode, equipmentIndex, sealIndex)
      case CompletingJourney => controllers.transport.equipment.index.routes.AddAnotherSealController.onPageLoad(userAnswers.lrn, mode, equipmentIndex)
    }
  }
}

object SealDomain {

  implicit def userAnswersReader(equipmentIndex: Index, sealIndex: Index): UserAnswersReader[SealDomain] =
    IdentificationNumberPage(equipmentIndex, sealIndex).reader.map(SealDomain(_)(equipmentIndex, sealIndex))
}
