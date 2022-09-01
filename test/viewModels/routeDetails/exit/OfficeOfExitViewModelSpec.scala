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

package viewModels.routeDetails.exit

import base.SpecBase
import generators.{Generators, RouteDetailsUserAnswersGenerator}
import models.reference.Country
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.routeDetails.exit.index.OfficeOfExitCountryPage

class OfficeOfExitViewModelSpec extends SpecBase with ScalaCheckPropertyChecks with Generators with RouteDetailsUserAnswersGenerator {

  "apply" - {

    "must return row for each answer" - {

      "must return 1 rows" in {
        forAll(arbitrary[Country]) {
          country =>
            val answers = emptyUserAnswers
              .setValue(OfficeOfExitCountryPage(index), country)

            val section = OfficeOfExitViewModel.apply(answers, index).section

            section.sectionTitle mustNot be(defined)
            section.rows.size mustBe 1
        }

      }
    }
  }
}
