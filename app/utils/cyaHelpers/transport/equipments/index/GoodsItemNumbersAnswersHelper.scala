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

package utils.cyaHelpers.transport.equipments.index

import models.journeyDomain.transport.equipment.index.itemNumber.ItemNumberDomain
import models.{Index, Mode, UserAnswers}
import pages.sections.transport.equipment.ItemNumbersSection
import pages.transport.equipment.index.ContainerIdentificationNumberPage
import pages.transport.equipment.index.itemNumber.ItemNumberPage
import play.api.i18n.Messages
import play.api.mvc.Call
import utils.cyaHelpers.AnswersHelper
import viewModels.ListItem

class GoodsItemNumbersAnswersHelper(userAnswers: UserAnswers, mode: Mode, equipmentIndex: Index)(implicit messages: Messages)
    extends AnswersHelper(userAnswers, mode) {

  def listItems: Seq[Either[ListItem, ListItem]] =
    buildListItems(ItemNumbersSection(equipmentIndex)) {
      itemNumberIndex =>
        val removeRoute: Option[Call] = if (equipmentIndex.isFirst && userAnswers.get(ContainerIdentificationNumberPage(equipmentIndex)).nonEmpty) {
          Some(Call("GET", "#")) // TODO: replace confirmRemoveGoodsItemNumber when built
        } else {
          None
        }

        buildListItem[ItemNumberDomain](
          nameWhenComplete = _.itemNumber,
          nameWhenInProgress = userAnswers.get(ItemNumberPage(equipmentIndex, itemNumberIndex)),
          removeRoute = removeRoute
        )(ItemNumberDomain.userAnswersReader(equipmentIndex, itemNumberIndex))
    }

}
