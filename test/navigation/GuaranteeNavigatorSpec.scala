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
import controllers.guaranteeDetails.guarantee.{routes => guaranteeRoutes}
import controllers.guaranteeDetails.{routes => guaranteeDetailsRoutes}
import generators.{Generators, GuaranteeDetailsUserAnswersGenerator}
import models._
import models.GuaranteeType._
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.guaranteeDetails.guarantee.GuaranteeTypePage
import pages.preTaskList.DeclarationTypePage

class GuaranteeNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks with Generators with GuaranteeDetailsUserAnswersGenerator {

  private val navigator = new GuaranteeNavigator(index)

  "Guarantee Details Navigator" - {

    "when answers complete" - {
      "when not a single-page journey" - {
        "must redirect to check your answers" in {
          val declarationType = arbitrary[DeclarationType](arbitraryNonOption4DeclarationType).sample.value
          val guaranteeType   = arbitrary[GuaranteeType](arbitrary01234589GuaranteeType).sample.value
          val initialAnswers = emptyUserAnswers
            .setValue(DeclarationTypePage, declarationType)
            .setValue(GuaranteeTypePage(index), guaranteeType)

          forAll(arbitraryGuaranteeAnswers(initialAnswers, index), arbitrary[Mode]) {
            (answers, mode) =>
              navigator
                .nextPage(answers, mode)
                .mustBe(guaranteeRoutes.CheckYourAnswersController.onPageLoad(answers.lrn, index))
          }
        }
      }

      "when a single-page journey" - {
        "must redirect to add another" in {
          val declarationType = arbitrary[DeclarationType](arbitraryNonOption4DeclarationType).sample.value
          val guaranteeType   = arbitrary[GuaranteeType](arbitraryARGuaranteeType).sample.value
          val initialAnswers = emptyUserAnswers
            .setValue(DeclarationTypePage, declarationType)
            .setValue(GuaranteeTypePage(index), guaranteeType)

          forAll(arbitraryGuaranteeAnswers(initialAnswers, index), arbitrary[Mode]) {
            (answers, mode) =>
              navigator
                .nextPage(answers, mode)
                .mustBe(guaranteeDetailsRoutes.AddAnotherGuaranteeController.onPageLoad(answers.lrn))
          }
        }
      }
    }
  }
}
