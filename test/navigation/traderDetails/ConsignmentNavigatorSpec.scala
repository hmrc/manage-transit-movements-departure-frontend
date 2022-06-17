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
import controllers.traderDetails.consignment.consignor.{routes => consignorRoutes}
import generators.{Generators, TraderDetailsUserAnswersGenerator}
import models._
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages._
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
          "when Yes selected" - {
            "to consignor eori yes no page page" in {
              val userAnswers = emptyUserAnswers.setValue(ApprovedOperatorPage, true)
              navigator
                .nextPage(ApprovedOperatorPage, mode, userAnswers)
                .mustBe(consignorRoutes.EoriYesNoController.onPageLoad(userAnswers.lrn, mode))
            }
          }

          "when No selected" - {
            "to consignee yes no page" ignore {
              val userAnswers = emptyUserAnswers.setValue(ApprovedOperatorPage, false)
              navigator
                .nextPage(ApprovedOperatorPage, mode, userAnswers)
                .mustBe(???) //ToDo - Consignee Yes No Controller
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

        "must go from name page to address page" in {
          navigator
            .nextPage(consignor.NamePage, mode, emptyUserAnswers)
            .mustBe(consignorRoutes.AddressController.onPageLoad(emptyUserAnswers.lrn, mode))
        }

        "must go from address page to add contact page" in {
          navigator
            .nextPage(consignor.AddressPage, mode, emptyUserAnswers)
            .mustBe(consignorRoutes.AddContactController.onPageLoad(emptyUserAnswers.lrn, mode))
        }

        "must go from add contact page" - {
          "when Yes selected" - {
            "to contact name page" ignore {
              val userAnswers = emptyUserAnswers.setValue(consignor.AddContactPage, true)
              navigator
                .nextPage(consignor.AddContactPage, mode, userAnswers)
                .mustBe(???) //ToDo - Consignor Contact Name Page
            }
          }

          "when No selected" - {
            "to check your answers page" ignore {
              val userAnswers = emptyUserAnswers.setValue(consignor.AddContactPage, false)
              navigator
                .nextPage(consignor.AddContactPage, mode, userAnswers)
                .mustBe(???) //ToDo - Consignee Yes No Controller
            }
          }

          "when nothing selected" - {
            "to session expired" ignore {
              navigator
                .nextPage(consignor.AddContactPage, mode, emptyUserAnswers)
                .mustBe(routes.SessionExpiredController.onPageLoad())
            }
          }
        }
      }

      "when answers complete" - {

        "must go from approved operator page" - {
          "when No selected" - {
            "to check Your Answers page" ignore {
              forAll(arbitraryTraderDetailsConsignmentAnswersWithConsignor) {
                answers =>
                  val userAnswers = answers.setValue(ApprovedOperatorPage, false)
                  navigator
                    .nextPage(ApprovedOperatorPage, mode, userAnswers)
                    .mustBe(???) //TODO CheckYourAnswers Here
              }
            }
          }

          "when Yes selected" - {
            "to consignor eori page" in {
              forAll(arbitraryTraderDetailsConsignmentAnswersWithoutConsignor) {
                answers =>
                  val userAnswers = answers.setValue(ApprovedOperatorPage, true)
                  navigator
                    .nextPage(ApprovedOperatorPage, mode, userAnswers)
                    .mustBe(consignorRoutes.EoriYesNoController.onPageLoad(userAnswers.lrn, mode))
              }
            }
          }
        }

        "must go from is consignor eori known page" - {
          "when No selected" - {
            "to check Your Answers page" ignore {
              forAll(arbitraryTraderDetailsConsignmentAnswersWithConsignor) {
                answers =>
                  val userAnswers = answers.setValue(consignor.EoriYesNoPage, false)
                  navigator
                    .nextPage(consignor.EoriYesNoPage, mode, userAnswers)
                    .mustBe(???) //TODO CheckYourAnswers Here
              }
            }
          }

          "when Yes selected" - {
            "to consignor eori page" in {
              forAll(arbitraryTraderDetailsConsignmentAnswersWithoutConsignorEori) {
                answers =>
                  val userAnswers = answers.setValue(consignor.EoriYesNoPage, true)
                  navigator
                    .nextPage(consignor.EoriYesNoPage, mode, userAnswers)
                    .mustBe(consignorRoutes.EoriController.onPageLoad(userAnswers.lrn, mode))
              }
            }
          }
        }

        "must go from name page to check your answers page" ignore {
          forAll(arbitraryTraderDetailsConsignmentAnswers) {
            answers =>
              navigator
                .nextPage(consignor.NamePage, mode, answers)
                .mustBe(???) //TODO CheckYourAnswers
          }
        }

        "must go from address page to check your answers page" ignore {
          forAll(arbitraryTraderDetailsConsignmentAnswers) {
            answers =>
              navigator
                .nextPage(consignor.AddressPage, mode, answers)
                .mustBe(???) //TODO CheckYourAnswers
          }
        }

        "must go from add contact page" - {
          "when No selected" - {
            "to check your answers page" ignore {
              forAll(arbitraryTraderDetailsConsignmentAnswers) {
                answers =>
                  val userAnswers = answers.setValue(consignor.AddContactPage, false)
                  navigator
                    .nextPage(consignor.AddContactPage, mode, userAnswers)
                    .mustBe(???) //TODO CheckYourAnswers
              }
            }
          }

          "when Yes selected" - {
            "to contact name page" ignore {
              forAll(arbitraryTraderDetailsConsignmentAnswers) {
                answers =>
                  val userAnswers = answers.setValue(consignor.AddContactPage, true)
                  navigator
                    .nextPage(consignor.AddContactPage, mode, userAnswers)
                    .mustBe(???) //TODO Consignor Contact Name Page
              }
            }
          }
        }
      }
    }

    "when in CheckMode" - {

      val mode = CheckMode

      "must go from change approved operator page" - {
        "when No selected" - {
          "to check your answers page" ignore {
            forAll(arbitraryTraderDetailsConsignmentAnswersWithConsignor) {
              answers =>
                val userAnswers = answers.setValue(ApprovedOperatorPage, false)
                navigator
                  .nextPage(ApprovedOperatorPage, mode, userAnswers)
                  .mustBe(???) //TODO Check Your Answers Page
            }
          }
        }

        "when Yes selected" - {
          "to consignor eori yes no page" in {
            forAll(arbitraryTraderDetailsConsignmentAnswersWithoutConsignor) {
              answers =>
                val userAnswers = answers.setValue(ApprovedOperatorPage, true)
                navigator
                  .nextPage(ApprovedOperatorPage, mode, userAnswers)
                  .mustBe(consignorRoutes.EoriYesNoController.onPageLoad(userAnswers.lrn, mode))
            }
          }
        }
      }

      "must go from change is consignor eori known page" - {
        "when No selected" - {
          "to check your answers page" ignore {
            forAll(arbitraryTraderDetailsConsignmentAnswersWithConsignor) {
              answers =>
                val userAnswers = answers.setValue(consignor.EoriYesNoPage, false)
                navigator
                  .nextPage(consignor.EoriYesNoPage, mode, userAnswers)
                  .mustBe(???) //TODO Check Your Answers Page
            }
          }
        }

        "when Yes selected" - {
          "to consignor eori page" in {
            forAll(arbitraryTraderDetailsConsignmentAnswersWithoutConsignorEori) {
              answers =>
                val userAnswers = answers.setValue(consignor.EoriYesNoPage, true)
                navigator
                  .nextPage(consignor.EoriYesNoPage, mode, userAnswers)
                  .mustBe(consignorRoutes.EoriController.onPageLoad(userAnswers.lrn, mode))
            }
          }
        }
      }

      "must go from eori page to check your answers page" ignore {
        forAll(arbitraryTraderDetailsConsignmentAnswersWithConsignor) {
          answers =>
            navigator
              .nextPage(consignor.EoriPage, mode, answers)
              .mustBe(???) //ToDo CheckYourAnswers Page
        }
      }

      "must go from name page to check your answers page" ignore {
        forAll(arbitraryTraderDetailsAnswers) {
          answers =>
            navigator
              .nextPage(consignor.NamePage, mode, answers)
              .mustBe(???) //TODO Check Your Answers Page
        }
      }

      "must go from address page to check your answers page" ignore {
        forAll(arbitraryTraderDetailsAnswers) {
          answers =>
            navigator
              .nextPage(consignor.AddressPage, mode, answers)
              .mustBe(???) //TODO Check Your Answers Page
        }
      }

      "must go from add contact page" - {
        "when No selected" - {
          "to check your answers page" ignore {
            forAll(arbitraryTraderDetailsConsignmentAnswersWithConsignor) {
              answers =>
                val userAnswers = answers.setValue(consignor.AddContactPage, false)
                navigator
                  .nextPage(consignor.AddContactPage, mode, userAnswers)
                  .mustBe(???) //TODO Check Your Answers Page
            }
          }
        }

        "when Yes selected" - {
          "to contact name page" ignore {
            forAll(arbitraryTraderDetailsConsignmentAnswersWithConsignorWithoutContact) {
              answers =>
                val userAnswers = answers.setValue(consignor.AddContactPage, true)
                navigator
                  .nextPage(consignor.AddContactPage, mode, userAnswers)
                  .mustBe(???) //TODO Consignore Contact Name Page
            }
          }
        }
      }

    }
  }
}
