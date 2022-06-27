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
import controllers.traderDetails.representative.{routes => repRoutes}
import controllers.traderDetails.{routes => tdRoutes}
import generators.{Generators, TraderDetailsUserAnswersGenerator}
import models.DeclarationType.{Option1, Option4}
import models.{CheckMode, Mode, NormalMode}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.Page
import pages.preTaskList.DeclarationTypePage
import pages.traderDetails.representative._

class RepresentativeNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks with Generators with TraderDetailsUserAnswersGenerator {

  private val navigator = new RepresentativeNavigator

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

        "must go from acting as representative page" - {
          "when Yes selected" - {
            "to eori page" in {
              val userAnswers = emptyUserAnswers.setValue(ActingAsRepresentativePage, true)
              navigator
                .nextPage(ActingAsRepresentativePage, mode, userAnswers)
                .mustBe(repRoutes.EoriController.onPageLoad(userAnswers.lrn, mode))
            }
          }

          "when No selected" - {
            "to consignorEoriPage when declarationType is TIR" in {
              forAll(arbitraryRepresentativeAnswersNotActingAsRepresentative) {
                answers =>
                  val updatedAnswers = answers.setValue(DeclarationTypePage, Option4)
                  navigator
                    .nextPage(ActingAsRepresentativePage, mode, updatedAnswers)
                    .mustBe(controllers.traderDetails.consignment.consignor.routes.EoriYesNoController.onPageLoad(updatedAnswers.lrn, mode))
              }
            }

            "to approvedOperatorPage when declarationType is not TIR" in {
              forAll(arbitraryRepresentativeAnswersNotActingAsRepresentative) {
                answers =>
                  val updatedAnswers = answers.setValue(DeclarationTypePage, Option1)
                  navigator
                    .nextPage(ActingAsRepresentativePage, mode, updatedAnswers)
                    .mustBe(controllers.traderDetails.consignment.routes.ApprovedOperatorController.onPageLoad(updatedAnswers.lrn, mode))
              }
            }
          }
        }

        "must go from eori page to name page" in {
          navigator
            .nextPage(EoriPage, mode, emptyUserAnswers)
            .mustBe(repRoutes.NameController.onPageLoad(emptyUserAnswers.lrn, mode))
        }

        "must go from name page to capacity page" in {
          navigator
            .nextPage(NamePage, mode, emptyUserAnswers)
            .mustBe(repRoutes.CapacityController.onPageLoad(emptyUserAnswers.lrn, mode))
        }

        "must go from capacity page to phone number page" in {
          navigator
            .nextPage(CapacityPage, mode, emptyUserAnswers)
            .mustBe(repRoutes.TelephoneNumberController.onPageLoad(emptyUserAnswers.lrn, mode))
        }

