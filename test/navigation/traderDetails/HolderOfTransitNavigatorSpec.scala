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
import controllers.traderDetails.holderOfTransit.contact.{routes => contactRoutes}
import controllers.traderDetails.holderOfTransit.{routes => hotRoutes}
import controllers.traderDetails.{routes => tdRoutes}
import generators.{Generators, TraderDetailsUserAnswersGenerator}
import models._
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages._
import pages.traderDetails.holderOfTransit._

class HolderOfTransitNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks with Generators with TraderDetailsUserAnswersGenerator {

  private val navigator = new HolderOfTransitNavigator

  "Navigator" ignore {
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

        "must go from is eori known page" - {
          "when Yes selected" - {
            "to eori page" in {
              val userAnswers = emptyUserAnswers.setValue(EoriYesNoPage, true)
              navigator
                .nextPage(EoriYesNoPage, mode, userAnswers)
                .mustBe(hotRoutes.EoriController.onPageLoad(userAnswers.lrn, mode))
            }
          }

          "when No selected" - {
            "to name page" in {
              val userAnswers = emptyUserAnswers.setValue(EoriYesNoPage, false)
              navigator
                .nextPage(EoriYesNoPage, mode, userAnswers)
                .mustBe(hotRoutes.NameController.onPageLoad(userAnswers.lrn, mode))
            }
          }

          "when nothing selected" - {
            "to session expired" in {
              navigator
                .nextPage(EoriYesNoPage, mode, emptyUserAnswers)
                .mustBe(routes.SessionExpiredController.onPageLoad())
            }
          }
        }

        "must go from eori page to name page" in {
          navigator
            .nextPage(EoriPage, mode, emptyUserAnswers)
            .mustBe(hotRoutes.NameController.onPageLoad(emptyUserAnswers.lrn, mode))
        }

        "must go from is tir id known page" - {
          "when Yes selected" - {
            "to tir id page" in {
              val userAnswers = emptyUserAnswers.setValue(TirIdentificationYesNoPage, true)
              navigator
                .nextPage(TirIdentificationYesNoPage, mode, userAnswers)
                .mustBe(hotRoutes.TirIdentificationController.onPageLoad(userAnswers.lrn, mode))
            }
          }

          "when No selected" - {
            "to name page" in {
              val userAnswers = emptyUserAnswers.setValue(TirIdentificationYesNoPage, false)
              navigator
                .nextPage(TirIdentificationYesNoPage, mode, userAnswers)
                .mustBe(hotRoutes.NameController.onPageLoad(userAnswers.lrn, mode))
            }
          }

          "when nothing selected" - {
            "to session expired" in {
              navigator
                .nextPage(TirIdentificationYesNoPage, mode, emptyUserAnswers)
                .mustBe(routes.SessionExpiredController.onPageLoad())
            }
          }
        }

        "must go from tir id page to name page" in {
          navigator
            .nextPage(TirIdentificationPage, mode, emptyUserAnswers)
            .mustBe(hotRoutes.NameController.onPageLoad(emptyUserAnswers.lrn, mode))
        }

        "must go from name page to address page" in {
          navigator
            .nextPage(NamePage, mode, emptyUserAnswers)
            .mustBe(hotRoutes.AddressController.onPageLoad(emptyUserAnswers.lrn, mode))
        }

        "must go from address page to add contact page" in {
          navigator
            .nextPage(AddressPage, mode, emptyUserAnswers)
            .mustBe(hotRoutes.AddContactController.onPageLoad(emptyUserAnswers.lrn, mode))
        }

        "must go from add contact page" - {
          "when Yes selected" - {
            "to contact name page" in {
              val userAnswers = emptyUserAnswers.setValue(AddContactPage, true)
              navigator
                .nextPage(AddContactPage, mode, userAnswers)
                .mustBe(contactRoutes.NameController.onPageLoad(userAnswers.lrn, mode))
            }
          }

          "when No selected" - {
            "to check your answers page" in {
              val userAnswers = emptyUserAnswers.setValue(AddContactPage, false)
              navigator
                .nextPage(AddContactPage, mode, userAnswers)
                .mustBe(hotRoutes.CheckYourAnswersController.onPageLoad(userAnswers.lrn))
            }
          }

          "must go from contact name page to contact telephone number page" in {
            navigator
              .nextPage(contact.NamePage, mode, emptyUserAnswers)
              .mustBe(contactRoutes.TelephoneNumberController.onPageLoad(emptyUserAnswers.lrn, mode))
          }

          "must go from contact telephone number page to check your answers page" in {
            navigator
              .nextPage(contact.TelephoneNumberPage, mode, emptyUserAnswers)
              .mustBe(hotRoutes.CheckYourAnswersController.onPageLoad(emptyUserAnswers.lrn))
          }

          "when nothing selected" - {
            "to session expired" in {
              navigator
                .nextPage(AddContactPage, mode, emptyUserAnswers)
                .mustBe(routes.SessionExpiredController.onPageLoad())
            }
          }
        }
      }

