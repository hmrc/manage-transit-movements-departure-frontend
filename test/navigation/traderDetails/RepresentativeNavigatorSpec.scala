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

package navigation.traderDetails

import base.SpecBase
import controllers.traderDetails.representative.{routes => repRoutes}
import controllers.traderDetails.{routes => tdRoutes}
import generators.{Generators, TraderDetailsUserAnswersGenerator}
import models.traderDetails.representative.RepresentativeCapacity
import models.{CheckMode, NormalMode}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.traderDetails.representative._

class RepresentativeNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks with Generators with TraderDetailsUserAnswersGenerator {

  private val navigator = new RepresentativeNavigator

  "Navigator" - {

    "when in NormalMode" - {

      val mode = NormalMode

      "when answers incomplete" - {

        "must go from eori page to name page" in {
          forAll(Gen.alphaNumStr) {
            eori =>
              val userAnswers = emptyUserAnswers.setValue(EoriPage, eori)
              navigator
                .nextPage(EoriPage, mode, userAnswers)
                .mustBe(repRoutes.NameController.onPageLoad(userAnswers.lrn, mode))
          }
        }

        "must go from name page to capacity page" in {
          forAll(Gen.alphaNumStr, Gen.alphaNumStr) {
            (eori, name) =>
              val userAnswers = emptyUserAnswers
                .setValue(EoriPage, eori)
                .setValue(NamePage, name)
              navigator
                .nextPage(NamePage, mode, userAnswers)
                .mustBe(repRoutes.CapacityController.onPageLoad(userAnswers.lrn, mode))
          }
        }

        "must go from capacity page to phone number page" in {
          forAll(Gen.alphaNumStr, Gen.alphaNumStr, arbitrary[RepresentativeCapacity]) {
            (eori, name, capacity) =>
              val userAnswers = emptyUserAnswers
                .setValue(EoriPage, eori)
                .setValue(NamePage, name)
                .setValue(CapacityPage, capacity)
              navigator
                .nextPage(CapacityPage, mode, userAnswers)
                .mustBe(repRoutes.TelephoneNumberController.onPageLoad(userAnswers.lrn, mode))
          }
        }

        "must go from RepresentativePhonePage to CheckYourAnswers page" in {
          forAll(Gen.alphaNumStr, Gen.alphaNumStr, arbitrary[RepresentativeCapacity], Gen.alphaNumStr) {
            (eori, name, capacity, telephoneNumber) =>
              val userAnswers = emptyUserAnswers
                .setValue(EoriPage, eori)
                .setValue(NamePage, name)
                .setValue(CapacityPage, capacity)
                .setValue(TelephoneNumberPage, telephoneNumber)
              navigator
                .nextPage(TelephoneNumberPage, mode, userAnswers)
                .mustBe(repRoutes.CheckYourAnswersController.onPageLoad(userAnswers.lrn))
          }
        }
      }

      "when answers complete" - {

        "must go from eori page to check your answers page" in {
          forAll(arbitraryRepresentativeAnswersActingAsRepresentative) {
            answers =>
              navigator
                .nextPage(EoriPage, mode, answers)
                .mustBe(repRoutes.CheckYourAnswersController.onPageLoad(answers.lrn))
          }
        }

        "must go from name page to check your answers page" in {
          forAll(arbitraryRepresentativeAnswersActingAsRepresentative) {
            answers =>
              navigator
                .nextPage(NamePage, mode, answers)
                .mustBe(repRoutes.CheckYourAnswersController.onPageLoad(answers.lrn))
          }
        }

        "must go from capacity page to check your answers page" in {
          forAll(arbitraryRepresentativeAnswersActingAsRepresentative) {
            answers =>
              navigator
                .nextPage(CapacityPage, mode, answers)
                .mustBe(repRoutes.CheckYourAnswersController.onPageLoad(answers.lrn))
          }
        }

        "must go from phone number page to check your answers page" in {
          forAll(arbitraryRepresentativeAnswersActingAsRepresentative) {
            answers =>
              navigator
                .nextPage(TelephoneNumberPage, mode, answers)
                .mustBe(repRoutes.CheckYourAnswersController.onPageLoad(answers.lrn))
          }
        }
      }
    }

    "when in CheckMode" - {

      val mode = CheckMode

      "must go from eori page to check your answers page" in {
        forAll(arbitraryTraderDetailsAnswersWithRepresentative) {
          answers =>
            navigator
              .nextPage(EoriPage, mode, answers)
              .mustBe(tdRoutes.CheckYourAnswersController.onPageLoad(answers.lrn))
        }
      }

      "must go from name page to check your answers page" in {
        forAll(arbitraryTraderDetailsAnswersWithRepresentative) {
          answers =>
            navigator
              .nextPage(NamePage, mode, answers)
              .mustBe(tdRoutes.CheckYourAnswersController.onPageLoad(answers.lrn))
        }
      }

      "must go from capacity page to check your answers page" in {
        forAll(arbitraryTraderDetailsAnswersWithRepresentative) {
          answers =>
            navigator
              .nextPage(CapacityPage, mode, answers)
              .mustBe(tdRoutes.CheckYourAnswersController.onPageLoad(answers.lrn))
        }
      }

      "must go from phone number page to check your answers page" in {
        forAll(arbitraryTraderDetailsAnswersWithRepresentative) {
          answers =>
            navigator
              .nextPage(TelephoneNumberPage, mode, answers)
              .mustBe(tdRoutes.CheckYourAnswersController.onPageLoad(answers.lrn))
        }
      }
    }
  }
}
