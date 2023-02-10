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

package viewModels.guaranteeDetails

import base.SpecBase
import generators.Generators
import models.Index
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import viewModels.guaranteeDetails.AddAnotherGuaranteeViewModel.AddAnotherGuaranteeViewModelProvider

class AddAnotherGuaranteeViewModelSpec extends SpecBase with Generators with ScalaCheckPropertyChecks {

  "must get list items" - {

    "when there is one guarantee" in {
      val userAnswers = arbitraryGuaranteeAnswers(emptyUserAnswers, index).sample.value

      val result = new AddAnotherGuaranteeViewModelProvider()(userAnswers)

      result.listItems.length mustBe 1
      result.title mustBe "You have added 1 guarantee"
      result.heading mustBe "You have added 1 guarantee"
      result.legend mustBe "Do you want to add another guarantee?"
      result.maxLimitLabel mustBe "You cannot add any more guarantees. To add another guarantee, you need to remove one first."
    }

    "when there are multiple guarantees" in {
      val formatter = java.text.NumberFormat.getIntegerInstance

      forAll(Gen.choose(2, frontendAppConfig.maxGuarantees)) {
        count =>
          val userAnswers = (0 until count).foldLeft(emptyUserAnswers) {
            (acc, i) =>
              arbitraryGuaranteeAnswers(acc, Index(i)).sample.value
          }

          val result = new AddAnotherGuaranteeViewModelProvider()(userAnswers)

          result.listItems.length mustBe count
          result.title mustBe s"You have added ${formatter.format(count)} guarantees"
          result.heading mustBe s"You have added ${formatter.format(count)} guarantees"
          result.legend mustBe "Do you want to add another guarantee?"
          result.maxLimitLabel mustBe "You cannot add any more guarantees. To add another guarantee, you need to remove one first."
      }
    }
  }

}
