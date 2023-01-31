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

import models.domain.{JsArrayGettableAsReaderOps, UserAnswersReader}
import models.{Index, RichJsArray}
import pages.sections.transport.equipment.ItemNumbersSection

case class ItemNumbersDomain(itemNumbers: Seq[ItemNumberDomain])

object ItemNumbersDomain {

  implicit def userAnswersReader(equipmentIndex: Index): UserAnswersReader[ItemNumbersDomain] =
    ItemNumbersSection(equipmentIndex).arrayReader
      .flatMap {
        case x if x.isEmpty =>
          UserAnswersReader(ItemNumberDomain.userAnswersReader(equipmentIndex, Index(0))).map(Seq(_))
        case x =>
          x.traverse[ItemNumberDomain](ItemNumberDomain.userAnswersReader(equipmentIndex, _))
      }
      .map(ItemNumbersDomain(_))
}
