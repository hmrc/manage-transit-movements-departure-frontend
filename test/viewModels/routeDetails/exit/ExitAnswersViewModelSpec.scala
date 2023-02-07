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

package viewModels.routeDetails.exit

import base.SpecBase
import generators.Generators
import models.Mode
import models.reference.{Country, CustomsOffice}
import org.scalacheck.Arbitrary.arbitrary
import pages.routeDetails.exit.index.{OfficeOfExitCountryPage, OfficeOfExitPage}
import viewModels.Link
import viewModels.routeDetails.exit.ExitAnswersViewModel.ExitAnswersViewModelProvider

class ExitAnswersViewModelSpec extends SpecBase with Generators {

  "must return sections" in {
    val mode = arbitrary[Mode].sample.value

    val userAnswers = emptyUserAnswers
      .setValue(OfficeOfExitCountryPage(index), arbitrary[Country].sample.value)
      .setValue(OfficeOfExitPage(index), arbitrary[CustomsOffice].sample.value)

    val viewModelProvider = injector.instanceOf[ExitAnswersViewModelProvider]
    val sections          = viewModelProvider.apply(userAnswers, mode).sections

    sections.size mustBe 1

    sections.head.sectionTitle.get mustBe "Offices of exit"
    sections.head.rows.size mustBe 1
    sections.head.addAnotherLink.get mustBe Link(
      "add-or-remove-offices-of-exit",
      "Add or remove offices of exit",
      controllers.routeDetails.exit.routes.AddAnotherOfficeOfExitController.onPageLoad(userAnswers.lrn, mode).url
    )
  }
}
