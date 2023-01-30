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

package utils.cyaHelpers.transport.equipment

import models.journeyDomain.transport.equipment.seal.SealDomain
import models.{Index, Mode, UserAnswers}
import pages.sections.SealsSection
import pages.transport.equipment.index.AddSealYesNoPage
import pages.transport.equipment.index.seals.IdentificationNumberPage
import play.api.i18n.Messages
import play.api.mvc.Call
import utils.cyaHelpers.AnswersHelper
import viewModels.ListItem

class SealsAnswersHelper(userAnswers: UserAnswers, mode: Mode, equipmentIndex: Index)(implicit messages: Messages) extends AnswersHelper(userAnswers, mode) {

  def listItems: Seq[Either[ListItem, ListItem]] =
    buildListItems(SealsSection(equipmentIndex)) {
      index =>
        val removeRoute: Option[Call] = if (userAnswers.get(AddSealYesNoPage(equipmentIndex)).isEmpty && index == Index(0)) {
          None
        } else {
          Some(
            controllers.transport.supplyChainActors.index.routes.RemoveSupplyChainActorController.onPageLoad(userAnswers.lrn, mode, index)
          ) //TODO: Change to ConfirmRemoveSeal when added
        }
        buildListItem[SealDomain](
          nameWhenComplete = _.identificationNumber,
          nameWhenInProgress = userAnswers.get(IdentificationNumberPage(equipmentIndex, index)),
          removeRoute = removeRoute
        )(SealDomain.userAnswersReader(equipmentIndex, index))
    }

}
