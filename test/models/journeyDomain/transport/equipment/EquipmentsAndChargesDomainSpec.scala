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
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.transport.equipment.AddTransportEquipmentYesNoPage
import pages.transport.preRequisites.ContainerIndicatorPage

class EquipmentsAndChargesDomainSpec extends SpecBase with ScalaCheckPropertyChecks with Generators with UserAnswersGenerator {

  "Equipment and charges domain" - {

    "can be read from user answers" - {
      "when container indicator is true" in {
        val initialAnswers = emptyUserAnswers
          .setValue(ContainerIndicatorPage, true)

        forAll(arbitraryEquipmentAnswers(initialAnswers, Index(0))) {
          userAnswers =>
            val result: EitherType[EquipmentsAndChargesDomain] = UserAnswersReader[EquipmentsAndChargesDomain].run(userAnswers)
            result.value.equipments must be(defined)
        }
      }

      "when container indicator is false" - {
        "and add transport equipment yes/no is yes" in {
          val initialAnswers = emptyUserAnswers
            .setValue(ContainerIndicatorPage, false)
            .setValue(AddTransportEquipmentYesNoPage, true)

          forAll(arbitraryEquipmentAnswers(initialAnswers, Index(0))) {
            userAnswers =>
              val result: EitherType[EquipmentsAndChargesDomain] = UserAnswersReader[EquipmentsAndChargesDomain].run(userAnswers)
              result.value.equipments must be(defined)
          }
        }

        "and add transport equipment yes/no is no" in {
          val userAnswers = emptyUserAnswers
            .setValue(ContainerIndicatorPage, false)
            .setValue(AddTransportEquipmentYesNoPage, false)

          val result: EitherType[EquipmentsAndChargesDomain] = UserAnswersReader[EquipmentsAndChargesDomain].run(userAnswers)
          result.value.equipments must not be defined
        }
      }
    }

    "cannot be read from user answers" - {
      "when container indicator is not answered" in {
        val result: EitherType[EquipmentsAndChargesDomain] = UserAnswersReader[EquipmentsAndChargesDomain].run(emptyUserAnswers)

        result.left.value.page mustBe ContainerIndicatorPage
      }

      "when container indicator is false" - {
        "and add transport equipment yes/no is unanswered" in {
          val userAnswers = emptyUserAnswers
            .setValue(ContainerIndicatorPage, false)

          val result: EitherType[EquipmentsAndChargesDomain] = UserAnswersReader[EquipmentsAndChargesDomain].run(userAnswers)

          result.left.value.page mustBe AddTransportEquipmentYesNoPage
        }
      }
    }
  }

}
