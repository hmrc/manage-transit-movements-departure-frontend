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

package models.journeyDomain.transport.equipment

import base.SpecBase
import generators.{Generators, UserAnswersGenerator}
import models.Index
import models.domain.{EitherType, UserAnswersReader}
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.transport.equipment.index.ContainerIdentificationNumberPage

class EquipmentsDomainSpec extends SpecBase with ScalaCheckPropertyChecks with Generators with UserAnswersGenerator {

  "Equipments Domain" - {

    "can be read from user answers" - {
      "when there are equipments" in {
        val numberOfEquipments = Gen.choose(1, 5: Int).sample.value

        val userAnswers = (0 until numberOfEquipments).foldLeft(emptyUserAnswers) {
          (acc, i) =>
            arbitraryEquipmentAnswers(acc, Index(i)).sample.value
        }

        val result: EitherType[EquipmentsDomain] = UserAnswersReader[EquipmentsDomain].run(userAnswers)

        result.value.value.size mustBe numberOfEquipments
      }
    }

    "can not be read from user answers" - {
      "when there aren't any equipments" in {
        val result: EitherType[EquipmentsDomain] = UserAnswersReader[EquipmentsDomain].run(emptyUserAnswers)

        result.left.value.page mustBe ContainerIdentificationNumberPage(Index(0))
      }
    }
  }

}