      "when answers complete" - {

        "must go from is eori known page" - {
          "when No selected" - {
            "to check Your Answers page" in {
              forAll(arbitraryHolderOfTransitAnswersWithEori) {
                answers =>
                  val userAnswers = answers.setValue(EoriYesNoPage, false)
                  navigator
                    .nextPage(EoriYesNoPage, mode, userAnswers)
                    .mustBe(hotRoutes.CheckYourAnswersController.onPageLoad(userAnswers.lrn))
              }
            }
          }

          "when Yes selected" - {
            "to eori page" in {
              forAll(arbitraryHolderOfTransitAnswersWithoutEori) {
                answers =>
                  val userAnswers = answers.setValue(EoriYesNoPage, true)
                  navigator
                    .nextPage(EoriYesNoPage, mode, userAnswers)
                    .mustBe(hotRoutes.EoriController.onPageLoad(userAnswers.lrn, mode))
              }
            }
          }
        }

        "must go from eori page to check your answers page" in {
          forAll(arbitraryHolderOfTransitAnswersWithEori) {
            answers =>
              navigator
                .nextPage(EoriPage, mode, answers)
                .mustBe(hotRoutes.CheckYourAnswersController.onPageLoad(answers.lrn))
          }
        }

        "must go from change tir id known page" - {
          "when No selected" - {
            "to check your answers page" in {
              forAll(arbitraryHolderOfTransitAnswersWithTirId) {
                answers =>
                  val userAnswers = answers.setValue(TirIdentificationYesNoPage, false)
                  navigator
                    .nextPage(TirIdentificationYesNoPage, mode, userAnswers)
                    .mustBe(hotRoutes.CheckYourAnswersController.onPageLoad(userAnswers.lrn))
              }
            }
          }

          "when Yes selected" - {
            "to tir id page" in {
              forAll(arbitraryHolderOfTransitAnswersWithoutTirId) {
                answers =>
                  val userAnswers = answers.setValue(TirIdentificationYesNoPage, true)
                  navigator
                    .nextPage(TirIdentificationYesNoPage, mode, userAnswers)
                    .mustBe(hotRoutes.TirIdentificationController.onPageLoad(userAnswers.lrn, mode))
              }
            }
          }
        }

        "must go from tir id page to check your answers page" in {
          forAll(arbitraryHolderOfTransitAnswersWithTirId) {
            answers =>
              navigator
                .nextPage(TirIdentificationPage, mode, answers)
                .mustBe(hotRoutes.CheckYourAnswersController.onPageLoad(answers.lrn))
          }
        }

        "must go from name page to check your answers page" in {
          forAll(arbitraryHolderOfTransitAnswers) {
            answers =>
              navigator
                .nextPage(NamePage, mode, answers)
                .mustBe(hotRoutes.CheckYourAnswersController.onPageLoad(answers.lrn))
          }
        }

        "must go from address page to check your answers page" in {
          forAll(arbitraryHolderOfTransitAnswers) {
            answers =>
              navigator
                .nextPage(AddressPage, mode, answers)
                .mustBe(hotRoutes.CheckYourAnswersController.onPageLoad(answers.lrn))
          }
        }

        "must go from add contact name page" - {
          "when No selected" - {
            "to check your answers page" in {
              forAll(arbitraryHolderOfTransitAnswersWithAdditionalContact) {
                answers =>
                  val userAnswers = answers.setValue(AddContactPage, false)
                  navigator
                    .nextPage(AddContactPage, mode, userAnswers)
                    .mustBe(hotRoutes.CheckYourAnswersController.onPageLoad(userAnswers.lrn))
              }
            }
          }

          "when Yes selected" - {
            "to contact name page" in {
              forAll(arbitraryHolderOfTransitAnswersWithoutAdditionalContact) {
                answers =>
                  val userAnswers = answers.setValue(AddContactPage, true)
                  navigator
                    .nextPage(AddContactPage, mode, userAnswers)
                    .mustBe(contactRoutes.NameController.onPageLoad(userAnswers.lrn, mode))
              }
            }
          }
        }

        "must go from contact name page" - {
          "when telephone number exists" - {
            "to check your answers page" in {
              forAll(arbitraryHolderOfTransitAnswersWithAdditionalContact) {
                answers =>
                  navigator
                    .nextPage(contact.NamePage, mode, answers)
                    .mustBe(hotRoutes.CheckYourAnswersController.onPageLoad(answers.lrn))
              }
            }
          }

          "when no telephone number exists" - {
            "to contact telephone number page" in {
              forAll(arbitraryHolderOfTransitAnswersWithAdditionalContact) {
                answers =>
                  val userAnswers = answers.removeValue(contact.TelephoneNumberPage)
                  navigator
                    .nextPage(contact.NamePage, mode, userAnswers)
                    .mustBe(contactRoutes.TelephoneNumberController.onPageLoad(userAnswers.lrn, mode))
              }
            }
          }
        }

        "must go from contact telephone number page to check your answers page" in {
          forAll(arbitraryHolderOfTransitAnswersWithAdditionalContact) {
            answers =>
              navigator
                .nextPage(contact.TelephoneNumberPage, mode, answers)
                .mustBe(hotRoutes.CheckYourAnswersController.onPageLoad(answers.lrn))
          }
        }
      }
    }

    "when in CheckMode" - {

      val mode = CheckMode

      "must go from change is eori known page" - {
        "when No selected" - {
          "to check your answers page" in {
            forAll(arbitraryTraderDetailsAnswersWithHolderOfTransitWithEori) {
              answers =>
                val userAnswers = answers.setValue(EoriYesNoPage, false)
                navigator
                  .nextPage(EoriYesNoPage, mode, userAnswers)
                  .mustBe(tdRoutes.CheckYourAnswersController.onPageLoad(userAnswers.lrn))
            }
          }
        }

        "when Yes selected" - {
          "to eori page" in {
            forAll(arbitraryTraderDetailsAnswersWithHolderOfTransitWithoutEori) {
              answers =>
                val userAnswers = answers.setValue(EoriYesNoPage, true)
                navigator
                  .nextPage(EoriYesNoPage, mode, userAnswers)
                  .mustBe(hotRoutes.EoriController.onPageLoad(userAnswers.lrn, mode))
            }
          }
        }
      }

      "must go from eori page to check your answers page" in {
        forAll(arbitraryTraderDetailsAnswersWithHolderOfTransitWithEori) {
          answers =>
            navigator
              .nextPage(EoriPage, mode, answers)
              .mustBe(tdRoutes.CheckYourAnswersController.onPageLoad(answers.lrn))
        }
      }

      "must go from change is tir id known page" - {
        "when No selected" - {
          "to check your answers page" in {
            forAll(arbitraryTraderDetailsAnswersWithHolderOfTransitWithTirId) {
              answers =>
                val userAnswers = answers.setValue(TirIdentificationYesNoPage, false)
                navigator
                  .nextPage(TirIdentificationYesNoPage, mode, userAnswers)
                  .mustBe(tdRoutes.CheckYourAnswersController.onPageLoad(userAnswers.lrn))
            }
          }
        }

        "when Yes selected" - {
          "to tir id page" in {
            forAll(arbitraryTraderDetailsAnswersWithHolderOfTransitWithoutTirId) {
              answers =>
                val userAnswers = answers.setValue(TirIdentificationYesNoPage, true)
                navigator
                  .nextPage(TirIdentificationYesNoPage, mode, userAnswers)
                  .mustBe(hotRoutes.TirIdentificationController.onPageLoad(userAnswers.lrn, mode))
            }
          }
        }
      }

      "must go from tir id page to check your answers page" in {
        forAll(arbitraryTraderDetailsAnswersWithHolderOfTransitWithTirId) {
          answers =>
            navigator
              .nextPage(TirIdentificationPage, mode, answers)
              .mustBe(tdRoutes.CheckYourAnswersController.onPageLoad(answers.lrn))
        }
      }

      "must go from name page to check your answers page" in {
        forAll(arbitraryTraderDetailsAnswers) {
          answers =>
            navigator
              .nextPage(NamePage, mode, answers)
              .mustBe(tdRoutes.CheckYourAnswersController.onPageLoad(answers.lrn))
        }
      }

      "must go from address page to check your answers page" in {
        forAll(arbitraryTraderDetailsAnswers) {
          answers =>
            navigator
              .nextPage(AddressPage, mode, answers)
              .mustBe(tdRoutes.CheckYourAnswersController.onPageLoad(answers.lrn))
        }
      }

      "must go from add contact page" - {
        "when No selected" - {
          "to check your answers page" in {
            forAll(arbitraryTraderDetailsAnswersWithHolderOfTransitWithAdditionalContact) {
              answers =>
                val userAnswers = answers.setValue(AddContactPage, false)
                navigator
                  .nextPage(AddContactPage, mode, userAnswers)
                  .mustBe(tdRoutes.CheckYourAnswersController.onPageLoad(userAnswers.lrn))
            }
          }
        }

        "when Yes selected" - {
          "to contact name page" in {
            forAll(arbitraryTraderDetailsAnswersWithHolderOfTransitWithoutAdditionalContact) {
              answers =>
                val userAnswers = answers.setValue(AddContactPage, true)
                navigator
                  .nextPage(AddContactPage, mode, userAnswers)
                  .mustBe(contactRoutes.NameController.onPageLoad(userAnswers.lrn, mode))
            }
          }
        }
      }

      "must go from contact name page" - {
        "when telephone number exists" - {
          "to check your answers page" in {
            forAll(arbitraryTraderDetailsAnswersWithHolderOfTransitWithAdditionalContact) {
              answers =>
                navigator
                  .nextPage(contact.NamePage, mode, answers)
                  .mustBe(tdRoutes.CheckYourAnswersController.onPageLoad(answers.lrn))
            }
          }
        }

        "when no telephone number exists" - {
          "to contact telephone number page" in {
            forAll(arbitraryTraderDetailsAnswersWithHolderOfTransitWithAdditionalContact) {
              answers =>
                val userAnswers = answers.removeValue(contact.TelephoneNumberPage)
                navigator
                  .nextPage(contact.NamePage, mode, userAnswers)
                  .mustBe(contactRoutes.TelephoneNumberController.onPageLoad(userAnswers.lrn, mode))
            }
          }
        }
      }

      "must go from contact telephone number page to check your answers page" in {
        forAll(arbitraryTraderDetailsAnswersWithHolderOfTransitWithAdditionalContact) {
          answers =>
            navigator
              .nextPage(contact.TelephoneNumberPage, mode, answers)
              .mustBe(tdRoutes.CheckYourAnswersController.onPageLoad(answers.lrn))
        }
      }
    }
  }
}
