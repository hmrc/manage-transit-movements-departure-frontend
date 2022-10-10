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
import controllers.traderDetails.routes
import generators.{Generators, TraderDetailsUserAnswersGenerator}
import models.Mode
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

class TraderDetailsNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks with Generators with TraderDetailsUserAnswersGenerator {

  "Trader Details Navigator" - {

    "when answers complete" - {
      "must redirect to check your answers" in {
        forAll(arbitraryTraderDetailsAnswers(emptyUserAnswers), arbitrary[Mode]) {
          (answers, mode) =>
            val navigator = new TraderDetailsNavigator(mode)

            navigator
              .nextPage(answers)
              .mustBe(routes.CheckYourAnswersController.onPageLoad(answers.lrn))
        }
      }
    }
  }
}
