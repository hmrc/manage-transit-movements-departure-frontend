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
import models.SecurityDetailsType.NoSecurityDetails
import models.domain.{EitherType, UserAnswersReader}
import models.transport.equipment.PaymentMethod
import models.{Index, SecurityDetailsType}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.preTaskList.SecurityDetailsTypePage
import pages.transport.equipment._
import pages.transport.preRequisites.ContainerIndicatorPage

class EquipmentsAndChargesDomainSpec extends SpecBase with ScalaCheckPropertyChecks with Generators with UserAnswersGenerator {

  "Equipment and charges domain" - {

    "userAnswersReader" - {
      "when add transport equipment yes/no is no" - {
        "equipments and payment method must both be None" in {
          val userAnswers = emptyUserAnswers
            .setValue(ContainerIndicatorPage, false)
            .setValue(AddTransportEquipmentYesNoPage, false)

          val expectedResult = EquipmentsAndChargesDomain(
            equipments = None,
            paymentMethod = None
          )

          val result: EitherType[EquipmentsAndChargesDomain] = UserAnswersReader[EquipmentsAndChargesDomain].run(userAnswers)

          result.value mustBe expectedResult
        }
      }
    }

    "equipmentsReads" - {

      "can be read from user answers" - {
        "when container indicator is true" in {
          val initialAnswers = emptyUserAnswers
            .setValue(ContainerIndicatorPage, true)

          forAll(arbitraryEquipmentAnswers(initialAnswers, Index(0))) {
            userAnswers =>
              val result: EitherType[Option[EquipmentsDomain]] = UserAnswersReader[Option[EquipmentsDomain]](
                EquipmentsAndChargesDomain.equipmentsReads
              ).run(userAnswers)
              result.value must be(defined)
          }
        }

        "when container indicator is false" - {
          "and add transport equipment yes/no is yes" in {
            val initialAnswers = emptyUserAnswers
              .setValue(ContainerIndicatorPage, false)
              .setValue(AddTransportEquipmentYesNoPage, true)

            forAll(arbitraryEquipmentAnswers(initialAnswers, Index(0))) {
              userAnswers =>
                val result: EitherType[Option[EquipmentsDomain]] = UserAnswersReader[Option[EquipmentsDomain]](
                  EquipmentsAndChargesDomain.equipmentsReads
                ).run(userAnswers)
                result.value must be(defined)
            }
          }

          "and add transport equipment yes/no is no" in {
            val userAnswers = emptyUserAnswers
              .setValue(ContainerIndicatorPage, false)
              .setValue(AddTransportEquipmentYesNoPage, false)

            val result: EitherType[Option[EquipmentsDomain]] = UserAnswersReader[Option[EquipmentsDomain]](
              EquipmentsAndChargesDomain.equipmentsReads
            ).run(userAnswers)
            result.value must not be defined
          }
        }
      }

      "cannot be read from user answers" - {
        "when container indicator is not answered" in {
          val result: EitherType[Option[EquipmentsDomain]] = UserAnswersReader[Option[EquipmentsDomain]](
            EquipmentsAndChargesDomain.equipmentsReads
          ).run(emptyUserAnswers)

          result.left.value.page mustBe ContainerIndicatorPage
        }

        "when container indicator is false" - {
          "and add transport equipment yes/no is unanswered" in {
            val userAnswers = emptyUserAnswers
              .setValue(ContainerIndicatorPage, false)

            val result: EitherType[Option[EquipmentsDomain]] = UserAnswersReader[Option[EquipmentsDomain]](
              EquipmentsAndChargesDomain.equipmentsReads
            ).run(userAnswers)

            result.left.value.page mustBe AddTransportEquipmentYesNoPage
          }
        }
      }
    }

    "chargesReads" - {

      "can be read from user answers" - {
        "when there is no security" in {
          val userAnswers = emptyUserAnswers.setValue(SecurityDetailsTypePage, NoSecurityDetails)

          val expectedResult = None

          val result: EitherType[Option[PaymentMethod]] = UserAnswersReader[Option[PaymentMethod]](
            EquipmentsAndChargesDomain.chargesReads
          ).run(userAnswers)

          result.value mustBe expectedResult
        }

        "when there is security" - {
          "and add payment method yes/no is false" in {
            forAll(arbitrary[SecurityDetailsType](arbitrarySomeSecurityDetailsType)) {
              security =>
                val userAnswers = emptyUserAnswers
                  .setValue(SecurityDetailsTypePage, security)
                  .setValue(AddPaymentMethodYesNoPage, false)

                val expectedResult = None

                val result: EitherType[Option[PaymentMethod]] = UserAnswersReader[Option[PaymentMethod]](
                  EquipmentsAndChargesDomain.chargesReads
                ).run(userAnswers)

                result.value mustBe expectedResult
            }
          }

          "and add payment method yes/no is true" in {
            forAll(arbitrary[SecurityDetailsType](arbitrarySomeSecurityDetailsType), arbitrary[PaymentMethod]) {
              (security, paymentMethod) =>
                val userAnswers = emptyUserAnswers
                  .setValue(SecurityDetailsTypePage, security)
                  .setValue(AddPaymentMethodYesNoPage, true)
                  .setValue(PaymentMethodPage, paymentMethod)

                val expectedResult = Some(paymentMethod)

                val result: EitherType[Option[PaymentMethod]] = UserAnswersReader[Option[PaymentMethod]](
                  EquipmentsAndChargesDomain.chargesReads
                ).run(userAnswers)

                result.value mustBe expectedResult
            }
          }
        }
      }

      "cannot be read from user answers" - {
        "when there is security" - {
          "and add payment method yes/no is unanswered" in {
            forAll(arbitrary[SecurityDetailsType](arbitrarySomeSecurityDetailsType)) {
              security =>
                val userAnswers = emptyUserAnswers
                  .setValue(SecurityDetailsTypePage, security)

                val result: EitherType[Option[PaymentMethod]] = UserAnswersReader[Option[PaymentMethod]](
                  EquipmentsAndChargesDomain.chargesReads
                ).run(userAnswers)

                result.left.value.page mustBe AddPaymentMethodYesNoPage
            }
          }

          "and add payment method yes/no is true" - {
            "and payment method is unanswered" in {
              forAll(arbitrary[SecurityDetailsType](arbitrarySomeSecurityDetailsType)) {
                security =>
                  val userAnswers = emptyUserAnswers
                    .setValue(SecurityDetailsTypePage, security)
                    .setValue(AddPaymentMethodYesNoPage, true)

                  val result: EitherType[Option[PaymentMethod]] = UserAnswersReader[Option[PaymentMethod]](
                    EquipmentsAndChargesDomain.chargesReads
                  ).run(userAnswers)

                  result.left.value.page mustBe PaymentMethodPage
              }
            }
          }
        }
      }
    }
  }

}
