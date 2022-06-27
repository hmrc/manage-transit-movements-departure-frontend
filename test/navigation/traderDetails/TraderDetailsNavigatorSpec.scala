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
import controllers.traderDetails.consignment.consignor.{routes => consignorRoutes}
import controllers.traderDetails.consignment.{routes => consignmentRoutes}
import controllers.traderDetails.representative.{routes => repRoutes}
import controllers.traderDetails.{routes => tdRoutes}
import generators.{Generators, PreTaskListUserAnswersGenerator, TraderDetailsUserAnswersGenerator}
import models.{CheckMode, NormalMode}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.traderDetails.ActingAsRepresentativePage

class TraderDetailsNavigatorSpec
    extends SpecBase
    with ScalaCheckPropertyChecks
    with Generators
    with TraderDetailsUserAnswersGenerator
    with PreTaskListUserAnswersGenerator {

  private val navigator = new TraderDetailsNavigator

  "Navigator" - {

    "when in NormalMode" - {

      val mode = NormalMode

      "must go from acting as representative page" - {
        "when Yes selected" - {
          "to eori page" in {
            forAll(arbitraryPreTaskListAnswers) {
              preTaskListAnswers =>
                forAll(arbitraryHolderOfTransitAnswers(preTaskListAnswers)) {
                  answers =>
                    val userAnswers = answers.setValue(ActingAsRepresentativePage, true)
                    navigator
                      .nextPage(ActingAsRepresentativePage, mode, userAnswers)
                      .mustBe(repRoutes.EoriController.onPageLoad(userAnswers.lrn, mode))
                }
            }
          }
        }

        "when No selected" - {
          "to consignorEoriYesNoPage when declarationType is TIR" in {
            forAll(arbitraryPreTaskListAnswersWithTir) {
              preTaskListAnswers =>
                forAll(arbitraryHolderOfTransitAnswers(preTaskListAnswers)) {
                  answers =>
                    val userAnswers = answers.setValue(ActingAsRepresentativePage, false)
                    navigator
                      .nextPage(ActingAsRepresentativePage, mode, userAnswers)
                      .mustBe(consignorRoutes.EoriYesNoController.onPageLoad(userAnswers.lrn, mode))
                }
            }
          }

          "to approvedOperatorPage when declarationType is not TIR" in {
            forAll(arbitraryPreTaskListAnswersWithoutTir) {
              preTaskListAnswers =>
                forAll(arbitraryHolderOfTransitAnswers(preTaskListAnswers)) {
                  answers =>
                    val userAnswers = answers.setValue(ActingAsRepresentativePage, false)
                    navigator
                      .nextPage(ActingAsRepresentativePage, mode, userAnswers)
                      .mustBe(consignmentRoutes.ApprovedOperatorController.onPageLoad(userAnswers.lrn, mode))
                }
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
