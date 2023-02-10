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

package utils.cyaHelpers.transport

import base.SpecBase
import controllers.transport.authorisationsAndLimit.limit.{routes => limitRoutes}
import controllers.transport.carrierDetails.{routes => carrierDetailsRoutes}
import controllers.transport.carrierDetails.contact.{routes => carrierDetailsContactRoutes}
import controllers.transport.equipment.index.{routes => equipmentRoutes}
import controllers.transport.equipment.{routes => equipmentsRoutes}
import generators.Generators
import models.Mode
import models.domain.UserAnswersReader
import models.journeyDomain.transport.equipment.EquipmentDomain
import models.transport.equipment.PaymentMethod
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.transport.authorisationsAndLimit.limit.LimitDatePage
import pages.transport.carrierDetails.contact.{NamePage, TelephoneNumberPage}
import pages.transport.carrierDetails.{AddContactYesNoPage, IdentificationNumberPage}
import pages.transport.equipment.{AddPaymentMethodYesNoPage, AddTransportEquipmentYesNoPage, PaymentMethodPage}
import pages.transport.preRequisites.ContainerIndicatorPage

import java.time.LocalDate

class TransportAnswersHelperSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  "TransportAnswersHelper" - {

    "limitDate" - {
      "must return None" - {
        s"when $LimitDatePage undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new TransportAnswersHelper(emptyUserAnswers, mode)
              val result = helper.limitDate
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        s"when $LimitDatePage defined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val limitDate = LocalDate.of(2000: Int, 1: Int, 8: Int)
              val answers   = emptyUserAnswers.setValue(LimitDatePage, limitDate)
              val helper    = new TransportAnswersHelper(answers, mode)
              val result    = helper.limitDate.get

              result.key.value mustBe "Limit date"
              result.value.value mustBe "8 January 2000"
              val actions = result.actions.get.items
              actions.size mustBe 1
              val action = actions.head
              action.content.value mustBe "Change"
              action.href mustBe limitRoutes.LimitDateController.onPageLoad(answers.lrn, mode).url
              action.visuallyHiddenText.get mustBe "limit date"
              action.id mustBe "change-limit-date"
          }
        }
      }
    }

    "eoriNumber" - {
      "must return None" - {
        s"when $IdentificationNumberPage undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new TransportAnswersHelper(emptyUserAnswers, mode)
              val result = helper.eoriNumber
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        s"when $IdentificationNumberPage defined" in {
          forAll(arbitrary[Mode], nonEmptyString) {
            (mode, eoriNumber) =>
              val answers = emptyUserAnswers.setValue(IdentificationNumberPage, eoriNumber)
              val helper  = new TransportAnswersHelper(answers, mode)
              val result  = helper.eoriNumber.get

              result.key.value mustBe "EORI number or Trader Identification Number (TIN)"
              result.value.value mustBe eoriNumber
              val actions = result.actions.get.items
              actions.size mustBe 1
              val action = actions.head
              action.content.value mustBe "Change"
              action.href mustBe carrierDetailsRoutes.IdentificationNumberController.onPageLoad(answers.lrn, mode).url
              action.visuallyHiddenText.get mustBe "carrier EORI number or Trader Identification Number (TIN)"
              action.id mustBe "change-eori-number"
          }
        }
      }
    }

    "addContact" - {
      "must return None" - {
        s"when $AddContactYesNoPage undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new TransportAnswersHelper(emptyUserAnswers, mode)
              val result = helper.addContactPerson
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        s"when $AddContactYesNoPage defined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val answers = emptyUserAnswers.setValue(AddContactYesNoPage, true)
              val helper  = new TransportAnswersHelper(answers, mode)
              val result  = helper.addContactPerson.get

              result.key.value mustBe "Do you want to add a contact for the carrier?"
              result.value.value mustBe "Yes"
              val actions = result.actions.get.items
              actions.size mustBe 1
              val action = actions.head
              action.content.value mustBe "Change"
              action.href mustBe carrierDetailsRoutes.AddContactYesNoController.onPageLoad(answers.lrn, mode).url
              action.visuallyHiddenText.get mustBe "if you want to add a contact for the carrier"
              action.id mustBe "change-add-contact"
          }
        }
      }
    }

    "contactName" - {
      "must return None" - {
        s"when $NamePage undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new TransportAnswersHelper(emptyUserAnswers, mode)
              val result = helper.contactName
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        s"when $NamePage defined" in {
          forAll(arbitrary[Mode], nonEmptyString) {
            (mode, contactName) =>
              val answers = emptyUserAnswers.setValue(NamePage, contactName)
              val helper  = new TransportAnswersHelper(answers, mode)
              val result  = helper.contactName.get

              result.key.value mustBe "Contact name"
              result.value.value mustBe contactName
              val actions = result.actions.get.items
              actions.size mustBe 1
              val action = actions.head
              action.content.value mustBe "Change"
              action.href mustBe carrierDetailsContactRoutes.NameController.onPageLoad(answers.lrn, mode).url
              action.visuallyHiddenText.get mustBe "carrier contact name"
              action.id mustBe "change-contact-name"
          }
        }
      }
    }

    "contactTelephoneNumber" - {
      "must return None" - {
        s"when $TelephoneNumberPage undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new TransportAnswersHelper(emptyUserAnswers, mode)
              val result = helper.contactTelephoneNumber
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        s"when $TelephoneNumberPage defined" in {
          forAll(arbitrary[Mode], nonEmptyString) {
            (mode, contactNumber) =>
              val answers = emptyUserAnswers.setValue(TelephoneNumberPage, contactNumber)
              val helper  = new TransportAnswersHelper(answers, mode)
              val result  = helper.contactTelephoneNumber.get

              result.key.value mustBe "Phone number"
              result.value.value mustBe contactNumber
              val actions = result.actions.get.items
              actions.size mustBe 1
              val action = actions.head
              action.content.value mustBe "Change"
              action.href mustBe carrierDetailsContactRoutes.TelephoneNumberController.onPageLoad(answers.lrn, mode).url
              action.visuallyHiddenText.get mustBe "carrier phone number"
              action.id mustBe "change-contact-telephone-number"
          }
        }
      }
    }

    "addEquipment" - {
      "must return None" - {
        s"when $AddTransportEquipmentYesNoPage undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new TransportAnswersHelper(emptyUserAnswers, mode)
              val result = helper.addEquipment
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        s"when $AddTransportEquipmentYesNoPage defined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val answers = emptyUserAnswers.setValue(AddTransportEquipmentYesNoPage, true)
              val helper  = new TransportAnswersHelper(answers, mode)
              val result  = helper.addEquipment.get

              result.key.value mustBe "Do you want to add any transport equipment?"
              result.value.value mustBe "Yes"
              val actions = result.actions.get.items
              actions.size mustBe 1
              val action = actions.head
              action.content.value mustBe "Change"
              action.href mustBe equipmentsRoutes.AddTransportEquipmentYesNoController.onPageLoad(answers.lrn, mode).url
              action.visuallyHiddenText.get mustBe "if you want to add any transport equipment"
              action.id mustBe "change-add-equipment"
          }
        }
      }
    }

    "equipment" - {
      "must return None" - {
        "when equipment is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new TransportAnswersHelper(emptyUserAnswers, mode)
              val result = helper.equipment(index)
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when equipment is  defined and container id is undefined" in {
          val initialUserAnswers = emptyUserAnswers
            .setValue(ContainerIndicatorPage, false)

          forAll(arbitraryEquipmentAnswers(initialUserAnswers, index), arbitrary[Mode]) {
            (userAnswers, mode) =>
              val helper = new TransportAnswersHelper(userAnswers, mode)
              val result = helper.equipment(index).get

              result.key.value mustBe "Transport equipment 1"
              result.value.value mustBe "Transport equipment 1"
              val actions = result.actions.get.items
              actions.size mustBe 1
              val action = actions.head
              action.content.value mustBe "Change"
              action.href mustBe equipmentRoutes.EquipmentAnswersController.onPageLoad(userAnswers.lrn, mode, index).url
              action.visuallyHiddenText.get mustBe "transport equipment 1"
              action.id mustBe "change-transport-equipment-1"
          }
        }

        "when equipment is  defined and container id is defined" in {
          val initialUserAnswers = emptyUserAnswers
            .setValue(ContainerIndicatorPage, true)

          forAll(arbitraryEquipmentAnswers(initialUserAnswers, index), arbitrary[Mode]) {
            (userAnswers, mode) =>
              val equipment = UserAnswersReader[EquipmentDomain](EquipmentDomain.userAnswersReader(index)).run(userAnswers).value

              val helper = new TransportAnswersHelper(userAnswers, mode)
              val result = helper.equipment(index).get

              result.key.value mustBe "Transport equipment 1"
              result.value.value mustBe s"Transport equipment 1 - container ${equipment.containerId.get}"
              val actions = result.actions.get.items
              actions.size mustBe 1
              val action = actions.head
              action.content.value mustBe "Change"
              action.href mustBe equipmentRoutes.EquipmentAnswersController.onPageLoad(userAnswers.lrn, mode, index).url
              action.visuallyHiddenText.get mustBe "transport equipment 1"
              action.id mustBe "change-transport-equipment-1"
          }
        }
      }
    }

    "addPaymentMethod" - {
      "must return None" - {
        s"when $AddPaymentMethodYesNoPage undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new TransportAnswersHelper(emptyUserAnswers, mode)
              val result = helper.addPaymentMethod
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        s"when $AddPaymentMethodYesNoPage defined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val answers = emptyUserAnswers.setValue(AddPaymentMethodYesNoPage, true)
              val helper  = new TransportAnswersHelper(answers, mode)
              val result  = helper.addPaymentMethod.get

              result.key.value mustBe "Do you want to add a method of payment for transport charges?"
              result.value.value mustBe "Yes"
              val actions = result.actions.get.items
              actions.size mustBe 1
              val action = actions.head
              action.content.value mustBe "Change"
              action.href mustBe equipmentsRoutes.AddPaymentMethodYesNoController.onPageLoad(answers.lrn, mode).url
              action.visuallyHiddenText.get mustBe "if you want to add a method of payment for transport charges"
              action.id mustBe "change-add-payment-method"
          }
        }
      }
    }

    "paymentMethod" - {
      "must return None" - {
        s"when $PaymentMethodPage undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new TransportAnswersHelper(emptyUserAnswers, mode)
              val result = helper.paymentMethod
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        s"when $PaymentMethodPage defined" in {
          forAll(arbitrary[Mode], arbitrary[PaymentMethod]) {
            (mode, paymentMethod) =>
              val answers = emptyUserAnswers.setValue(PaymentMethodPage, paymentMethod)
              val helper  = new TransportAnswersHelper(answers, mode)
              val result  = helper.paymentMethod.get

              result.key.value mustBe "Payment method"
              val key = s"transport.equipment.paymentMethod.$paymentMethod"
              messages.isDefinedAt(key) mustBe true
              result.value.value mustBe messages(key)
              val actions = result.actions.get.items
              actions.size mustBe 1
              val action = actions.head
              action.content.value mustBe "Change"
              action.href mustBe equipmentsRoutes.PaymentMethodController.onPageLoad(answers.lrn, mode).url
              action.visuallyHiddenText.get mustBe "payment method for transport charges"
              action.id mustBe "change-payment-method"
          }
        }
      }
    }

  }
}
