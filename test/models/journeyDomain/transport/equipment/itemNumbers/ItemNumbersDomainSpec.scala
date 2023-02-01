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
import models.Index
import models.domain.{EitherType, UserAnswersReader}
import models.journeyDomain.transport.equipment.index.itemNumber.{ItemNumberDomain, ItemNumbersDomain}
import org.scalacheck.Gen
import pages.transport.equipment.index.itemNumber.ItemNumberPage

class ItemNumbersDomainSpec extends SpecBase {

  "Item Numbers Domain" - {

    "can be read from user answers" - {
      "when there are goods item numbers" in {
        val itemNumber1 = Gen.alphaNumStr.sample.value
        val itemNumber2 = Gen.alphaNumStr.sample.value

        val userAnswers = emptyUserAnswers
          .setValue(ItemNumberPage(equipmentIndex, Index(0)), itemNumber1)
          .setValue(ItemNumberPage(equipmentIndex, Index(1)), itemNumber2)

        val expectedResult = ItemNumbersDomain(
          Seq(
            ItemNumberDomain(itemNumber1)(equipmentIndex, Index(0)),
            ItemNumberDomain(itemNumber2)(equipmentIndex, Index(1))
          )
        )

        val result: EitherType[ItemNumbersDomain] = UserAnswersReader[ItemNumbersDomain](
          ItemNumbersDomain.userAnswersReader(equipmentIndex)
        ).run(userAnswers)

        result.value mustBe expectedResult
      }
    }

    "can not be read from user answers" - {
      "when there aren't any goods item numbers" in {
        val result: EitherType[ItemNumbersDomain] = UserAnswersReader[ItemNumbersDomain](
          ItemNumbersDomain.userAnswersReader(equipmentIndex)
        ).run(emptyUserAnswers)

        result.left.value.page mustBe ItemNumberPage(equipmentIndex, Index(0))
      }
    }
  }

}
