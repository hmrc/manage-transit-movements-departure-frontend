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

package viewModels.routeDetails.loadingAndUnloading

import base.SpecBase
import generators.Generators
import models.Mode
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.routeDetails.loadingAndUnloading
import pages.routeDetails.loadingAndUnloading._
import viewModels.routeDetails.loadingAndUnloading.LoadingAndUnloadingAnswersViewModel.LoadingAndUnloadingAnswersViewModelProvider

class LoadingAndUnloadingAnswersViewModelSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  "apply" - {

    "loading answers" - {

      "when adding a UN/LOCODE" - {
        "and not adding country and location" - {
          "must render 3 rows" in {
            val initialAnswers = emptyUserAnswers
              .setValue(loading.AddUnLocodeYesNoPage, true)
              .setValue(loading.AddExtraInformationYesNoPage, false)

            forAll(arbitraryLoadingAnswers(initialAnswers), arbitrary[Mode]) {
              (userAnswers, mode) =>
                val viewModelProvider = injector.instanceOf[LoadingAndUnloadingAnswersViewModelProvider]
                val section           = viewModelProvider.apply(userAnswers, mode).sections.head
                section.rows.size mustBe 3
                section.sectionTitle.value mustBe "Place of loading"
            }
          }
        }

        "and adding country and location" - {
          "must render 5 rows" in {
            val initialAnswers = emptyUserAnswers
              .setValue(loading.AddUnLocodeYesNoPage, true)
              .setValue(loading.AddExtraInformationYesNoPage, true)

            forAll(arbitraryLoadingAnswers(initialAnswers), arbitrary[Mode]) {
              (userAnswers, mode) =>
                val viewModelProvider = injector.instanceOf[LoadingAndUnloadingAnswersViewModelProvider]
                val section           = viewModelProvider.apply(userAnswers, mode).sections.head
                section.rows.size mustBe 5
                section.sectionTitle.value mustBe "Place of loading"
            }
          }
        }
      }

      "when not adding a UN/LOCODE" - {
        "must render 3 rows" in {
          val initialAnswers = emptyUserAnswers.setValue(loading.AddUnLocodeYesNoPage, false)

          forAll(arbitraryLoadingAnswers(initialAnswers), arbitrary[Mode]) {
            (userAnswers, mode) =>
              val viewModelProvider = injector.instanceOf[LoadingAndUnloadingAnswersViewModelProvider]
              val section           = viewModelProvider.apply(userAnswers, mode).sections.head
              section.rows.size mustBe 3
              section.sectionTitle.value mustBe "Place of loading"
          }
        }
      }
    }

    "unloading answers" - {

      "when not adding a place of unloading" in {
        val userAnswers = emptyUserAnswers.setValue(loadingAndUnloading.AddPlaceOfUnloadingPage, false)
        forAll(arbitrary[Mode]) {
          mode =>
            val viewModelProvider = injector.instanceOf[LoadingAndUnloadingAnswersViewModelProvider]
            val section           = viewModelProvider.apply(userAnswers, mode).sections.last
            section.rows.size mustBe 1
            section.sectionTitle.value mustBe "Place of unloading"
        }
      }

      "when adding a place of unloading" - {
        "when adding a UN/LOCODE" - {
          "and not adding country and location" - {
            "must render 4 rows" in {
              val initialAnswers = emptyUserAnswers
                .setValue(loadingAndUnloading.AddPlaceOfUnloadingPage, true)
                .setValue(unloading.UnLocodeYesNoPage, true)
                .setValue(unloading.AddExtraInformationYesNoPage, false)

              forAll(arbitraryUnloadingAnswers(initialAnswers), arbitrary[Mode]) {
                (userAnswers, mode) =>
                  val viewModelProvider = injector.instanceOf[LoadingAndUnloadingAnswersViewModelProvider]
                  val section           = viewModelProvider.apply(userAnswers, mode).sections.last
                  section.rows.size mustBe 4
                  section.sectionTitle.value mustBe "Place of unloading"
              }
            }
          }

          "and adding country and location" - {
            "must render 6 rows" in {
              val initialAnswers = emptyUserAnswers
                .setValue(loadingAndUnloading.AddPlaceOfUnloadingPage, true)
                .setValue(unloading.UnLocodeYesNoPage, true)
                .setValue(unloading.AddExtraInformationYesNoPage, true)

              forAll(arbitraryUnloadingAnswers(initialAnswers), arbitrary[Mode]) {
                (userAnswers, mode) =>
                  val viewModelProvider = injector.instanceOf[LoadingAndUnloadingAnswersViewModelProvider]
                  val section           = viewModelProvider.apply(userAnswers, mode).sections.last
                  section.rows.size mustBe 6
                  section.sectionTitle.value mustBe "Place of unloading"
              }
            }
          }
        }

        "when not adding a UN/LOCODE" - {
          "must render 4 rows" in {
            val initialAnswers = emptyUserAnswers
              .setValue(loadingAndUnloading.AddPlaceOfUnloadingPage, true)
              .setValue(unloading.UnLocodeYesNoPage, false)

            forAll(arbitraryUnloadingAnswers(initialAnswers), arbitrary[Mode]) {
              (userAnswers, mode) =>
                val viewModelProvider = injector.instanceOf[LoadingAndUnloadingAnswersViewModelProvider]
                val section           = viewModelProvider.apply(userAnswers, mode).sections.last
                section.rows.size mustBe 4
                section.sectionTitle.value mustBe "Place of unloading"
            }
          }
        }
      }
    }
  }
}
