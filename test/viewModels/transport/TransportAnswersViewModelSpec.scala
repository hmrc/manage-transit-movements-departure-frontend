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

package viewModels.transport

import base.SpecBase
import generators.Generators
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import viewModels.transport.TransportAnswersViewModel.TransportAnswersViewModelProvider

class TransportAnswersViewModelSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  "apply" - {
    "must return all sections" in {
      forAll(arbitraryTransportAnswers(emptyUserAnswers)) {
        answers =>
          val viewModelProvider = injector.instanceOf[TransportAnswersViewModelProvider]
          val sections          = viewModelProvider.apply(answers).sections

          sections.size mustBe 0
      }
    }
  }
}
