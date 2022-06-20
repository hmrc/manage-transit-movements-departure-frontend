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
import controllers.routes
import controllers.traderDetails.{routes => tdRoutes}
import controllers.traderDetails.consignment.consignor.{routes => consignorRoutes}
import controllers.traderDetails.consignment.consignor.contact.{routes => contactRoutes}
import controllers.traderDetails.consignment.consignee.{routes => consigneeRoutes}
import controllers.traderDetails.consignment.{routes => consignmentRoutes}
import generators.{Generators, TraderDetailsUserAnswersGenerator}
import models.SecurityDetailsType.{EntrySummaryDeclarationSecurityDetails, NoSecurityDetails}
import models._
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages._
import pages.preTaskList.SecurityDetailsTypePage
import pages.traderDetails.consignment._

class ConsignmentNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks with Generators with TraderDetailsUserAnswersGenerator {

  private val navigator = new ConsignmentNavigator

  "Navigator" - {
    "must go from a page that doesn't exist in the route map" - {
      case object UnknownPage extends Page

      "to session expired" in {
        forAll(arbitrary[Mode]) {
          mode =>
            navigator
              .nextPage(UnknownPage, mode, emptyUserAnswers)
              .mustBe(routes.SessionExpiredController.onPageLoad())
        }
      }
    }

    "when in NormalMode" - {

      val mode = NormalMode

      "when answers incomplete" - {

        "must go from is approved operator page" - {
          "when Yes selected and security exists" - {
            "to consignor eori yes no page page" in {
              val userAnswers = emptyUserAnswers
                .setValue(ApprovedOperatorPage, true)
                .setValue(SecurityDetailsTypePage, EntrySummaryDeclarationSecurityDetails)
              navigator
                .nextPage(ApprovedOperatorPage, mode, userAnswers)
                .mustBe(consignorRoutes.EoriYesNoController.onPageLoad(userAnswers.lrn, mode))
            }
          }

          "when Yes selected and no security exists" - {
            "to consignor eori yes no page page" in {
              val userAnswers = emptyUserAnswers
                .setValue(ApprovedOperatorPage, true)
                .setValue(SecurityDetailsTypePage, NoSecurityDetails)
              navigator
                .nextPage(ApprovedOperatorPage, mode, userAnswers)
                .mustBe(consigneeRoutes.MoreThanOneConsigneeController.onPageLoad(userAnswers.lrn, mode))
            }
          }

          "when No selected and security exists" - {
            "to how many consignees page" in {
              val userAnswers = emptyUserAnswers
                .setValue(SecurityDetailsTypePage, EntrySummaryDeclarationSecurityDetails)
                .setValue(ApprovedOperatorPage, false)
              navigator
                .nextPage(ApprovedOperatorPage, mode, userAnswers)
                .mustBe(consignorRoutes.EoriYesNoController.onPageLoad(userAnswers.lrn, mode))
            }
          }

          "when nothing selected" - {
            "to session expired" in {
              navigator
                .nextPage(ApprovedOperatorPage, mode, emptyUserAnswers)
                .mustBe(routes.SessionExpiredController.onPageLoad())
            }
          }
        }

        "must go from is consignor eori known page" - {
          "when Yes selected" - {
            "to eori page" in {
              val userAnswers = emptyUserAnswers.setValue(consignor.EoriYesNoPage, true)
              navigator
                .nextPage(consignor.EoriYesNoPage, mode, userAnswers)
                .mustBe(consignorRoutes.EoriController.onPageLoad(userAnswers.lrn, mode))
            }
          }

          "when No selected" - {
            "to name page" in {
              val userAnswers = emptyUserAnswers.setValue(consignor.EoriYesNoPage, false)
              navigator
                .nextPage(consignor.EoriYesNoPage, mode, userAnswers)
                .mustBe(consignorRoutes.NameController.onPageLoad(emptyUserAnswers.lrn, mode))
            }
          }

          "when nothing selected" - {
            "to session expired" in {
              navigator
                .nextPage(consignor.EoriYesNoPage, mode, emptyUserAnswers)
                .mustBe(routes.SessionExpiredController.onPageLoad())
            }
          }
        }

        "must go from consignor eori page to name page" in {
          navigator
            .nextPage(consignor.EoriPage, mode, emptyUserAnswers)
            .mustBe(consignorRoutes.NameController.onPageLoad(emptyUserAnswers.lrn, mode))
        }

        "must go from consignor name page to address page" in {
          navigator
            .nextPage(consignor.NamePage, mode, emptyUserAnswers)
            .mustBe(consignorRoutes.AddressController.onPageLoad(emptyUserAnswers.lrn, mode))
        }

        "must go from consignor address page to add contact page" in {
          navigator
            .nextPage(consignor.AddressPage, mode, emptyUserAnswers)
            .mustBe(consignorRoutes.AddContactController.onPageLoad(emptyUserAnswers.lrn, mode))
        }

        "must go from add contact page" - {
          "when Yes selected" - {
            "to contact name page" in {
              val userAnswers = emptyUserAnswers.setValue(consignor.AddContactPage, true)
              navigator
                .nextPage(consignor.AddContactPage, mode, userAnswers)
                .mustBe(contactRoutes.NameController.onPageLoad(emptyUserAnswers.lrn, mode))
            }
          }

          "when No selected" - {
            "to more than one consignee page" in {
              val userAnswers = emptyUserAnswers.setValue(consignor.AddContactPage, false)
              navigator
                .nextPage(consignor.AddContactPage, mode, userAnswers)
                .mustBe(consigneeRoutes.MoreThanOneConsigneeController.onPageLoad(userAnswers.lrn, mode))
            }
          }

          "when nothing selected" - {
            "to session expired" in {
              navigator
                .nextPage(consignor.AddContactPage, mode, emptyUserAnswers)
                .mustBe(routes.SessionExpiredController.onPageLoad())
            }
          }
        }

        "must go from contact name page to contact telephone number page" in {
          navigator
            .nextPage(consignor.contact.NamePage, mode, emptyUserAnswers)
            .mustBe(contactRoutes.TelephoneNumberController.onPageLoad(emptyUserAnswers.lrn, mode))
        }

        "must go from contact telephone number page to more than one consignee page" in {
          navigator
            .nextPage(consignor.contact.TelephoneNumberPage, mode, emptyUserAnswers)
            .mustBe(consigneeRoutes.MoreThanOneConsigneeController.onPageLoad(emptyUserAnswers.lrn, mode))
        }

        "must go from more than one consignee page" - {
          "when yes selected" - {
            "to check your answers page" in {
              val userAnswers = emptyUserAnswers.setValue(consignee.MoreThanOneConsigneePage, true)
              navigator
                .nextPage(consignee.MoreThanOneConsigneePage, mode, userAnswers)
                .mustBe(consignmentRoutes.CheckYourAnswersController.onPageLoad(userAnswers.lrn))
            }
          }

          "when no selected" - {
            "to eori yes no page" in {
              val userAnswers = emptyUserAnswers.setValue(consignee.MoreThanOneConsigneePage, false)
              navigator
                .nextPage(consignee.MoreThanOneConsigneePage, mode, userAnswers)
                .mustBe(consigneeRoutes.EoriYesNoController.onPageLoad(userAnswers.lrn, mode))
            }
          }
        }

        "must go from eori yes no page" - {
          "when Yes selected" - {
            "to eori number page" in {
              val userAnswers = emptyUserAnswers.setValue(consignee.EoriYesNoPage, true)
              navigator
                .nextPage(consignee.EoriYesNoPage, mode, userAnswers)
                .mustBe(consigneeRoutes.EoriNumberController.onPageLoad(emptyUserAnswers.lrn, mode))
            }
          }

          "when No selected" - {
            "to consignee name page" in {
              val userAnswers = emptyUserAnswers.setValue(consignee.EoriYesNoPage, false)
              navigator
                .nextPage(consignee.EoriYesNoPage, mode, userAnswers)
                .mustBe(consigneeRoutes.NameController.onPageLoad(emptyUserAnswers.lrn, mode))
            }
          }
        }

        "must go from Consignee EORI Page to Consignee name page" in {
          val userAnswers = emptyUserAnswers.setValue(consignee.EoriNumberPage, "GB1234567")
          navigator
            .nextPage(consignee.EoriNumberPage, mode, userAnswers)
            .mustBe(consigneeRoutes.NameController.onPageLoad(emptyUserAnswers.lrn, mode))
        }

        "must go from Consignee name page to Consignee address page" in {
          val userAnswers = emptyUserAnswers.setValue(consignee.NamePage, "TestName")
          navigator
            .nextPage(consignee.NamePage, mode, userAnswers)
            .mustBe(consigneeRoutes.AddressController.onPageLoad(emptyUserAnswers.lrn, mode))
        }

        "must go from Consignee address page to Consignment Check Your Answers page page" in {
          navigator
            .nextPage(consignee.AddressPage, mode, emptyUserAnswers)
            .mustBe(consignmentRoutes.CheckYourAnswersController.onPageLoad(emptyUserAnswers.lrn))
        }
      }

      "when answers complete" - {

        "must go from approved operator page" - {
          "when Yes selected" - {
            "to check Your Answers page" in {
              forAll(arbitraryTraderDetailsConsignmentAnswersWithConsignorAndConsignee) {
                answers =>
                  val userAnswers = answers
                    .setValue(ApprovedOperatorPage, true)
                    .setValue(SecurityDetailsTypePage, NoSecurityDetails)
                  navigator
                    .nextPage(ApprovedOperatorPage, mode, userAnswers)
                    .mustBe(consignmentRoutes.CheckYourAnswersController.onPageLoad(userAnswers.lrn))
              }
            }
          }

          "when No selected" - {
            "to consignor eori page" in {
              forAll(arbitraryTraderDetailsConsignmentAnswersWithConsignorAndConsignee) {
                answers =>
                  val userAnswers = answers
                    .setValue(ApprovedOperatorPage, false)
                    .setValue(SecurityDetailsTypePage, NoSecurityDetails)
                    .removeValue(consignor.EoriYesNoPage)

                  navigator
                    .nextPage(ApprovedOperatorPage, mode, userAnswers)
                    .mustBe(consignorRoutes.EoriYesNoController.onPageLoad(userAnswers.lrn, mode))
              }
            }
          }
        }

        "must go from more than one consignor EoriYesNo page " - {
          "to check your answers page when no is selected" in {
            forAll(arbitraryTraderDetailsConsignmentAnswersWithConsignorAndConsignee) {
              answers =>
                val userAnswers = answers.setValue(consignor.EoriYesNoPage, false)
                navigator
                  .nextPage(consignor.EoriYesNoPage, mode, userAnswers)
                  .mustBe(consignmentRoutes.CheckYourAnswersController.onPageLoad(userAnswers.lrn))
            }
          }

          "to Consignee Eori page when yes is selected" in {
            forAll(arbitraryTraderDetailsConsignmentAnswersWithConsignorAndConsignee) {
              answers =>
                val userAnswers = answers
                  .setValue(consignor.EoriYesNoPage, true)
                  .removeValue(consignor.EoriPage)

                navigator
                  .nextPage(consignor.EoriYesNoPage, mode, userAnswers)
                  .mustBe(consignorRoutes.EoriController.onPageLoad(userAnswers.lrn, mode))
            }
          }
        }

        "must go from consignor eori page to check your answers page" in {
          forAll(arbitraryTraderDetailsConsignmentAnswersWithConsignorAndConsignee) {
            answers =>
              navigator
                .nextPage(consignor.EoriPage, mode, answers)
                .mustBe(consignmentRoutes.CheckYourAnswersController.onPageLoad(answers.lrn))
          }
        }

        "must go from consignor name page to check your answers page" in {
          forAll(arbitraryTraderDetailsConsignmentAnswersWithConsignorAndConsignee) {
            answers =>
              navigator
                .nextPage(consignor.NamePage, mode, answers)
                .mustBe(consignmentRoutes.CheckYourAnswersController.onPageLoad(answers.lrn))
          }
        }

        "must go from consignor address page to check your answers page" in {
          forAll(arbitraryTraderDetailsConsignmentAnswersWithConsignorAndConsignee) {
            answers =>
              navigator
                .nextPage(consignor.AddressPage, mode, answers)
                .mustBe(consignmentRoutes.CheckYourAnswersController.onPageLoad(answers.lrn))
          }
        }

        "must go from add contact page" - {
          "when No selected" - {
            "to check your answers page" in {
              forAll(arbitraryTraderDetailsConsignmentAnswersWithConsignorAndConsignee) {
                answers =>
                  val userAnswers = answers.setValue(consignor.AddContactPage, false)
                  navigator
                    .nextPage(consignor.AddContactPage, mode, userAnswers)
                    .mustBe(consignmentRoutes.CheckYourAnswersController.onPageLoad(answers.lrn))
              }
            }
          }

          "when Yes selected" - {
            "to contact name page" in {
              forAll(arbitraryTraderDetailsConsignmentAnswersWithConsignorAndConsignee) {
                answers =>
                  val userAnswers = answers
                    .setValue(consignor.AddContactPage, true)
                    .removeValue(consignor.contact.NamePage)
                    .removeValue(consignor.contact.TelephoneNumberPage)
                  navigator
                    .nextPage(consignor.AddContactPage, mode, userAnswers)
                    .mustBe(contactRoutes.NameController.onPageLoad(userAnswers.lrn, mode))
              }
            }
          }
        }

        "must go from contact name page" - {
          "when telephone number exists" - {
            "to check your answers page" in {
              forAll(arbitraryTraderDetailsConsignmentAnswersWithConsignorAndConsignee) {
                answers =>
                  navigator
                    .nextPage(consignor.contact.NamePage, mode, answers)
                    .mustBe(consignmentRoutes.CheckYourAnswersController.onPageLoad(answers.lrn))
              }
            }
          }

          "when no telephone number exists" - {
            "to contact telephone number page" in {
              forAll(arbitraryTraderDetailsConsignmentAnswersWithConsignorAndConsignee) {
                answers =>
                  val userAnswers = answers.removeValue(consignor.contact.TelephoneNumberPage)
                  navigator
                    .nextPage(consignor.contact.NamePage, mode, userAnswers)
                    .mustBe(contactRoutes.TelephoneNumberController.onPageLoad(userAnswers.lrn, mode))
              }
            }
          }
        }

        "must go from contact telephone number page to check your answers page" in {
          forAll(arbitraryTraderDetailsConsignmentAnswersWithConsignorAndConsignee) {
            answers =>
              navigator
                .nextPage(consignor.contact.TelephoneNumberPage, mode, answers)
                .mustBe(consignmentRoutes.CheckYourAnswersController.onPageLoad(answers.lrn))
          }
        }

        "must go from more than one consignee page " - {
          "to check your answers page when yes is selected" in {
            forAll(arbitraryTraderDetailsConsignmentAnswersWithConsignorAndConsignee) {
              answers =>
                val userAnswers = answers.setValue(consignee.MoreThanOneConsigneePage, true)
                navigator
                  .nextPage(consignee.MoreThanOneConsigneePage, mode, userAnswers)
                  .mustBe(consignmentRoutes.CheckYourAnswersController.onPageLoad(userAnswers.lrn))
            }
          }

          "to Consignee EoriYesNo page when no is selected and we have no Consignee Data" in {
            forAll(arbitraryTraderDetailsConsignmentAnswersWithConsignorAndConsignee) {
              answers =>
                val userAnswers = answers
                  .setValue(consignee.MoreThanOneConsigneePage, false)
                  .removeValue(consignee.EoriYesNoPage)
                  .removeValue(consignee.EoriNumberPage)

                navigator
                  .nextPage(consignee.MoreThanOneConsigneePage, mode, userAnswers)
                  .mustBe(consigneeRoutes.EoriYesNoController.onPageLoad(userAnswers.lrn, mode))
            }
          }
        }

        "must go from more than one consignee EoriYesNo page " - {
          "to check your answers page when no is selected" in {
            forAll(arbitraryTraderDetailsConsignmentAnswersWithConsignorAndConsignee) {
              answers =>
                val userAnswers = answers.setValue(consignee.EoriYesNoPage, false)
                navigator
                  .nextPage(consignee.EoriYesNoPage, mode, userAnswers)
                  .mustBe(consignmentRoutes.CheckYourAnswersController.onPageLoad(userAnswers.lrn))
            }
          }

          "to Consignee Eori page when yes is selected" in {
            forAll(arbitraryTraderDetailsConsignmentAnswersWithConsignorAndConsignee) {
              answers =>
                val userAnswers = answers
                  .setValue(consignee.EoriYesNoPage, true)
                  .removeValue(consignee.EoriNumberPage)

                navigator
                  .nextPage(consignee.EoriYesNoPage, mode, userAnswers)
                  .mustBe(consigneeRoutes.EoriNumberController.onPageLoad(userAnswers.lrn, mode))
            }
          }
        }

        "must go from consignee eori page to check your answers page" in {
          forAll(arbitraryTraderDetailsConsignmentAnswersWithConsignorAndConsignee) {
            answers =>
              navigator
                .nextPage(consignee.EoriNumberPage, mode, answers)
                .mustBe(consignmentRoutes.CheckYourAnswersController.onPageLoad(answers.lrn))
          }
        }

        "must go from consignee name page to check your answers page" in {
          forAll(arbitraryTraderDetailsConsignmentAnswersWithConsignorAndConsignee) {
            answers =>
              navigator
                .nextPage(consignee.NamePage, mode, answers)
                .mustBe(consignmentRoutes.CheckYourAnswersController.onPageLoad(answers.lrn))
          }
        }

        "must go from consignee address page to check your answers page" in {
          forAll(arbitraryTraderDetailsConsignmentAnswersWithConsignorAndConsignee) {
            answers =>
              navigator
                .nextPage(consignee.AddressPage, mode, answers)
                .mustBe(consignmentRoutes.CheckYourAnswersController.onPageLoad(answers.lrn))
          }
        }
      }
    }

    "when in CheckMode" - {

      val mode = CheckMode

      "must go from approved operator page" - {
        "when Yes selected" - {
          "to check Your Answers page" in {
            forAll(arbitraryTraderDetailsWithConsignorAndConsigneeAnswers) {
              answers =>
                val userAnswers = answers
                  .setValue(ApprovedOperatorPage, true)
                  .setValue(SecurityDetailsTypePage, NoSecurityDetails)
                navigator
                  .nextPage(ApprovedOperatorPage, mode, userAnswers)
                  .mustBe(tdRoutes.CheckYourAnswersController.onPageLoad(userAnswers.lrn))
            }
          }
        }

        "when No selected" - {
          "to consignor eori page" in {
            forAll(arbitraryTraderDetailsWithConsignorAndConsigneeAnswers) {
              answers =>
                val userAnswers = answers
                  .setValue(ApprovedOperatorPage, false)
                  .setValue(SecurityDetailsTypePage, NoSecurityDetails)
                  .removeValue(consignor.EoriYesNoPage)

                navigator
                  .nextPage(ApprovedOperatorPage, mode, userAnswers)
                  .mustBe(consignorRoutes.EoriYesNoController.onPageLoad(userAnswers.lrn, mode))
            }
          }
        }
      }

      "must go from more than one consignor EoriYesNo page " - {
        "to check your answers page when no is selected" in {
          forAll(arbitraryTraderDetailsWithConsignorAndConsigneeAnswers) {
            answers =>
              val userAnswers = answers.setValue(consignor.EoriYesNoPage, false)
              navigator
                .nextPage(consignor.EoriYesNoPage, mode, userAnswers)
                .mustBe(tdRoutes.CheckYourAnswersController.onPageLoad(userAnswers.lrn))
          }
        }

        "to Consignee Eori page when yes is selected" in {
          forAll(arbitraryTraderDetailsWithConsignorAndConsigneeAnswers) {
            answers =>
              val userAnswers = answers
                .setValue(consignor.EoriYesNoPage, true)
                .removeValue(consignor.EoriPage)

              navigator
                .nextPage(consignor.EoriYesNoPage, mode, userAnswers)
                .mustBe(consignorRoutes.EoriController.onPageLoad(userAnswers.lrn, mode))
          }
        }
      }

      "must go from consignor eori page to check your answers page" in {
        forAll(arbitraryTraderDetailsWithConsignorAndConsigneeAnswers) {
          answers =>
            navigator
              .nextPage(consignor.EoriPage, mode, answers)
              .mustBe(tdRoutes.CheckYourAnswersController.onPageLoad(answers.lrn))
        }
      }

      "must go from consignor name page to check your answers page" in {
        forAll(arbitraryTraderDetailsWithConsignorAndConsigneeAnswers) {
          answers =>
            navigator
              .nextPage(consignor.NamePage, mode, answers)
              .mustBe(tdRoutes.CheckYourAnswersController.onPageLoad(answers.lrn))
        }
      }

      "must go from consignor address page to check your answers page" in {
        forAll(arbitraryTraderDetailsWithConsignorAndConsigneeAnswers) {
          answers =>
            navigator
              .nextPage(consignor.AddressPage, mode, answers)
              .mustBe(tdRoutes.CheckYourAnswersController.onPageLoad(answers.lrn))
        }
      }

      "must go from add contact page" - {
        "when No selected" - {
          "to check your answers page" in {
            forAll(arbitraryTraderDetailsWithConsignorAndConsigneeAnswers) {
              answers =>
                val userAnswers = answers.setValue(consignor.AddContactPage, false)
                navigator
                  .nextPage(consignor.AddContactPage, mode, userAnswers)
                  .mustBe(tdRoutes.CheckYourAnswersController.onPageLoad(answers.lrn))
            }
          }
        }

        "when Yes selected" - {
          "to contact name page" in {
            forAll(arbitraryTraderDetailsWithConsignorAndConsigneeAnswers) {
              answers =>
                val userAnswers = answers
                  .setValue(consignor.AddContactPage, true)
                  .removeValue(consignor.contact.NamePage)
                  .removeValue(consignor.contact.TelephoneNumberPage)
                navigator
                  .nextPage(consignor.AddContactPage, mode, userAnswers)
                  .mustBe(contactRoutes.NameController.onPageLoad(userAnswers.lrn, mode))
            }
          }
        }
      }

      "must go from contact name page" - {
        "when telephone number exists" - {
          "to check your answers page" in {
            forAll(arbitraryTraderDetailsWithConsignorAndConsigneeAnswers) {
              answers =>
                navigator
                  .nextPage(consignor.contact.NamePage, mode, answers)
                  .mustBe(tdRoutes.CheckYourAnswersController.onPageLoad(answers.lrn))
            }
          }
        }

        "when no telephone number exists" - {
          "to contact telephone number page" in {
            forAll(arbitraryTraderDetailsWithConsignorAndConsigneeAnswers) {
              answers =>
                val userAnswers = answers.removeValue(consignor.contact.TelephoneNumberPage)
                navigator
                  .nextPage(consignor.contact.NamePage, mode, userAnswers)
                  .mustBe(contactRoutes.TelephoneNumberController.onPageLoad(userAnswers.lrn, mode))
            }
          }
        }
      }

      "must go from contact telephone number page to check your answers page" in {
        forAll(arbitraryTraderDetailsWithConsignorAndConsigneeAnswers) {
          answers =>
            navigator
              .nextPage(consignor.contact.TelephoneNumberPage, mode, answers)
              .mustBe(tdRoutes.CheckYourAnswersController.onPageLoad(answers.lrn))
        }
      }

      "must go from more than one consignee page " - {
        "to check your answers page when yes is selected" in {
          forAll(arbitraryTraderDetailsWithConsignorAndConsigneeAnswers) {
            answers =>
              val userAnswers = answers.setValue(consignee.MoreThanOneConsigneePage, true)
              navigator
                .nextPage(consignee.MoreThanOneConsigneePage, mode, userAnswers)
                .mustBe(tdRoutes.CheckYourAnswersController.onPageLoad(userAnswers.lrn))
          }
        }

        "to Consignee EoriYesNo page when no is selected and we have no Consignee Data" in {
          forAll(arbitraryTraderDetailsWithConsignorAndConsigneeAnswers) {
            answers =>
              val userAnswers = answers
                .setValue(consignee.MoreThanOneConsigneePage, false)
                .removeValue(consignee.EoriYesNoPage)
                .removeValue(consignee.EoriNumberPage)

              navigator
                .nextPage(consignee.MoreThanOneConsigneePage, mode, userAnswers)
                .mustBe(consigneeRoutes.EoriYesNoController.onPageLoad(userAnswers.lrn, mode))
          }
        }
      }

      "must go from more than one consignee EoriYesNo page " - {
        "to check your answers page when no is selected" in {
          forAll(arbitraryTraderDetailsWithConsignorAndConsigneeAnswers) {
            answers =>
              val userAnswers = answers.setValue(consignee.EoriYesNoPage, false)
              navigator
                .nextPage(consignee.EoriYesNoPage, mode, userAnswers)
                .mustBe(tdRoutes.CheckYourAnswersController.onPageLoad(userAnswers.lrn))
          }
        }

        "to Consignee Eori page when yes is selected" in {
          forAll(arbitraryTraderDetailsConsignmentAnswersWithConsignorAndConsignee) {
            answers =>
              val userAnswers = answers
                .setValue(consignee.EoriYesNoPage, true)
                .removeValue(consignee.EoriNumberPage)

              navigator
                .nextPage(consignee.EoriYesNoPage, mode, userAnswers)
                .mustBe(consigneeRoutes.EoriNumberController.onPageLoad(userAnswers.lrn, mode))
          }
        }
      }

      "must go from consignee eori page to check your answers page" in {
        forAll(arbitraryTraderDetailsWithConsignorAndConsigneeAnswers) {
          answers =>
            navigator
              .nextPage(consignee.EoriNumberPage, mode, answers)
              .mustBe(tdRoutes.CheckYourAnswersController.onPageLoad(answers.lrn))
        }
      }

      "must go from consignee name page to check your answers page" in {
        forAll(arbitraryTraderDetailsWithConsignorAndConsigneeAnswers) {
          answers =>
            navigator
              .nextPage(consignee.NamePage, mode, answers)
              .mustBe(tdRoutes.CheckYourAnswersController.onPageLoad(answers.lrn))
        }
      }

      "must go from consignee address page to check your answers page" in {
        forAll(arbitraryTraderDetailsWithConsignorAndConsigneeAnswers) {
          answers =>
            navigator
              .nextPage(consignee.AddressPage, mode, answers)
              .mustBe(tdRoutes.CheckYourAnswersController.onPageLoad(answers.lrn))
        }
      }
    }
  }
}
