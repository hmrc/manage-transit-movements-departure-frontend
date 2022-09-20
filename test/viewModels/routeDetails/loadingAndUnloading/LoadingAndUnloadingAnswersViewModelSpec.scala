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

package viewModels.routeDetails.loadingAndUnloading

import base.SpecBase
import generators.{Generators, RouteDetailsUserAnswersGenerator}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.routeDetails.loading._

class LoadingAndUnloadingAnswersViewModelSpec extends SpecBase with ScalaCheckPropertyChecks with Generators with RouteDetailsUserAnswersGenerator {

  "apply" - {

    "loading answers" - {
      "when adding a UN/LOCODE" - {
        "and not adding country and location" - {
          "must render 3 rows" in {
            val initialAnswers = emptyUserAnswers
              .setValue(PlaceOfLoadingAddUnLocodeYesNoPage, true)
              .setValue(PlaceOfLoadingAddExtraInformationYesNoPage, false)

            forAll(arbitraryLoadingAnswers(initialAnswers)) {
              userAnswers =>
                val section = LoadingAndUnloadingAnswersViewModel.apply(userAnswers).sections.head
                section.rows.size mustBe 3
                section.sectionTitle.value mustBe "Loading"
            }
          }
        }

        "and adding country and location" - {
          "must render 5 rows" in {
            val initialAnswers = emptyUserAnswers
              .setValue(PlaceOfLoadingAddUnLocodeYesNoPage, true)
              .setValue(PlaceOfLoadingAddExtraInformationYesNoPage, true)

            forAll(arbitraryLoadingAnswers(initialAnswers)) {
              userAnswers =>
                val section = LoadingAndUnloadingAnswersViewModel.apply(userAnswers).sections.head
                section.rows.size mustBe 5
                section.sectionTitle.value mustBe "Loading"
            }
          }
        }
      }

      "when not adding a UN/LOCODE" - {
        "must render 3 rows" in {
          val initialAnswers = emptyUserAnswers.setValue(PlaceOfLoadingAddUnLocodeYesNoPage, false)

          forAll(arbitraryLoadingAnswers(initialAnswers)) {
            userAnswers =>
              val section = LoadingAndUnloadingAnswersViewModel.apply(userAnswers).sections.head
              section.rows.size mustBe 3
              section.sectionTitle.value mustBe "Loading"
          }
        }
      }
    }

    "unloading answers" - {}
  }
}
