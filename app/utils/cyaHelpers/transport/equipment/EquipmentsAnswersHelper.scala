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

import controllers.transport.equipment.index.routes
import models.journeyDomain.transport.equipment.EquipmentDomain
import models.{Mode, UserAnswers}
import pages.sections.transport.equipment.EquipmentsSection
import pages.transport.equipment.AddTransportEquipmentYesNoPage
import pages.transport.equipment.index.ContainerIdentificationNumberPage
import play.api.i18n.Messages
import play.api.mvc.Call
import utils.cyaHelpers.AnswersHelper
import viewModels.ListItem

class EquipmentsAnswersHelper(
  userAnswers: UserAnswers,
  mode: Mode
)(implicit messages: Messages)
    extends AnswersHelper(userAnswers, mode) {

  def listItems: Seq[Either[ListItem, ListItem]] =
    buildListItems(EquipmentsSection) {
      equipmentIndex =>
        val removeRoute: Option[Call] = if (equipmentIndex.isFirst && userAnswers.get(AddTransportEquipmentYesNoPage).isEmpty) {
          None
        } else {
          Some(routes.RemoveTransportEquipmentController.onPageLoad(lrn, mode, equipmentIndex))
        }

        buildListItem[EquipmentDomain](
          nameWhenComplete = _.asString,
          nameWhenInProgress = Some(EquipmentDomain.asString(equipmentIndex, userAnswers.get(ContainerIdentificationNumberPage(equipmentIndex)))),
          removeRoute = removeRoute
        )(EquipmentDomain.userAnswersReader(equipmentIndex))
    }
}
