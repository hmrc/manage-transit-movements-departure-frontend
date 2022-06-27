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

package viewModels.traderDetails

import base.SpecBase
import generators.{Generators, PreTaskListUserAnswersGenerator, TraderDetailsUserAnswersGenerator}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

class TraderDetailsViewModelSpec
    extends SpecBase
    with ScalaCheckPropertyChecks
    with Generators
    with TraderDetailsUserAnswersGenerator
    with PreTaskListUserAnswersGenerator {

  private val viewModel = injector.instanceOf[TraderDetailsViewModel]

  "apply" - {
    "must return all sections" in {
      forAll(arbitraryPreTaskListAnswers) {
        preTaskListAnswers =>
          forAll(arbitraryTraderDetailsAnswers(preTaskListAnswers)) {
            answers =>
              val sections = viewModel.apply(answers)

              sections.size mustBe 6
              sections.head.sectionTitle.get mustBe "Transit holder"
              sections(1).sectionTitle.get mustBe "Additional contact"
              sections(2).sectionTitle.get mustBe "Representative"
              sections(3).sectionTitle.get mustBe "Consignor"
              sections(4).sectionTitle.get mustBe "Consignor contact"
              sections(5).sectionTitle.get mustBe "Consignee"
          }
      }
    }
  }
}
