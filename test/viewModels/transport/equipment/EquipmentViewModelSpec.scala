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

package viewModels.transport.equipment

import base.SpecBase
import controllers.transport.equipment.index.routes
import generators.Generators
import models.{Index, Mode, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import pages.transport.equipment.index._
import viewModels.Link
import viewModels.transport.equipment.EquipmentViewModel.EquipmentViewModelProvider

class EquipmentViewModelSpec extends SpecBase with Generators {

  "apply" - {
    "when user answers empty" - {
      "must return empty rows" in {
        val answers           = emptyUserAnswers
        val mode              = arbitrary[Mode].sample.value
        val viewModelProvider = injector.instanceOf[EquipmentViewModelProvider]
        val sections          = viewModelProvider.apply(answers, mode, index).sections

        sections.size mustBe 3

        sections.head.sectionTitle must not be defined
        sections.head.rows must be(empty)
        sections.head.addAnotherLink must not be defined

        sections(1).sectionTitle.get mustBe "Seals"
        sections(1).rows must be(empty)
        sections(1).addAnotherLink.get mustBe Link(
          "add-or-remove-seals",
          "Add or remove seals",
          routes.AddAnotherSealController.onPageLoad(answers.lrn, mode, index).url
        )

        sections(2).sectionTitle.get mustBe "Goods item numbers"
        sections(2).rows must be(empty)
        sections(2).addAnotherLink.get mustBe Link(
          "add-or-remove-goods-item-numbers",
          "Add or remove goods item numbers",
          routes.AddAnotherGoodsItemNumberController.onPageLoad(answers.lrn, mode, index).url
        )
      }
    }

    "when user answers populated" - {
      val containerId = nonEmptyString.sample.value

      "must return row for each answer" in {

        val numberOfSeals            = Gen.choose(1, 10: Int).sample.value
        val numberOfGoodsItemNumbers = Gen.choose(1, 10: Int).sample.value

        implicit class TestRichUserAnswers(userAnswers: UserAnswers) {
          def setSealsValues(): UserAnswers =
            (0 until numberOfSeals).foldLeft(userAnswers) {
              (acc, i) =>
                acc.setValue(seals.IdentificationNumberPage(index, Index(i)), nonEmptyString.sample.value)
            }

          def setGoodsItemNumberValues(): UserAnswers =
            (0 until numberOfGoodsItemNumbers).foldLeft(userAnswers) {
              (acc, i) =>
                acc.setValue(itemNumber.ItemNumberPage(index, Index(i)), nonEmptyString.sample.value)
            }
        }

        val answers = emptyUserAnswers
          .setValue(AddContainerIdentificationNumberYesNoPage(index), true)
          .setValue(ContainerIdentificationNumberPage(index), containerId)
          .setValue(AddSealYesNoPage(index), true)
          .setSealsValues()
          .setValue(AddGoodsItemNumberYesNoPage(index), true)
          .setGoodsItemNumberValues()

        val mode              = arbitrary[Mode].sample.value
        val viewModelProvider = injector.instanceOf[EquipmentViewModelProvider]
        val sections          = viewModelProvider.apply(answers, mode, index).sections

        sections.size mustBe 3

        sections.head.sectionTitle must not be defined
        sections.head.rows.size mustBe 2
        sections.head.rows.head.value.value mustBe "Yes"
        sections.head.rows(1).value.value mustBe containerId
        sections.head.addAnotherLink must not be defined

        sections(1).sectionTitle.get mustBe "Seals"
        sections(1).rows.size mustBe 1 + numberOfSeals
        sections(1).rows.head.value.value mustBe "Yes"
        sections(1).addAnotherLink.get mustBe Link(
          "add-or-remove-seals",
          "Add or remove seals",
          routes.AddAnotherSealController.onPageLoad(answers.lrn, mode, index).url
        )

        sections(2).sectionTitle.get mustBe "Goods item numbers"
        sections(2).rows.size mustBe 1 + numberOfGoodsItemNumbers
        sections(2).rows.head.value.value mustBe "Yes"
        sections(2).addAnotherLink.get mustBe Link(
          "add-or-remove-goods-item-numbers",
          "Add or remove goods item numbers",
          routes.AddAnotherGoodsItemNumberController.onPageLoad(answers.lrn, mode, index).url
        )
      }
    }
  }
}
