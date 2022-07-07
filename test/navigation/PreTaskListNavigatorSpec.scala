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
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.preTaskList._

class PreTaskListNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks with Generators with UserAnswersGenerator with PreTaskListUserAnswersGenerator {

  private val navigator = new PreTaskListNavigator

  "Pre Task List Navigator" - {

    val pageGen = Gen.oneOf(
      OfficeOfDeparturePage,
      ProcedureTypePage,
      DeclarationTypePage,
      TIRCarnetReferencePage,
      SecurityDetailsTypePage
    )

    "when answers complete" - {
      "must redirect to check your answers" in {
        forAll(arbitraryPreTaskListAnswers(emptyUserAnswers), pageGen, arbitrary[Mode]) {
          (answers, page, mode) =>
            navigator
              .nextPage(page, mode, answers)
              .mustBe(routes.CheckYourAnswersController.onPageLoad(answers.lrn))
        }
      }
    }
  }
}
