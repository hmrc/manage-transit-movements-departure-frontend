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

package models.journeyDomain.transport.equipment.index.itemNumber

import controllers.transport.equipment.index.itemNumber.{routes => itemNumberRoutes}
import controllers.transport.equipment.index.routes
import models.domain.{GettableAsReaderOps, UserAnswersReader}
import models.journeyDomain.Stage.{AccessingJourney, CompletingJourney}
import models.journeyDomain.{JourneyDomainModel, Stage}
import models.{Index, Mode, UserAnswers}
import pages.transport.equipment.index.itemNumber.ItemNumberPage
import play.api.mvc.Call

case class ItemNumberDomain(
  itemNumber: String
)(equipmentIndex: Index, itemNumberIndex: Index)
    extends JourneyDomainModel {

  override def toString: String = itemNumber

  override def routeIfCompleted(userAnswers: UserAnswers, mode: Mode, stage: Stage): Option[Call] = Some {
    stage match {
      case AccessingJourney  => itemNumberRoutes.ItemNumberController.onPageLoad(userAnswers.lrn, mode, equipmentIndex, itemNumberIndex)
      case CompletingJourney => routes.AddAnotherGoodsItemNumberController.onPageLoad(userAnswers.lrn, mode, equipmentIndex)
    }
  }
}

object ItemNumberDomain {

  def userAnswersReader(equipmentIndex: Index, itemNumberIndex: Index): UserAnswersReader[ItemNumberDomain] =
    ItemNumberPage(equipmentIndex, itemNumberIndex).reader.map(ItemNumberDomain(_)(equipmentIndex, itemNumberIndex))
}
