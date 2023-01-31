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
import generators.Generators
import models.Mode
import org.scalacheck.Arbitrary.arbitrary
import pages.transport.equipment.index._
import viewModels.transport.equipment.EquipmentViewModel.EquipmentViewModelProvider

class EquipmentViewModelSpec extends SpecBase with Generators {

  "apply" - {
    "when user answers empty" - {
      "must return empty rows" in {
        val mode              = arbitrary[Mode].sample.value
        val viewModelProvider = injector.instanceOf[EquipmentViewModelProvider]
        val sections          = viewModelProvider.apply(emptyUserAnswers, mode, index).sections

        sections.size mustBe 3

        sections.head.sectionTitle must not be defined
        sections.head.rows must be(empty)

        sections(1).sectionTitle.get mustBe "Seals"
        sections(1).rows must be(empty)

        sections(2).sectionTitle.get mustBe "Goods item numbers"
        sections(2).rows must be(empty)
      }
    }

    "when user answers populated" - {
      val containerId = nonEmptyString.sample.value

      "must return row for each answer" in {

        val answers = emptyUserAnswers
          .setValue(AddContainerIdentificationNumberYesNoPage(index), true)
          .setValue(ContainerIdentificationNumberPage(index), containerId)
          .setValue(AddSealYesNoPage(index), true)
          .setValue(AddGoodsItemNumberYesNoPage(index), true)

        val mode              = arbitrary[Mode].sample.value
        val viewModelProvider = injector.instanceOf[EquipmentViewModelProvider]
        val sections          = viewModelProvider.apply(answers, mode, index).sections

        sections.size mustBe 3

        sections.head.sectionTitle must not be defined
        sections.head.rows.size mustBe 2
        sections.head.rows.head.value.value mustBe "Yes"
        sections.head.rows(1).value.value mustBe containerId

        sections(1).sectionTitle.get mustBe "Seals"
        sections(1).rows.size mustBe 1
        sections(1).rows.head.value.value mustBe "Yes"

        sections(2).sectionTitle.get mustBe "Goods item numbers"
        sections(2).rows.size mustBe 1
        sections(2).rows.head.value.value mustBe "Yes"
      }
    }
  }
}
