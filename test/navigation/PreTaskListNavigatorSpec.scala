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
import controllers.preTaskList.routes
import generators.{Generators, PreTaskListUserAnswersGenerator, UserAnswersGenerator}
import models._
import models.reference.CustomsOffice
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.preTaskList._

class PreTaskListNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks with Generators with UserAnswersGenerator with PreTaskListUserAnswersGenerator {

  private val navigator = new PreTaskListNavigator

  "Navigator" - {

    "when in NormalMode" - {

      val mode = NormalMode

      "must go from Local Reference Number page to Office of Departure page" in {
        val userAnswers = emptyUserAnswers
        navigator
          .nextPage(LocalReferenceNumberPage, mode, userAnswers)
          .mustBe(routes.OfficeOfDepartureController.onPageLoad(userAnswers.lrn, mode))
      }

      "must go from Office of Departure page to Procedure Type page" in {
        forAll(arbitrary[CustomsOffice]) {
          customsOffice =>
            val userAnswers = emptyUserAnswers.setValue(OfficeOfDeparturePage, customsOffice)
            navigator
              .nextPage(OfficeOfDeparturePage, mode, userAnswers)
              .mustBe(routes.ProcedureTypeController.onPageLoad(userAnswers.lrn, mode))
        }
      }

      "must go from Procedure Type page to Declaration Type page" in {
        forAll(arbitrary[CustomsOffice], arbitrary[ProcedureType]) {
          (customsOffice, procedureType) =>
            val userAnswers = emptyUserAnswers
              .setValue(OfficeOfDeparturePage, customsOffice)
              .setValue(ProcedureTypePage, procedureType)
            navigator
              .nextPage(ProcedureTypePage, mode, userAnswers)
              .mustBe(routes.DeclarationTypeController.onPageLoad(userAnswers.lrn, mode))
        }
      }

      "must go from Declaration Type page" - {
        "to Security Details Type page" - {
          "when not Normal procedure type and TIR declaration type" in {
            forAll(arbitraryPreTaskListAnswers) {
              answers =>
                val userAnswers = answers
                  .removeValue(DetailsConfirmedPage)
                  .removeValue(SecurityDetailsTypePage)
                navigator
                  .nextPage(DeclarationTypePage, mode, userAnswers)
                  .mustBe(routes.SecurityDetailsTypeController.onPageLoad(userAnswers.lrn, mode))
            }
          }
        }

        "to TIR Carnet Reference page" - {
          "when Normal procedure type and TIR declaration type" in {
            forAll(arbitrary[CustomsOffice](arbitraryXiCustomsOffice)) {
              customsOffice =>
                val userAnswers = emptyUserAnswers
                  .setValue(OfficeOfDeparturePage, customsOffice)
                  .setValue(ProcedureTypePage, ProcedureType.Normal)
                  .setValue(DeclarationTypePage, DeclarationType.Option4)
                navigator
                  .nextPage(DeclarationTypePage, mode, userAnswers)
                  .mustBe(routes.TIRCarnetReferenceController.onPageLoad(userAnswers.lrn, mode))
            }
          }
        }
      }

      "must go from TIR Carnet Reference page to Security Details Type page" in {
        forAll(Gen.alphaNumStr, arbitrary[CustomsOffice](arbitraryXiCustomsOffice)) {
          (tirCarnetRef, customsOffice) =>
            val userAnswers = emptyUserAnswers
              .setValue(OfficeOfDeparturePage, customsOffice)
              .setValue(ProcedureTypePage, ProcedureType.Normal)
              .setValue(DeclarationTypePage, DeclarationType.Option4)
              .setValue(TIRCarnetReferencePage, tirCarnetRef)
            navigator
              .nextPage(TIRCarnetReferencePage, mode, userAnswers)
              .mustBe(routes.SecurityDetailsTypeController.onPageLoad(userAnswers.lrn, mode))
        }
      }

      "must go from Security Details Type page to Check Your Answers page" in {
        forAll(arbitraryPreTaskListAnswers) {
          answers =>
            val userAnswers = answers.removeValue(DetailsConfirmedPage)
            navigator
              .nextPage(SecurityDetailsTypePage, mode, userAnswers)
              .mustBe(routes.CheckYourAnswersController.onPageLoad(userAnswers.lrn))
        }
      }
    }

    "when in CheckMode" - {

      val mode = CheckMode

      "when TIR declaration type changes office of departure from XI to GB" - {
        "must go from Office of Departure page to Procedure Type page" in {
          forAll(arbitraryPreTaskListAnswersWithTir, arbitrary[CustomsOffice](arbitraryGbCustomsOffice)) {
            (answers, customsOffice) =>
              val userAnswers = answers
                .removeValue(DetailsConfirmedPage)
                .setValue(OfficeOfDeparturePage, customsOffice)
              navigator
                .nextPage(OfficeOfDeparturePage, mode, userAnswers)
                .mustBe(routes.DeclarationTypeController.onPageLoad(userAnswers.lrn, mode))
          }
        }
      }
    }
  }
}
