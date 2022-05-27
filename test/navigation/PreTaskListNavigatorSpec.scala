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
import generators.{Generators, PreTaskListUserAnswersGenerator}
import models._
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages._
import pages.preTaskList._

class PreTaskListNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks with Generators with PreTaskListUserAnswersGenerator {

  private val navigator = new PreTaskListNavigator

  "Navigator" - {
    "must go from a page that doesn't exist in the route map" - {

      case object UnknownPage extends Page

      "when in normal mode" - {
        "to start of the departure journey" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              navigator
                .nextPage(UnknownPage, NormalMode, answers)
                .mustBe(routes.LocalReferenceNumberController.onPageLoad())
          }
        }
      }

      "when in check mode" - {
        "to session expired" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              navigator
                .nextPage(UnknownPage, CheckMode, answers)
                .mustBe(controllers.routes.SessionExpiredController.onPageLoad())
          }
        }
      }
    }

    "must go from Local Reference Number page to Office of Departure page" in {
      forAll(arbitrary[UserAnswers], arbitrary[Mode]) {
        (answers, mode) =>
          navigator
            .nextPage(LocalReferenceNumberPage, mode, answers)
            .mustBe(routes.OfficeOfDepartureController.onPageLoad(answers.lrn, mode))
      }
    }

    "must go from Office of Departure page to Procedure Type page" in {
      forAll(arbitrary[UserAnswers], arbitrary[Mode]) {
        (answers, mode) =>
          navigator
            .nextPage(OfficeOfDeparturePage, mode, answers)
            .mustBe(routes.ProcedureTypeController.onPageLoad(answers.lrn, mode))
      }
    }

    "must go from Procedure Type page to Declaration Type page" in {
      forAll(arbitrary[UserAnswers], arbitrary[Mode]) {
        (answers, mode) =>
          navigator
            .nextPage(ProcedureTypePage, mode, answers)
            .mustBe(routes.DeclarationTypeController.onPageLoad(answers.lrn, mode))
      }
    }

    "must go from Declaration Type page" - {
      "to Security Details Type page" - {
        "when not Normal procedure type and TIR declaration type" in {
          forAll(arbitrary[(UserAnswers, Mode, DeclarationType, ProcedureType)].suchThat {
            case (_, _, declarationType, procedureType) => !(declarationType == DeclarationType.Option4 && procedureType == ProcedureType.Normal)
          }) {
            case (answers, mode, declarationType, procedureType) =>
              val updatedAnswers = answers
                .setValue(ProcedureTypePage, procedureType)
                .setValue(DeclarationTypePage, declarationType)

              navigator
                .nextPage(DeclarationTypePage, mode, updatedAnswers)
                .mustBe(routes.SecurityDetailsTypeController.onPageLoad(answers.lrn, mode))
          }
        }
      }

      "to TIR Carnet Reference page" - {
        "when Normal procedure type and TIR declaration type" in {
          forAll(arbitrary[UserAnswers], arbitrary[Mode]) {
            (answers, mode) =>
              val updatedAnswers = answers
                .setValue(ProcedureTypePage, ProcedureType.Normal)
                .setValue(DeclarationTypePage, DeclarationType.Option4)

              navigator
                .nextPage(DeclarationTypePage, mode, updatedAnswers)
                .mustBe(routes.TIRCarnetReferenceController.onPageLoad(answers.lrn, mode))
          }
        }
      }
    }

    "must go from TIR Carnet Reference page to Security Details Type page" in {
      forAll(arbitrary[UserAnswers], arbitrary[Mode]) {
        (answers, mode) =>
          navigator
            .nextPage(TIRCarnetReferencePage, mode, answers)
            .mustBe(routes.SecurityDetailsTypeController.onPageLoad(answers.lrn, mode))
      }
    }

    "must go from Security Details Type page to Check Your Answers page" in {
      forAll(arbitrary[UserAnswers], arbitrary[Mode]) {
        (answers, mode) =>
          navigator
            .nextPage(SecurityDetailsTypePage, mode, answers)
            .mustBe(routes.CheckYourAnswersController.onPageLoad(answers.lrn))
      }
    }
  }
}
