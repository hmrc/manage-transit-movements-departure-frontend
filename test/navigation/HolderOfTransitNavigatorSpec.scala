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

package navigation

import base.SpecBase
import controllers.preTaskList.{routes => ptlRoutes}
import controllers.routes
import controllers.traderDetails.holderOfTransit.{routes => hotRoutes}
import generators.{Generators, HolderOfTransitUserAnswersGenerator}
import models._
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages._
import pages.traderDetails.holderOfTransit._

class HolderOfTransitNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks with Generators with HolderOfTransitUserAnswersGenerator {

  private val navigator = new HolderOfTransitNavigator

  "Navigator" - {
    "must go from a page that doesn't exist in the route map" - {

      case object UnknownPage extends Page

      "when in normal mode" - {
        "to start of the departure journey" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              navigator
                .nextPage(UnknownPage, NormalMode, answers)
                .mustBe(ptlRoutes.LocalReferenceNumberController.onPageLoad())
          }
        }
      }

      "when in check mode" - {
        "to session expired" in {
          navigator
            .nextPage(UnknownPage, CheckMode, emptyUserAnswers)
            .mustBe(routes.SessionExpiredController.onPageLoad())
        }
      }
    }

    "In NormalMode" - {
      "must go from Transit Holder EORI Yes No page" - {
        "when Yes selected" - {
          "to Eori page" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val userAnswers = answers.setValue(EoriYesNoPage, true)
                navigator
                  .nextPage(EoriYesNoPage, NormalMode, userAnswers)
                  .mustBe(hotRoutes.EoriController.onPageLoad(userAnswers.lrn, NormalMode))
            }
          }
        }

        "when No selected" - {
          "to NamePage" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val userAnswers = answers.setValue(EoriYesNoPage, false)
                navigator
                  .nextPage(EoriYesNoPage, NormalMode, userAnswers)
                  .mustBe(hotRoutes.NameController.onPageLoad(userAnswers.lrn, NormalMode))
            }
          }
        }

        "when nothing selected" - {
          "to session expired" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val userAnswers = answers.removeValue(EoriYesNoPage)
                navigator
                  .nextPage(EoriYesNoPage, NormalMode, userAnswers)
                  .mustBe(routes.SessionExpiredController.onPageLoad())
            }
          }
        }
      }

      "must go from EoriPage to NamePage" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            navigator
              .nextPage(EoriPage, NormalMode, answers)
              .mustBe(hotRoutes.NameController.onPageLoad(answers.lrn, NormalMode))
        }
      }

      "must go from TIR identification number Yes No page" - {
        "when Yes selected" - {
          "to TIR identification number page" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val userAnswers = answers.setValue(TirIdentificationYesNoPage, true)
                navigator
                  .nextPage(TirIdentificationYesNoPage, NormalMode, userAnswers)
                  .mustBe(hotRoutes.TirIdentificationController.onPageLoad(answers.lrn, NormalMode))
            }
          }
        }

        "when No selected" - {
          "to NamePage" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val userAnswers = answers.setValue(TirIdentificationYesNoPage, false)
                navigator
                  .nextPage(TirIdentificationYesNoPage, NormalMode, userAnswers)
                  .mustBe(hotRoutes.NameController.onPageLoad(userAnswers.lrn, NormalMode))
            }
          }
        }

        "must go from TirIdentificationPage to NamePage" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              navigator
                .nextPage(TirIdentificationPage, NormalMode, answers)
                .mustBe(hotRoutes.NameController.onPageLoad(answers.lrn, NormalMode))
          }
        }

        "when nothing selected" - {
          "to session expired" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val userAnswers = answers.removeValue(TirIdentificationYesNoPage)
                navigator
                  .nextPage(TirIdentificationYesNoPage, NormalMode, userAnswers)
                  .mustBe(routes.SessionExpiredController.onPageLoad())
            }
          }
        }
      }

      "must go from Name page to Address page" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            navigator
              .nextPage(NamePage, NormalMode, answers)
              .mustBe(hotRoutes.AddressController.onPageLoad(answers.lrn, NormalMode))
        }
      }

      "must go from Address page to Add Contact page" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            navigator
              .nextPage(AddressPage, NormalMode, answers)
              .mustBe(hotRoutes.AddContactController.onPageLoad(answers.lrn, NormalMode))
        }
      }

      "must go from Add Contact page" - {
        "when Yes selected" - {
          "to ContactName Page" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val userAnswers = answers.setValue(AddContactPage, true)
                navigator
                  .nextPage(AddContactPage, NormalMode, userAnswers)
                  .mustBe(hotRoutes.ContactNameController.onPageLoad(userAnswers.lrn, NormalMode))
            }
          }
        }

        "when No selected" - {
          "to CheckYourAnswers page" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val userAnswers = answers.setValue(AddContactPage, false)
                navigator
                  .nextPage(AddContactPage, NormalMode, userAnswers)
                  .mustBe(hotRoutes.CheckYourAnswersController.onPageLoad(userAnswers.lrn))
            }
          }
        }

        "must go from ContactNamePage to ContactTelephoneNumberPage" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              navigator
                .nextPage(ContactNamePage, NormalMode, answers)
                .mustBe(hotRoutes.ContactTelephoneNumberController.onPageLoad(answers.lrn, NormalMode))
          }
        }

        "must go from ContactTelephoneNumberPage to CheckYourAnswersPage" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              navigator
                .nextPage(ContactTelephoneNumberPage, NormalMode, answers)
                .mustBe(hotRoutes.CheckYourAnswersController.onPageLoad(answers.lrn))
          }
        }

        "when nothing selected" - {
          "to session expired" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val userAnswers = answers.removeValue(AddContactPage)
                navigator
                  .nextPage(AddContactPage, NormalMode, userAnswers)
                  .mustBe(routes.SessionExpiredController.onPageLoad())
            }
          }
        }
      }
    }

    "In CheckMode" - {
      "must go from change EoriYesNoPage" - {
        "to Check Your Answers when answer is no" in {
          forAll(arbitraryCompletedAnswersWithEori) {
            answers =>
              val userAnswers = answers.setValue(EoriYesNoPage, false)
              navigator
                .nextPage(EoriYesNoPage, CheckMode, userAnswers)
                .mustBe(hotRoutes.CheckYourAnswersController.onPageLoad(userAnswers.lrn))
          }
        }

        "to Change Eori when answer is Yes" in {
          forAll(arbitraryCompletedAnswersWithoutEori) {
            answers =>
              val userAnswers = answers.setValue(EoriYesNoPage, true)
              navigator
                .nextPage(EoriYesNoPage, CheckMode, userAnswers)
                .mustBe(hotRoutes.EoriController.onPageLoad(userAnswers.lrn, CheckMode))
          }
        }
      }

      "must go from change Eori to Check your answers page" in {
        forAll(arbitraryCompletedAnswersWithEori) {
          answers =>
            navigator
              .nextPage(EoriPage, CheckMode, answers)
              .mustBe(hotRoutes.CheckYourAnswersController.onPageLoad(answers.lrn))
        }
      }

      "must go from change TirIdentificationYesNoPage" - {
        "to Check Your Answers when answer is no" in {
          forAll(arbitraryCompletedAnswersWithTirId) {
            answers =>
              val userAnswers = answers.setValue(TirIdentificationYesNoPage, false)
              navigator
                .nextPage(TirIdentificationYesNoPage, CheckMode, userAnswers)
                .mustBe(hotRoutes.CheckYourAnswersController.onPageLoad(userAnswers.lrn))
          }
        }

        "to Change TirIdentificationPage when answer is Yes" in {
          forAll(arbitraryCompletedAnswersWithoutTirId) {
            answers =>
              val userAnswers = answers.setValue(TirIdentificationYesNoPage, true)
              navigator
                .nextPage(TirIdentificationYesNoPage, CheckMode, userAnswers)
                .mustBe(hotRoutes.TirIdentificationController.onPageLoad(userAnswers.lrn, CheckMode))
          }
        }
      }

      "must go from change TirIdentificationPage to Check your answers page" in {
        forAll(arbitraryCompletedAnswersWithTirId) {
          answers =>
            navigator
              .nextPage(TirIdentificationPage, CheckMode, answers)
              .mustBe(hotRoutes.CheckYourAnswersController.onPageLoad(answers.lrn))
        }
      }

      "must go from change NamePage to Check your answers page" in {
        forAll(arbitraryCompletedAnswersWithoutEori) {
          answers =>
            navigator
              .nextPage(NamePage, CheckMode, answers)
              .mustBe(hotRoutes.CheckYourAnswersController.onPageLoad(answers.lrn))
        }
      }

      "must go from change AddressPage to Check your answers page" in {
        forAll(arbitraryCompletedAnswersWithoutEori) {
          answers =>
            navigator
              .nextPage(AddressPage, CheckMode, answers)
              .mustBe(hotRoutes.CheckYourAnswersController.onPageLoad(answers.lrn))
        }
      }

      "must go from change AddContactPage" - {
        "to Check Your Answers when answer is no" in {
          forAll(arbitraryCompletedAnswersWithAdditionalContact) {
            answers =>
              val userAnswers = answers.setValue(AddContactPage, false)
              navigator
                .nextPage(AddContactPage, CheckMode, userAnswers)
                .mustBe(hotRoutes.CheckYourAnswersController.onPageLoad(userAnswers.lrn))
          }
        }

        "to Change ContactNamePage when answer is Yes" in {
          forAll(arbitraryCompletedAnswersWithoutEori) {
            answers =>
              val userAnswers = answers.setValue(AddContactPage, true)
              navigator
                .nextPage(AddContactPage, CheckMode, userAnswers)
                .mustBe(hotRoutes.ContactNameController.onPageLoad(userAnswers.lrn, CheckMode))
          }
        }
      }

      "must go from change ContactNamePage" - {
        "to Check Your Answers when telephone number exists" in {
          forAll(arbitraryCompletedAnswersWithAdditionalContact) {
            answers =>
              navigator
                .nextPage(ContactNamePage, CheckMode, answers)
                .mustBe(hotRoutes.CheckYourAnswersController.onPageLoad(answers.lrn))
          }
        }

        "to Change ContactTelephone when no telephone number exists" in {
          forAll(arbitraryCompletedAnswersWithAdditionalContact) {
            answers =>
              val userAnswers = answers.removeValue(ContactTelephoneNumberPage)
              navigator
                .nextPage(ContactNamePage, CheckMode, userAnswers)
                .mustBe(hotRoutes.ContactTelephoneNumberController.onPageLoad(userAnswers.lrn, CheckMode))
          }
        }
      }

      "must go from change ContactTelephoneNumberPage to Check your answers page" in {
        forAll(arbitraryCompletedAnswersWithAdditionalContact) {
          answers =>
            navigator
              .nextPage(ContactTelephoneNumberPage, CheckMode, answers)
              .mustBe(hotRoutes.CheckYourAnswersController.onPageLoad(answers.lrn))
        }
      }

    }
  }
}
