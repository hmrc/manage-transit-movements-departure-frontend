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

package viewModels.transport.authorisations

import base.SpecBase
import generators.Generators
import models.{Index, Mode}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import viewModels.transport.authorisations.AddAnotherAuthorisationViewModel.AddAnotherAuthorisationViewModelProvider

class AddAnotherAuthorisationViewModelSpec extends SpecBase with Generators with ScalaCheckPropertyChecks {

  "must get list items" - {

    "when there is one authorisation" in {
      forAll(arbitrary[Mode]) {
        mode =>
          val userAnswers = arbitraryAuthorisationAnswers(emptyUserAnswers, authorisationIndex).sample.value

          val result = new AddAnotherAuthorisationViewModelProvider()(userAnswers, mode)

          result.listItems.length mustBe 1
          result.title mustBe "You have added 1 authorisation"
          result.heading mustBe "You have added 1 authorisation"
          result.legend mustBe "Do you want to add another authorisation?"
          result.maxLimitLabel mustBe "You cannot add any more authorisations. To add another, you need to remove one first."
      }
    }

    "when there are multiple authorisations" in {
      val formatter = java.text.NumberFormat.getIntegerInstance

      forAll(arbitrary[Mode], Gen.choose(2, frontendAppConfig.maxAuthorisations)) {
        (mode, authorisations) =>
          val userAnswers = (0 until authorisations).foldLeft(emptyUserAnswers) {
            (acc, i) =>
              arbitraryAuthorisationAnswers(acc, Index(i)).sample.value
          }

          val result = new AddAnotherAuthorisationViewModelProvider()(userAnswers, mode)
          result.listItems.length mustBe authorisations
          result.title mustBe s"You have added ${formatter.format(authorisations)} authorisations"
          result.heading mustBe s"You have added ${formatter.format(authorisations)} authorisations"
          result.legend mustBe "Do you want to add another authorisation?"
          result.maxLimitLabel mustBe "You cannot add any more authorisations. To add another, you need to remove one first."
      }
    }
  }
}
