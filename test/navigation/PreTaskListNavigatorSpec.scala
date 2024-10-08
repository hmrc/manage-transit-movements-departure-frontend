/*
 * Copyright 2024 HM Revenue & Customs
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
import generators.Generators
import models._
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

class PreTaskListNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  "Pre Task List Navigator" - {

    "when answers complete" - {
      "must redirect to check your answers" in {
        forAll(arbitraryPreTaskListAnswers(emptyUserAnswers), arbitrary[Mode]) {
          (answers, mode) =>
            val navigatorProvider = new PreTaskListNavigatorProviderImpl(frontendAppConfig)
            val navigator         = navigatorProvider.apply(mode)

            navigator
              .nextPage(answers, None)
              .mustBe(routes.CheckYourAnswersController.onPageLoad(answers.lrn))
        }
      }
    }
  }
}