        "must go from RepresentativePhonePage to CheckYourAnswers page" in {
          navigator
            .nextPage(TelephoneNumberPage, mode, emptyUserAnswers)
            .mustBe(repRoutes.CheckYourAnswersController.onPageLoad(emptyUserAnswers.lrn))
        }
      }

      "when answers complete" - {

        "must go from acting as representative page" - {
          "when No selected" - {
            "to ???" ignore {
              forAll(arbitraryRepresentativeAnswersNotActingAsRepresentative) {
                answers =>
                  navigator
                    .nextPage(ActingAsRepresentativePage, mode, answers)
                    .mustBe(???) //TODO change to next section when built
              }
            }
          }

          "when Yes selected" - {
            "to check your answers page" in {
              forAll(arbitraryRepresentativeAnswersActingAsRepresentative) {
                answers =>
                  navigator
                    .nextPage(ActingAsRepresentativePage, mode, answers)
                    .mustBe(repRoutes.CheckYourAnswersController.onPageLoad(answers.lrn))
              }
            }
          }
        }

        "must go from eori page to check your answers page" in {
          forAll(arbitraryRepresentativeAnswersActingAsRepresentative) {
            answers =>
              navigator
                .nextPage(EoriPage, mode, answers)
                .mustBe(repRoutes.CheckYourAnswersController.onPageLoad(answers.lrn))
          }
        }

        "must go from name page to check your answers page" in {
          forAll(arbitraryRepresentativeAnswersActingAsRepresentative) {
            answers =>
              navigator
                .nextPage(NamePage, mode, answers)
                .mustBe(repRoutes.CheckYourAnswersController.onPageLoad(answers.lrn))
          }
        }

        "must go from capacity page to check your answers page" in {
          forAll(arbitraryRepresentativeAnswersActingAsRepresentative) {
            answers =>
              navigator
                .nextPage(CapacityPage, mode, answers)
                .mustBe(repRoutes.CheckYourAnswersController.onPageLoad(answers.lrn))
          }
        }

        "must go from phone number page to check your answers page" in {
          forAll(arbitraryRepresentativeAnswersActingAsRepresentative) {
            answers =>
              navigator
                .nextPage(TelephoneNumberPage, mode, answers)
                .mustBe(repRoutes.CheckYourAnswersController.onPageLoad(answers.lrn))
          }
        }
      }
    }

    "when in CheckMode" - {

      val mode = CheckMode

      "must go from acting as representative page" - {
        "when No selected" - {
          "to consignorEoriPage when declarationType is TIR" in {
            forAll(arbitraryRepresentativeAnswersNotActingAsRepresentative) {
              answers =>
                val updatedAnswers = answers
                  .setValue(DeclarationTypePage, Option4)
                navigator
                  .nextPage(ActingAsRepresentativePage, mode, updatedAnswers)
                  .mustBe(controllers.traderDetails.consignment.consignor.routes.EoriYesNoController.onPageLoad(updatedAnswers.lrn, mode))
            }
          }

          "to approvedOperatorPage when declarationType is not TIR" in {
            forAll(arbitraryRepresentativeAnswersNotActingAsRepresentative) {
              answers =>
                val updatedAnswers = answers
                  .setValue(DeclarationTypePage, Option1)
                navigator
                  .nextPage(ActingAsRepresentativePage, mode, updatedAnswers)
                  .mustBe(controllers.traderDetails.consignment.routes.ApprovedOperatorController.onPageLoad(updatedAnswers.lrn, mode))
            }
          }
        }

        "when Yes selected" - {
          "to check your answers page" in {
            forAll(arbitraryTraderDetailsAnswersWithRepresentative) {
              answers =>
                navigator
                  .nextPage(ActingAsRepresentativePage, mode, answers)
                  .mustBe(tdRoutes.CheckYourAnswersController.onPageLoad(answers.lrn))
            }
          }
        }
      }

      "must go from eori page to check your answers page" in {
        forAll(arbitraryTraderDetailsAnswersWithRepresentative) {
          answers =>
            navigator
              .nextPage(EoriPage, mode, answers)
              .mustBe(tdRoutes.CheckYourAnswersController.onPageLoad(answers.lrn))
        }
      }

      "must go from name page to check your answers page" in {
        forAll(arbitraryTraderDetailsAnswersWithRepresentative) {
          answers =>
            navigator
              .nextPage(NamePage, mode, answers)
              .mustBe(tdRoutes.CheckYourAnswersController.onPageLoad(answers.lrn))
        }
      }

      "must go from capacity page to check your answers page" in {
        forAll(arbitraryTraderDetailsAnswersWithRepresentative) {
          answers =>
            navigator
              .nextPage(CapacityPage, mode, answers)
              .mustBe(tdRoutes.CheckYourAnswersController.onPageLoad(answers.lrn))
        }
      }

      "must go from phone number page to check your answers page" in {
        forAll(arbitraryTraderDetailsAnswersWithRepresentative) {
          answers =>
            navigator
              .nextPage(TelephoneNumberPage, mode, answers)
              .mustBe(tdRoutes.CheckYourAnswersController.onPageLoad(answers.lrn))
        }
      }
    }
  }
}
