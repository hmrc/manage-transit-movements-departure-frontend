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
import controllers.routes
import controllers.traderDetails.representative.{routes => repRoutes}
import generators.{Generators, RepresentativeUserAnswersGenerator}
import models.{CheckMode, NormalMode, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.Page
import pages.traderDetails.representative._

class RepresentativeNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks with Generators with RepresentativeUserAnswersGenerator {

  private val navigator = new RepresentativeNavigator

  "Navigator" - {
    "must go from a page that doesn't exist in the route map" - {
      case object UnknownPage extends Page

      "when in check mode" - {
        "to session expired" in {
          navigator
            .nextPage(UnknownPage, CheckMode, emptyUserAnswers)
            .mustBe(routes.SessionExpiredController.onPageLoad())
        }
      }
    }

    "when in NormalMode" - {

      val mode = NormalMode

      "must go from ActingRepresentativePage" - {
        "when Yes selected" - {
          "to RepresentativeEori page" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val userAnswers = answers.setValue(ActingRepresentativePage, true)
                navigator
                  .nextPage(ActingRepresentativePage, mode, userAnswers)
                  .mustBe(repRoutes.RepresentativeEoriController.onPageLoad(userAnswers.lrn, mode))
            }
          }
        }

        "when No selected" - {
          "to ??? page" ignore {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val userAnswers = answers.setValue(ActingRepresentativePage, false)
                navigator
                  .nextPage(ActingRepresentativePage, mode, userAnswers)
                  .mustBe(repRoutes.RepresentativeEoriController.onPageLoad(userAnswers.lrn, mode)) //TODO redirect to next section when built
            }
          }
        }
      }

      "must go from RepresentativeEoriPage to RepresentativeName page" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            navigator
              .nextPage(RepresentativeEoriPage, mode, answers)
              .mustBe(repRoutes.RepresentativeNameController.onPageLoad(answers.lrn, mode))
        }
      }

      "must go from RepresentativeNamePage to Representative Capacity page" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            navigator
              .nextPage(RepresentativeNamePage, mode, answers)
              .mustBe(repRoutes.RepresentativeCapacityController.onPageLoad(answers.lrn, mode))
        }
      }

      "must go from RepresentativeCapacityPage to RepresentativePhone page" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            navigator
              .nextPage(RepresentativeCapacityPage, mode, answers)
              .mustBe(repRoutes.RepresentativePhoneController.onPageLoad(answers.lrn, mode))
        }
      }

      "must go from RepresentativePhonePage to CheckYourAnswers page" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            navigator
              .nextPage(RepresentativePhonePage, mode, answers)
              .mustBe(repRoutes.CheckYourAnswersController.onPageLoad(answers.lrn))
        }
      }
    }

    "when in CheckMode" - {

      val mode = CheckMode

      "must go from change ActingRepresentative" - {
        "when No selected" - {
          "to Task List page" in {
            forAll(arbitraryRepresentativeAnswersNotActingAsRepresentative) {
              answers =>
                navigator
                  .nextPage(ActingRepresentativePage, mode, answers)
                  .mustBe(controllers.routes.TaskListController.onPageLoad(answers.lrn)) //TODO change to next section when built
            }
          }
        }

        "when Yes selected" - {
          "to RepresentativeEoriPage" in {
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
}
