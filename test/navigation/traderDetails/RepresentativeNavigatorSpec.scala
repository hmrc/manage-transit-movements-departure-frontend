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
import controllers.traderDetails.{routes => tdRoutes}
import generators.{Generators, TraderDetailsUserAnswersGenerator}
import models.DeclarationType.{Option1, Option2, Option3, Option4}
import models.{CheckMode, NormalMode}
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.preTaskList.DeclarationTypePage

class RepresentativeNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks with Generators with TraderDetailsUserAnswersGenerator {

  private val navigator = new RepresentativeNavigator

  "Representative Navigator" - {

    "when in NormalMode" - {

      val mode = NormalMode

      "when answers complete" - {
        "must redirect to ApprovedOperator page when declaration is type not TIR" in {
          forAll(arbitraryRepresentativeAnswers(emptyUserAnswers)) {
            answers =>
              val nonTir         = Gen.oneOf(Option1, Option2, Option3).sample.value
              val updatedAnswers = answers.setValue(DeclarationTypePage, nonTir)
              navigator
                .nextPage(updatedAnswers, mode)
                .mustBe(controllers.traderDetails.consignment.routes.ApprovedOperatorController.onPageLoad(updatedAnswers.lrn, NormalMode))
          }
        }

        "must redirect to EoriYesNo page when declaration is type TIR" in {
          forAll(arbitraryRepresentativeAnswers(emptyUserAnswers)) {
            answers =>
              val updatedAnswers = answers.setValue(DeclarationTypePage, Option4)
              navigator
                .nextPage(updatedAnswers, mode)
                .mustBe(controllers.traderDetails.consignment.consignor.routes.EoriYesNoController.onPageLoad(updatedAnswers.lrn, NormalMode))
          }
        }
      }
    }

    "when in CheckMode" - {

      val mode = CheckMode

      "when answers complete" - {
        "must redirect to check your answers" in {
          forAll(arbitraryTraderDetailsAnswers(emptyUserAnswers)) {
            answers =>
              navigator
                .nextPage(answers, mode)
                .mustBe(tdRoutes.CheckYourAnswersController.onPageLoad(answers.lrn))
          }
        }
      }
    }
  }
}
