/*
 * Copyright 2023 HM Revenue & Customs
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

package navigation.guaranteeDetails

import base.SpecBase
import controllers.guaranteeDetails.routes
import generators.Generators
import models.DeclarationType.Option4
import models._
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.preTaskList.DeclarationTypePage

class GuaranteeDetailsNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  "Guarantee Details Navigator" - {

    "when answers complete" - {
      "when TIR declaration type" - {
        "must redirect to 'TIR guarantee added' page" in {
          val initialAnswers = emptyUserAnswers.setValue(DeclarationTypePage, Option4)
          forAll(arbitraryGuaranteeDetailsAnswers(initialAnswers), arbitrary[Mode]) {
            (answers, mode) =>
              val navigatorProvider = new GuaranteeDetailsNavigatorProviderImpl()
              val navigator         = navigatorProvider.apply(mode)

              navigator
                .nextPage(answers)
                .mustBe(routes.GuaranteeAddedTIRController.onPageLoad(answers.lrn))
          }
        }
      }

      "when non-TIR declaration type" - {
        "must redirect to 'add another guarantee' page" in {
          forAll(arbitrary[DeclarationType](arbitraryNonOption4DeclarationType)) {
            declarationType =>
              val initialAnswers = emptyUserAnswers.setValue(DeclarationTypePage, declarationType)
              forAll(arbitraryGuaranteeDetailsAnswers(initialAnswers), arbitrary[Mode]) {
                (answers, mode) =>
                  val navigatorProvider = new GuaranteeDetailsNavigatorProviderImpl()
                  val navigator         = navigatorProvider.apply(mode)

                  navigator
                    .nextPage(answers)
                    .mustBe(routes.AddAnotherGuaranteeController.onPageLoad(answers.lrn))
              }
          }
        }
      }
    }
  }
}
