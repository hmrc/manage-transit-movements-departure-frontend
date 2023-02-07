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

package models.journeyDomain.transport.equipment.itemNumbers

import base.SpecBase
import models.domain.{EitherType, UserAnswersReader}
import models.journeyDomain.transport.equipment.index.itemNumber.ItemNumberDomain
import org.scalacheck.Gen
import pages.transport.equipment.index.itemNumber.ItemNumberPage

class ItemNumberDomainSpec extends SpecBase {

  "Item Number Domain" - {

    "can be read from user answers" - {
      "when goods item number page is answered" in {
        val itemNumber = Gen.alphaNumStr.sample.value

        val userAnswers = emptyUserAnswers
          .setValue(ItemNumberPage(equipmentIndex, itemNumberIndex), itemNumber)

        val expectedResult = ItemNumberDomain(itemNumber)(equipmentIndex, itemNumberIndex)

        val result: EitherType[ItemNumberDomain] =
          UserAnswersReader[ItemNumberDomain](ItemNumberDomain.userAnswersReader(equipmentIndex, itemNumberIndex)).run(userAnswers)

        result.value mustBe expectedResult
      }
    }

    "can not be read from user answers" - {
      "when goods item number page is unanswered" in {
        val result: EitherType[ItemNumberDomain] =
          UserAnswersReader[ItemNumberDomain](ItemNumberDomain.userAnswersReader(equipmentIndex, itemNumberIndex)).run(emptyUserAnswers)

        result.left.value.page mustBe ItemNumberPage(equipmentIndex, itemNumberIndex)
      }
    }
  }

}
