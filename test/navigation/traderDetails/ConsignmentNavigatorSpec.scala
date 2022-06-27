/*
 * Copyright 2022 HM Revenue & Customs
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

package navigation.traderDetails

import base.SpecBase
import controllers.traderDetails.consignment.consignee.{routes => consigneeRoutes}
import controllers.traderDetails.consignment.consignor.contact.{routes => contactRoutes}
import controllers.traderDetails.consignment.consignor.{routes => consignorRoutes}
import controllers.traderDetails.consignment.{routes => consignmentRoutes}
import controllers.traderDetails.{routes => tdRoutes}
import generators.{Generators, PreTaskListUserAnswersGenerator, TraderDetailsUserAnswersGenerator}
import models._
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.sections.{TraderDetailsConsigneeSection, TraderDetailsConsignorContactSection, TraderDetailsConsignorSection}
import pages.traderDetails.consignment._

class ConsignmentNavigatorSpec
    extends SpecBase
    with ScalaCheckPropertyChecks
    with Generators
    with TraderDetailsUserAnswersGenerator
    with PreTaskListUserAnswersGenerator {

  private val navigator = new ConsignmentNavigator

  "Navigator" - {

    "when in NormalMode" - {

      val mode = NormalMode

      "when answers incomplete" - {

        "must go from is approved operator page" - {
          "when Yes selected and security exists" - {
            "to consignor eori yes no page" in {
              forAll(arbitraryPreTaskListAnswersWithoutTirAndWithSecurity) {
                preTaskListAnswers =>
                  val userAnswers = preTaskListAnswers.setValue(ApprovedOperatorPage, true)
                  navigator
                    .nextPage(ApprovedOperatorPage, mode, userAnswers)
                    .mustBe(consignorRoutes.EoriYesNoController.onPageLoad(userAnswers.lrn, mode))
              }
            }
          }

          "when Yes selected and no security exists" - {
            "to more than one consignee page" in {
              forAll(arbitraryPreTaskListAnswersWithoutTirAndWithoutSecurity) {
                preTaskListAnswers =>
                  val userAnswers = preTaskListAnswers.setValue(ApprovedOperatorPage, true)
                  navigator
                    .nextPage(ApprovedOperatorPage, mode, userAnswers)
                    .mustBe(consigneeRoutes.MoreThanOneConsigneeController.onPageLoad(userAnswers.lrn, mode))
              }
            }
          }

          "when No selected and security exists" - {
            "to consignor eori yes no page" in {
              forAll(arbitraryPreTaskListAnswersWithoutTirAndWithSecurity) {
                preTaskListAnswers =>
                  val userAnswers = preTaskListAnswers.setValue(ApprovedOperatorPage, false)
                  navigator
                    .nextPage(ApprovedOperatorPage, mode, userAnswers)
                    .mustBe(consignorRoutes.EoriYesNoController.onPageLoad(userAnswers.lrn, mode))
              }
            }
          }
        }

        "must go from is consignor eori known page" - {
          "when Yes selected" - {
            "to eori page" in {
              forAll(arbitraryPreTaskListAnswersWithoutTirAndWithSecurity) {
                preTaskListAnswers =>
                  val userAnswers = preTaskListAnswers
                    .setValue(ApprovedOperatorPage, false)
                    .setValue(consignor.EoriYesNoPage, true)
                  navigator
                    .nextPage(consignor.EoriYesNoPage, mode, userAnswers)
                    .mustBe(consignorRoutes.EoriController.onPageLoad(userAnswers.lrn, mode))
              }
            }
          }

          "when No selected" - {
            "to name page" in {
              forAll(arbitraryPreTaskListAnswersWithoutTirAndWithSecurity) {
                preTaskListAnswers =>
                  val userAnswers = preTaskListAnswers
                    .setValue(ApprovedOperatorPage, false)
                    .setValue(consignor.EoriYesNoPage, false)
                  navigator
                    .nextPage(consignor.EoriYesNoPage, mode, userAnswers)
                    .mustBe(consignorRoutes.NameController.onPageLoad(userAnswers.lrn, mode))
              }
            }
          }
        }

        "must go from consignor eori page to name page" in {
          forAll(arbitraryPreTaskListAnswersWithoutTirAndWithSecurity, Gen.alphaNumStr) {
            (preTaskListAnswers, eori) =>
              val userAnswers = preTaskListAnswers
                .setValue(ApprovedOperatorPage, false)
                .setValue(consignor.EoriYesNoPage, true)
                .setValue(consignor.EoriPage, eori)
              navigator
                .nextPage(consignor.EoriPage, mode, userAnswers)
                .mustBe(consignorRoutes.NameController.onPageLoad(userAnswers.lrn, mode))
          }
        }

        "must go from consignor name page to address page" in {
          forAll(arbitraryPreTaskListAnswersWithoutTirAndWithSecurity, Gen.alphaNumStr) {
            (preTaskListAnswers, str) =>
              val userAnswers = preTaskListAnswers
                .setValue(ApprovedOperatorPage, false)
                .setValue(consignor.EoriYesNoPage, true)
                .setValue(consignor.EoriPage, str)
                .setValue(consignor.NamePage, str)
              navigator
                .nextPage(consignor.NamePage, mode, userAnswers)
                .mustBe(consignorRoutes.AddressController.onPageLoad(userAnswers.lrn, mode))
          }
        }

        "must go from consignor address page to add contact page" in {
          forAll(arbitraryPreTaskListAnswersWithoutTirAndWithSecurity, Gen.alphaNumStr, arbitrary[Address]) {
            (preTaskListAnswers, str, address) =>
              val userAnswers = preTaskListAnswers
                .setValue(ApprovedOperatorPage, false)
                .setValue(consignor.EoriYesNoPage, true)
                .setValue(consignor.EoriPage, str)
                .setValue(consignor.NamePage, str)
                .setValue(consignor.AddressPage, address)
              navigator
                .nextPage(consignor.AddressPage, mode, userAnswers)
                .mustBe(consignorRoutes.AddContactController.onPageLoad(userAnswers.lrn, mode))
          }
        }

        "must go from add contact page" - {
          "when Yes selected" - {
            "to contact name page" in {
              forAll(arbitraryPreTaskListAnswersWithoutTirAndWithSecurity, Gen.alphaNumStr, arbitrary[Address]) {
                (preTaskListAnswers, str, address) =>
                  val userAnswers = preTaskListAnswers
                    .setValue(ApprovedOperatorPage, false)
                    .setValue(consignor.EoriYesNoPage, true)
                    .setValue(consignor.EoriPage, str)
                    .setValue(consignor.NamePage, str)
                    .setValue(consignor.AddressPage, address)
                    .setValue(consignor.AddContactPage, true)
                  navigator
                    .nextPage(consignor.AddContactPage, mode, userAnswers)
                    .mustBe(contactRoutes.NameController.onPageLoad(userAnswers.lrn, mode))
              }
            }
          }

          "when No selected" - {
            "to more than one consignee page" in {
              forAll(arbitraryPreTaskListAnswersWithoutTirAndWithSecurity, Gen.alphaNumStr, arbitrary[Address]) {
                (preTaskListAnswers, str, address) =>
                  val userAnswers = preTaskListAnswers
                    .setValue(ApprovedOperatorPage, false)
                    .setValue(consignor.EoriYesNoPage, true)
                    .setValue(consignor.EoriPage, str)
                    .setValue(consignor.NamePage, str)
                    .setValue(consignor.AddressPage, address)
                    .setValue(consignor.AddContactPage, false)
                  navigator
                    .nextPage(consignor.AddContactPage, mode, userAnswers)
                    .mustBe(consigneeRoutes.MoreThanOneConsigneeController.onPageLoad(userAnswers.lrn, mode))
              }
            }
          }
        }

        "must go from contact name page to contact telephone number page" in {
          forAll(arbitraryPreTaskListAnswersWithoutTirAndWithSecurity, Gen.alphaNumStr, arbitrary[Address]) {
            (preTaskListAnswers, str, address) =>
              val userAnswers = preTaskListAnswers
                .setValue(ApprovedOperatorPage, false)
                .setValue(consignor.EoriYesNoPage, true)
                .setValue(consignor.EoriPage, str)
                .setValue(consignor.NamePage, str)
                .setValue(consignor.AddressPage, address)
                .setValue(consignor.AddContactPage, true)
                .setValue(consignor.contact.NamePage, str)
              navigator
                .nextPage(consignor.contact.NamePage, mode, userAnswers)
                .mustBe(contactRoutes.TelephoneNumberController.onPageLoad(userAnswers.lrn, mode))
          }
        }

        "must go from contact telephone number page to more than one consignee page" in {
          forAll(arbitraryPreTaskListAnswersWithoutTirAndWithSecurity, Gen.alphaNumStr, arbitrary[Address]) {
            (preTaskListAnswers, str, address) =>
              val userAnswers = preTaskListAnswers
                .setValue(ApprovedOperatorPage, false)
                .setValue(consignor.EoriYesNoPage, true)
                .setValue(consignor.EoriPage, str)
                .setValue(consignor.NamePage, str)
                .setValue(consignor.AddressPage, address)
                .setValue(consignor.AddContactPage, true)
                .setValue(consignor.contact.NamePage, str)
                .setValue(consignor.contact.TelephoneNumberPage, str)
              navigator
                .nextPage(consignor.contact.NamePage, mode, userAnswers)
                .mustBe(consigneeRoutes.MoreThanOneConsigneeController.onPageLoad(userAnswers.lrn, mode))
          }
        }

        "must go from more than one consignee page" - {
          "when yes selected" - {
            "to check your answers page" in {
              forAll(arbitraryPreTaskListAnswersWithoutTirAndWithoutSecurity) {
                preTaskListAnswers =>
                  val userAnswers = preTaskListAnswers
                    .setValue(ApprovedOperatorPage, true)
                    .setValue(consignee.MoreThanOneConsigneePage, false)
                  navigator
                    .nextPage(consignee.MoreThanOneConsigneePage, mode, userAnswers)
                    .mustBe(consigneeRoutes.EoriYesNoController.onPageLoad(userAnswers.lrn, mode))
              }
            }
          }

          "when no selected" - {
            "to eori yes no page" in {
              forAll(arbitraryPreTaskListAnswersWithoutTirAndWithoutSecurity) {
                preTaskListAnswers =>
                  val userAnswers = preTaskListAnswers
                    .setValue(ApprovedOperatorPage, true)
                    .setValue(consignee.MoreThanOneConsigneePage, true)
                  navigator
                    .nextPage(consignee.MoreThanOneConsigneePage, mode, userAnswers)
                    .mustBe(consignmentRoutes.CheckYourAnswersController.onPageLoad(userAnswers.lrn))
              }
            }
          }
        }

        "must go from eori yes no page" - {
          "when Yes selected" - {
            "to eori number page" in {
              forAll(arbitraryPreTaskListAnswersWithoutTirAndWithoutSecurity) {
                preTaskListAnswers =>
                  val userAnswers = preTaskListAnswers
                    .setValue(ApprovedOperatorPage, true)
                    .setValue(consignee.MoreThanOneConsigneePage, false)
                    .setValue(consignee.EoriYesNoPage, true)
                  navigator
                    .nextPage(consignee.EoriYesNoPage, mode, userAnswers)
                    .mustBe(consigneeRoutes.EoriNumberController.onPageLoad(userAnswers.lrn, mode))
              }
            }
          }

          "when No selected" - {
            "to consignee name page" in {
              forAll(arbitraryPreTaskListAnswersWithoutTirAndWithoutSecurity) {
                preTaskListAnswers =>
                  val userAnswers = preTaskListAnswers
                    .setValue(ApprovedOperatorPage, true)
                    .setValue(consignee.MoreThanOneConsigneePage, false)
                    .setValue(consignee.EoriYesNoPage, false)
                  navigator
                    .nextPage(consignee.EoriYesNoPage, mode, userAnswers)
                    .mustBe(consigneeRoutes.NameController.onPageLoad(userAnswers.lrn, mode))
              }
            }
          }
        }

        "must go from Consignee EORI Page to Consignee name page" in {
          forAll(arbitraryPreTaskListAnswersWithoutTirAndWithoutSecurity, Gen.alphaNumStr) {
            (preTaskListAnswers, eori) =>
              val userAnswers = preTaskListAnswers
                .setValue(ApprovedOperatorPage, true)
                .setValue(consignee.MoreThanOneConsigneePage, false)
                .setValue(consignee.EoriYesNoPage, false)
                .setValue(consignee.EoriNumberPage, eori)
              navigator
                .nextPage(consignee.EoriNumberPage, mode, userAnswers)
                .mustBe(consigneeRoutes.NameController.onPageLoad(userAnswers.lrn, mode))
          }
        }

        "must go from Consignee name page to Consignee address page" in {
          forAll(arbitraryPreTaskListAnswersWithoutTirAndWithoutSecurity, Gen.alphaNumStr) {
            (preTaskListAnswers, str) =>
              val userAnswers = preTaskListAnswers
                .setValue(ApprovedOperatorPage, true)
                .setValue(consignee.MoreThanOneConsigneePage, false)
                .setValue(consignee.EoriYesNoPage, false)
                .setValue(consignee.EoriNumberPage, str)
                .setValue(consignee.NamePage, str)
              navigator
                .nextPage(consignee.NamePage, mode, userAnswers)
                .mustBe(consigneeRoutes.AddressController.onPageLoad(userAnswers.lrn, mode))
          }
        }

        "must go from Consignee address page to Consignment Check Your Answers page page" in {
          forAll(arbitraryPreTaskListAnswersWithoutTirAndWithoutSecurity, Gen.alphaNumStr, arbitrary[Address]) {
            (preTaskListAnswers, str, address) =>
              val userAnswers = preTaskListAnswers
                .setValue(ApprovedOperatorPage, true)
                .setValue(consignee.MoreThanOneConsigneePage, false)
                .setValue(consignee.EoriYesNoPage, false)
                .setValue(consignee.EoriNumberPage, str)
                .setValue(consignee.NamePage, str)
                .setValue(consignee.AddressPage, address)
              navigator
                .nextPage(consignee.AddressPage, mode, userAnswers)
                .mustBe(consignmentRoutes.CheckYourAnswersController.onPageLoad(userAnswers.lrn))
          }
        }
      }

      "when answers complete" - {

        "must go from approved operator page" - {
          "when Yes selected" - {
            "to check Your Answers page" in {
              forAll(arbitraryPreTaskListAnswersWithoutTir) {
                preTaskListAnswers =>
                  forAll(arbitraryConsignmentAnswers(preTaskListAnswers)) {
                    answers =>
                      val userAnswers = answers.setValue(ApprovedOperatorPage, true)
                      navigator
                        .nextPage(ApprovedOperatorPage, mode, userAnswers)
                        .mustBe(consignmentRoutes.CheckYourAnswersController.onPageLoad(userAnswers.lrn))
                  }
              }
            }
          }

          "when No selected" - {
            "to consignor eori page" in {
              forAll(arbitraryPreTaskListAnswersWithoutTir) {
                preTaskListAnswers =>
                  forAll(arbitraryConsignmentAnswers(preTaskListAnswers)) {
                    answers =>
                      val userAnswers = answers
                        .removeValue(TraderDetailsConsignorSection)
                        .setValue(ApprovedOperatorPage, false)
                      navigator
                        .nextPage(ApprovedOperatorPage, mode, userAnswers)
                        .mustBe(consignorRoutes.EoriYesNoController.onPageLoad(userAnswers.lrn, mode))
                  }
              }
            }
          }
        }

        "must go from consignor EoriYesNo page " - {
          "to check your answers page when no is selected" in {
            forAll(arbitraryPreTaskListAnswers) {
              preTaskListAnswers =>
                forAll(arbitraryConsignmentAnswersWithConsignor(preTaskListAnswers)) {
                  answers =>
                    val userAnswers = answers.setValue(consignor.EoriYesNoPage, false)
                    navigator
                      .nextPage(consignor.EoriYesNoPage, mode, userAnswers)
                      .mustBe(consignmentRoutes.CheckYourAnswersController.onPageLoad(userAnswers.lrn))
                }
            }
          }

          "to Consignee Eori page when yes is selected" in {
            forAll(arbitraryPreTaskListAnswers) {
              preTaskListAnswers =>
                forAll(arbitraryConsignmentAnswersWithConsignor(preTaskListAnswers)) {
                  answers =>
                    val userAnswers = answers
                      .removeValue(consignor.EoriPage)
                      .setValue(consignor.EoriYesNoPage, true)
                    navigator
                      .nextPage(consignor.EoriYesNoPage, mode, userAnswers)
                      .mustBe(consignorRoutes.EoriController.onPageLoad(userAnswers.lrn, mode))
                }
            }
          }
        }

        "must go from consignor eori page to check your answers page" in {
          forAll(arbitraryPreTaskListAnswers) {
            preTaskListAnswers =>
              forAll(arbitraryConsignmentAnswersWithConsignor(preTaskListAnswers)) {
                answers =>
                  navigator
                    .nextPage(consignor.EoriPage, mode, answers)
                    .mustBe(consignmentRoutes.CheckYourAnswersController.onPageLoad(answers.lrn))
              }
          }
        }

        "must go from consignor name page to check your answers page" in {
          forAll(arbitraryPreTaskListAnswers) {
            preTaskListAnswers =>
              forAll(arbitraryConsignmentAnswersWithConsignor(preTaskListAnswers)) {
                answers =>
                  navigator
                    .nextPage(consignor.NamePage, mode, answers)
                    .mustBe(consignmentRoutes.CheckYourAnswersController.onPageLoad(answers.lrn))
              }
          }
        }

        "must go from consignor address page to check your answers page" in {
          forAll(arbitraryPreTaskListAnswers) {
            preTaskListAnswers =>
              forAll(arbitraryConsignmentAnswersWithConsignor(preTaskListAnswers)) {
                answers =>
                  navigator
                    .nextPage(consignor.AddressPage, mode, answers)
                    .mustBe(consignmentRoutes.CheckYourAnswersController.onPageLoad(answers.lrn))
              }
          }
        }

        "must go from add contact page" - {
          "when No selected" - {
            "to check your answers page" in {
              forAll(arbitraryPreTaskListAnswers) {
                preTaskListAnswers =>
                  forAll(arbitraryConsignmentAnswersWithConsignor(preTaskListAnswers)) {
                    answers =>
                      val userAnswers = answers.setValue(consignor.AddContactPage, false)
                      navigator
                        .nextPage(consignor.AddContactPage, mode, userAnswers)
                        .mustBe(consignmentRoutes.CheckYourAnswersController.onPageLoad(answers.lrn))
                  }
              }
            }
          }

          "when Yes selected" - {
            "to contact name page" in {
              forAll(arbitraryPreTaskListAnswers) {
                preTaskListAnswers =>
                  forAll(arbitraryConsignmentAnswersWithConsignor(preTaskListAnswers)) {
                    answers =>
                      val userAnswers = answers
                        .setValue(consignor.AddContactPage, true)
                        .removeValue(TraderDetailsConsignorContactSection)
                      navigator
                        .nextPage(consignor.AddContactPage, mode, userAnswers)
                        .mustBe(contactRoutes.NameController.onPageLoad(userAnswers.lrn, mode))
                  }
              }
            }
          }
        }

        "must go from contact name page" - {
          "when telephone number exists" - {
            "to check your answers page" in {
              forAll(arbitraryPreTaskListAnswers) {
                preTaskListAnswers =>
                  forAll(arbitraryConsignmentAnswersWithConsignor(preTaskListAnswers), Gen.alphaNumStr) {
                    (answers, str) =>
                      val userAnswers = answers
                        .setValue(consignor.AddContactPage, true)
                        .setValue(consignor.contact.TelephoneNumberPage, str)
                        .setValue(consignor.contact.NamePage, str)
                      navigator
                        .nextPage(consignor.contact.NamePage, mode, userAnswers)
                        .mustBe(consignmentRoutes.CheckYourAnswersController.onPageLoad(userAnswers.lrn))
                  }
              }
            }
          }

          "when no telephone number exists" - {
            "to contact telephone number page" in {
              forAll(arbitraryPreTaskListAnswers) {
                preTaskListAnswers =>
                  forAll(arbitraryConsignmentAnswersWithConsignor(preTaskListAnswers), Gen.alphaNumStr) {
                    (answers, name) =>
                      val userAnswers = answers
                        .setValue(consignor.AddContactPage, true)
                        .removeValue(consignor.contact.TelephoneNumberPage)
                        .setValue(consignor.contact.NamePage, name)
                      navigator
                        .nextPage(consignor.contact.NamePage, mode, userAnswers)
                        .mustBe(contactRoutes.TelephoneNumberController.onPageLoad(userAnswers.lrn, mode))
                  }
              }
            }
          }
        }

        "must go from contact telephone number page to check your answers page" in {
          forAll(arbitraryPreTaskListAnswers) {
            preTaskListAnswers =>
              forAll(arbitraryConsignmentAnswersWithConsignor(preTaskListAnswers), Gen.alphaNumStr) {
                (answers, str) =>
                  val userAnswers = answers
                    .setValue(consignor.AddContactPage, true)
                    .setValue(consignor.contact.NamePage, str)
                    .setValue(consignor.contact.TelephoneNumberPage, str)
                  navigator
                    .nextPage(consignor.contact.TelephoneNumberPage, mode, userAnswers)
                    .mustBe(consignmentRoutes.CheckYourAnswersController.onPageLoad(userAnswers.lrn))
              }
          }
        }

        "must go from more than one consignee page " - {
          "to check your answers page when yes is selected" in {
            forAll(arbitraryPreTaskListAnswers) {
              preTaskListAnswers =>
                forAll(arbitraryConsignmentAnswers(preTaskListAnswers)) {
                  answers =>
                    val userAnswers = answers.setValue(consignee.MoreThanOneConsigneePage, true)
                    navigator
                      .nextPage(consignee.MoreThanOneConsigneePage, mode, userAnswers)
                      .mustBe(consignmentRoutes.CheckYourAnswersController.onPageLoad(userAnswers.lrn))
                }
            }
          }

          "to Consignee EoriYesNo page when no is selected and we have no Consignee Data" in {
            forAll(arbitraryPreTaskListAnswers) {
              preTaskListAnswers =>
                forAll(arbitraryConsignmentAnswers(preTaskListAnswers)) {
                  answers =>
                    val userAnswers = answers
                      .removeValue(TraderDetailsConsigneeSection)
                      .setValue(consignee.MoreThanOneConsigneePage, false)
                    navigator
                      .nextPage(consignee.MoreThanOneConsigneePage, mode, userAnswers)
                      .mustBe(consigneeRoutes.EoriYesNoController.onPageLoad(userAnswers.lrn, mode))
                }
            }
          }
        }

        "must go from more than one consignee EoriYesNo page " - {
          "to check your answers page when no is selected" in {
            forAll(arbitraryPreTaskListAnswers) {
              preTaskListAnswers =>
                forAll(arbitraryConsignmentAnswersWithOneConsignee(preTaskListAnswers)) {
                  answers =>
                    val userAnswers = answers.setValue(consignee.EoriYesNoPage, false)
                    navigator
                      .nextPage(consignee.EoriYesNoPage, mode, userAnswers)
                      .mustBe(consignmentRoutes.CheckYourAnswersController.onPageLoad(userAnswers.lrn))
                }
            }
          }

          "to Consignee Eori page when yes is selected" in {
            forAll(arbitraryPreTaskListAnswers) {
              preTaskListAnswers =>
                forAll(arbitraryConsignmentAnswersWithOneConsignee(preTaskListAnswers)) {
                  answers =>
                    val userAnswers = answers
                      .removeValue(consignee.EoriNumberPage)
                      .setValue(consignee.EoriYesNoPage, true)
                    navigator
                      .nextPage(consignee.EoriYesNoPage, mode, userAnswers)
                      .mustBe(consigneeRoutes.EoriNumberController.onPageLoad(userAnswers.lrn, mode))
                }
            }
          }
        }

        "must go from consignee eori page to check your answers page" in {
          forAll(arbitraryPreTaskListAnswers) {
            preTaskListAnswers =>
              forAll(arbitraryConsignmentAnswersWithOneConsignee(preTaskListAnswers)) {
                answers =>
                  navigator
                    .nextPage(consignee.EoriNumberPage, mode, answers)
                    .mustBe(consignmentRoutes.CheckYourAnswersController.onPageLoad(answers.lrn))
              }
          }
        }

        "must go from consignee name page to check your answers page" in {
          forAll(arbitraryPreTaskListAnswers) {
            preTaskListAnswers =>
              forAll(arbitraryConsignmentAnswersWithOneConsignee(preTaskListAnswers)) {
                answers =>
                  navigator
                    .nextPage(consignee.NamePage, mode, answers)
                    .mustBe(consignmentRoutes.CheckYourAnswersController.onPageLoad(answers.lrn))
              }
          }
        }

        "must go from consignee address page to check your answers page" in {
          forAll(arbitraryPreTaskListAnswers) {
            preTaskListAnswers =>
              forAll(arbitraryConsignmentAnswersWithOneConsignee(preTaskListAnswers)) {
                answers =>
                  navigator
                    .nextPage(consignee.AddressPage, mode, answers)
                    .mustBe(consignmentRoutes.CheckYourAnswersController.onPageLoad(answers.lrn))
              }
          }
        }
      }
    }

    "when in CheckMode" - {

      val mode = CheckMode

      "when answers complete" - {
        "must redirect to check your answers" in {
          val userAnswers = emptyUserAnswers
          navigator
            .checkYourAnswersRoute(mode, emptyUserAnswers)
            .mustBe(tdRoutes.CheckYourAnswersController.onPageLoad(userAnswers.lrn))
        }
      }
    }
  }
}
