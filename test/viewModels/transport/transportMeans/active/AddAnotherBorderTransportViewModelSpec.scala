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

package viewModels.transport.transportMeans.active

import base.SpecBase
import generators.Generators
import models.{Index, Mode}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import viewModels.transport.transportMeans.active.AddAnotherBorderTransportViewModel.AddAnotherBorderTransportViewModelProvider

class AddAnotherBorderTransportViewModelSpec extends SpecBase with Generators with ScalaCheckPropertyChecks {

  "must get list items" - {

    "when there is one incident" in {
      forAll(arbitrary[Mode]) {
        mode =>
          val userAnswers = arbitraryTransportMeansActiveAnswers(emptyUserAnswers, activeIndex).sample.value

          val result = new AddAnotherBorderTransportViewModelProvider()(userAnswers, mode)
          result.listItems.length mustBe 1
          result.title mustBe "You have added 1 border means of transport"
          result.heading mustBe "You have added 1 border means of transport"
          result.legend mustBe "Do you want to add another border means of transport?"
          result.hint mustBe "Only include vehicles that cross into another CTC country. As the EU is one CTC country, you don’t need to provide vehicle changes that stay within the EU."
          result.maxLimitLabel mustBe "You cannot add any more border means of transport. To add another, you need to remove one first."
      }
    }

    "when there are multiple incidents" in {
      val formatter = java.text.NumberFormat.getIntegerInstance

      forAll(arbitrary[Mode], Gen.choose(2, frontendAppConfig.maxActiveBorderTransports)) {
        (mode, activeBorderTransports) =>
          val userAnswers = (0 until activeBorderTransports).foldLeft(emptyUserAnswers) {
            (acc, i) =>
              arbitraryTransportMeansActiveAnswers(acc, Index(i)).sample.value
          }

          val result = new AddAnotherBorderTransportViewModelProvider()(userAnswers, mode)
          result.listItems.length mustBe activeBorderTransports
          result.title mustBe s"You have added ${formatter.format(activeBorderTransports)} border means of transport"
          result.heading mustBe s"You have added ${formatter.format(activeBorderTransports)} border means of transport"
          result.legend mustBe "Do you want to add another border means of transport?"
          result.hint mustBe "Only include vehicles that cross into another CTC country. As the EU is one CTC country, you don’t need to provide vehicle changes that stay within the EU."
          result.maxLimitLabel mustBe "You cannot add any more border means of transport. To add another, you need to remove one first."
      }
    }
  }
}
