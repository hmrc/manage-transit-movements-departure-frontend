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
import controllers.guaranteeDetails.{routes => gdRoutes}
import generators.{Generators, GuaranteeDetailsUserAnswersGenerator}
import models._
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.guaranteeDetails.GuaranteeTypePage

class GuaranteeNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks with Generators with GuaranteeDetailsUserAnswersGenerator {

  private val navigator = new GuaranteeNavigator(index)

  "Guarantee Details Navigator" - {

    val pageGen = Gen.const(
      GuaranteeTypePage(index)
    )

    "when in NormalMode" - {

      val mode = NormalMode

      "when answers complete" - {
        "must redirect to check your answers" in {
          forAll(arbitraryGuaranteeAnswers(emptyUserAnswers, index), pageGen) {
            (answers, page) =>
              navigator
                .nextPage(page, mode, answers)
                .mustBe(gdRoutes.CheckYourAnswersController.onPageLoad(answers.lrn, index))
          }
        }
      }
    }

    "when in CheckMode" - {

      val mode = CheckMode

      "when answers complete" - {
        "must redirect to check your answers" in {
          forAll(arbitraryGuaranteeAnswers(emptyUserAnswers, index), pageGen) {
            (answers, page) =>
              navigator
                .nextPage(page, mode, answers)
                .mustBe(gdRoutes.CheckYourAnswersController.onPageLoad(answers.lrn, index))
          }
        }
      }
    }
  }
}
