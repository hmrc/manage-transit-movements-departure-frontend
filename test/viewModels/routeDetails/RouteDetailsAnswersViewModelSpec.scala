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

package viewModels.routeDetails

import base.SpecBase
import generators.{Generators, RouteDetailsUserAnswersGenerator}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import viewModels.routeDetails.RouteDetailsAnswersViewModel.RouteDetailsAnswersViewModelProvider

class RouteDetailsAnswersViewModelSpec extends SpecBase with ScalaCheckPropertyChecks with Generators with RouteDetailsUserAnswersGenerator {

  private val viewModelProvider = injector.instanceOf[RouteDetailsAnswersViewModelProvider]

  "apply" - {
    "must return all sections" in {
      forAll(arbitraryRouteDetailsAnswers(emptyUserAnswers)) {
        answers =>
          val sections = viewModelProvider.apply(answers).sections

          sections.size mustBe 2
          sections.head.sectionTitle must not be defined
          sections(1).sectionTitle.get mustBe "Transit route countries"
      }
    }
  }
}
