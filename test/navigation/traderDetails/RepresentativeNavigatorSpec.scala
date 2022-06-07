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
import models.{CheckMode, Mode, NormalMode}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.Page
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

        "must go from ActingRepresentativePage" - {
          "when Yes selected" - {
            "to RepresentativeEori page" in {
              val userAnswers = emptyUserAnswers.setValue(ActingRepresentativePage, true)
              navigator
                .nextPage(ActingRepresentativePage, mode, userAnswers)
                .mustBe(repRoutes.RepresentativeEoriController.onPageLoad(userAnswers.lrn, mode))
            }
          }

          "when No selected" - {
            "to ???" ignore {
              val userAnswers = emptyUserAnswers.setValue(ActingRepresentativePage, false)
              navigator
                .nextPage(ActingRepresentativePage, mode, userAnswers)
                .mustBe(???) //TODO change to next section when built
            }
          }
        }

        "must go from RepresentativeEoriPage to RepresentativeName page" in {
          navigator
            .nextPage(RepresentativeEoriPage, mode, emptyUserAnswers)
            .mustBe(repRoutes.RepresentativeNameController.onPageLoad(emptyUserAnswers.lrn, mode))
        }

        "must go from RepresentativeNamePage to Representative Capacity page" in {
          navigator
            .nextPage(RepresentativeNamePage, mode, emptyUserAnswers)
            .mustBe(repRoutes.RepresentativeCapacityController.onPageLoad(emptyUserAnswers.lrn, mode))
        }

        "must go from RepresentativeCapacityPage to RepresentativePhone page" in {
          navigator
            .nextPage(RepresentativeCapacityPage, mode, emptyUserAnswers)
            .mustBe(repRoutes.RepresentativePhoneController.onPageLoad(emptyUserAnswers.lrn, mode))
        }

        "must go from RepresentativePhonePage to CheckYourAnswers page" in {
          navigator
            .nextPage(RepresentativePhonePage, mode, emptyUserAnswers)
            .mustBe(repRoutes.CheckYourAnswersController.onPageLoad(emptyUserAnswers.lrn))
        }
      }

      "when answers complete" - {

        "must go from change ActingRepresentative" - {
          "when No selected" - {
            "to ???" ignore {
              forAll(arbitraryRepresentativeAnswersNotActingAsRepresentative) {
                answers =>
                  navigator
                    .nextPage(ActingRepresentativePage, mode, answers)
                    .mustBe(???) //TODO change to next section when built
              }
            }
          }

          "when Yes selected" - {
            "to Check your answers page" in {
              forAll(arbitraryRepresentativeAnswersActingAsRepresentative) {
                answers =>
                  navigator
                    .nextPage(ActingRepresentativePage, mode, answers)
                    .mustBe(repRoutes.CheckYourAnswersController.onPageLoad(answers.lrn))
              }
            }
          }
        }

        "must go from RepresentativeEoriPage to Check your answers page" in {
          forAll(arbitraryRepresentativeAnswersActingAsRepresentative) {
            answers =>
              navigator
                .nextPage(RepresentativeEoriPage, mode, answers)
                .mustBe(repRoutes.CheckYourAnswersController.onPageLoad(answers.lrn))
          }
        }

        "must go from RepresentativeNamePage to Check your answers page" in {
          forAll(arbitraryRepresentativeAnswersActingAsRepresentative) {
            answers =>
              navigator
                .nextPage(RepresentativeNamePage, mode, answers)
                .mustBe(repRoutes.CheckYourAnswersController.onPageLoad(answers.lrn))
          }
        }

        "must go from RepresentativeCapacityPage to Check your answers page" in {
          forAll(arbitraryRepresentativeAnswersActingAsRepresentative) {
            answers =>
              navigator
                .nextPage(RepresentativeCapacityPage, mode, answers)
                .mustBe(repRoutes.CheckYourAnswersController.onPageLoad(answers.lrn))
          }
        }

        "must go from RepresentativePhonePage to Check your answers page" in {
          forAll(arbitraryRepresentativeAnswersActingAsRepresentative) {
            answers =>
              navigator
                .nextPage(RepresentativePhonePage, mode, answers)
                .mustBe(repRoutes.CheckYourAnswersController.onPageLoad(answers.lrn))
          }
        }
      }
    }

    "when in CheckMode" - {

      val mode = CheckMode

      "must go from change ActingRepresentative" - {
        "when No selected" - {
          "to ???" ignore {
            forAll(arbitraryTraderDetailsAnswersWithoutRepresentative) {
              answers =>
                navigator
                  .nextPage(ActingRepresentativePage, mode, answers)
                  .mustBe(???) //TODO change to next section when built
            }
          }
        }

        "when Yes selected" - {
          "to Check your answers page" in {
            forAll(arbitraryTraderDetailsAnswersWithRepresentative) {
              answers =>
                navigator
                  .nextPage(ActingRepresentativePage, mode, answers)
                  .mustBe(tdRoutes.CheckYourAnswersController.onPageLoad(answers.lrn))
            }
          }
        }
      }

      "must go from RepresentativeEoriPage to Check your answers page" in {
        forAll(arbitraryTraderDetailsAnswersWithRepresentative) {
          answers =>
            navigator
              .nextPage(RepresentativeEoriPage, mode, answers)
              .mustBe(tdRoutes.CheckYourAnswersController.onPageLoad(answers.lrn))
        }
      }

      "must go from RepresentativeNamePage to Check your answers page" in {
        forAll(arbitraryTraderDetailsAnswersWithRepresentative) {
          answers =>
            navigator
              .nextPage(RepresentativeNamePage, mode, answers)
              .mustBe(tdRoutes.CheckYourAnswersController.onPageLoad(answers.lrn))
        }
      }

      "must go from RepresentativeCapacityPage to Check your answers page" in {
        forAll(arbitraryTraderDetailsAnswersWithRepresentative) {
          answers =>
            navigator
              .nextPage(RepresentativeCapacityPage, mode, answers)
              .mustBe(tdRoutes.CheckYourAnswersController.onPageLoad(answers.lrn))
        }
      }

      "must go from RepresentativePhonePage to Check your answers page" in {
        forAll(arbitraryTraderDetailsAnswersWithRepresentative) {
          answers =>
            navigator
              .nextPage(RepresentativePhonePage, mode, answers)
              .mustBe(tdRoutes.CheckYourAnswersController.onPageLoad(answers.lrn))
        }
      }
    }
  }
}
