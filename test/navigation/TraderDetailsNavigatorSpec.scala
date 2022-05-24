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

package navigation

import base.SpecBase
import controllers.preTaskList.{routes => ptlRoutes}
import controllers.routes
import controllers.traderDetails.holderOfTransit.{routes => hotRoutes}
import generators.Generators
import models._
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages._
import pages.traderDetails.holderOfTransit._

class TraderDetailsNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  private val navigator = new TraderDetailsNavigator

  "Navigator" - {
    "must go from a page that doesn't exist in the route map" - {

      case object UnknownPage extends Page

      "when in normal mode" - {
        "to start of the departure journey" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              navigator
                .nextPage(UnknownPage, NormalMode, answers)
                .mustBe(ptlRoutes.LocalReferenceNumberController.onPageLoad())
          }
        }
      }

      "when in check mode" - {
        "to session expired" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              navigator
                .nextPage(UnknownPage, CheckMode, answers)
                .mustBe(routes.SessionExpiredController.onPageLoad())
          }
        }
      }
    }

    "must go from Transit Holder EORI Yes No page" - {
      "when Yes selected" - {
        "to Eori page" in {
          forAll(arbitrary[UserAnswers], arbitrary[Mode]) {
            (answers, mode) =>
              val userAnswers = answers.setValue(EoriYesNoPage, true)
              navigator
                .nextPage(EoriYesNoPage, mode, userAnswers)
                .mustBe(hotRoutes.EoriController.onPageLoad(userAnswers.lrn, mode))
          }
        }
      }

      "when No selected" - {
        "to NamePage" in {
          forAll(arbitrary[UserAnswers], arbitrary[Mode]) {
            (answers, mode) =>
              val userAnswers = answers.setValue(EoriYesNoPage, false)
              navigator
                .nextPage(EoriYesNoPage, mode, userAnswers)
                .mustBe(hotRoutes.NameController.onPageLoad(userAnswers.lrn, mode))
          }
        }
      }

      "when nothing selected" - {
        "to session expired" in {
          forAll(arbitrary[UserAnswers], arbitrary[Mode]) {
            (answers, mode) =>
              val userAnswers = answers.removeValue(EoriYesNoPage)
              navigator
                .nextPage(EoriYesNoPage, mode, userAnswers)
                .mustBe(routes.SessionExpiredController.onPageLoad())
          }
        }
      }
    }

    "must go from EoriPage to NamePage" in {
      forAll(arbitrary[UserAnswers], arbitrary[Mode]) {
        (answers, mode) =>
          navigator
            .nextPage(EoriPage, mode, answers)
            .mustBe(hotRoutes.NameController.onPageLoad(answers.lrn, mode))
      }
    }

    "must go from TIR identification number Yes No page" - {
      "when Yes selected" - {
        "to TIR identification number page" ignore {
          forAll(arbitrary[UserAnswers], arbitrary[Mode]) {
            (answers, mode) =>
              val userAnswers = answers.setValue(TirIdentificationYesNoPage, true)
              navigator
                .nextPage(TirIdentificationYesNoPage, mode, userAnswers)
                .mustBe(???)
          }
        }
      }

      "when No selected" - {
        "to NamePage" in {
          forAll(arbitrary[UserAnswers], arbitrary[Mode]) {
            (answers, mode) =>
              val userAnswers = answers.setValue(TirIdentificationYesNoPage, false)
              navigator
                .nextPage(TirIdentificationYesNoPage, mode, userAnswers)
                .mustBe(hotRoutes.NameController.onPageLoad(userAnswers.lrn, mode))
          }
        }
      }

      "when nothing selected" - {
        "to session expired" in {
          forAll(arbitrary[UserAnswers], arbitrary[Mode]) {
            (answers, mode) =>
              val userAnswers = answers.removeValue(TirIdentificationYesNoPage)
              navigator
                .nextPage(TirIdentificationYesNoPage, mode, userAnswers)
                .mustBe(routes.SessionExpiredController.onPageLoad())
          }
        }
      }
    }

    "must go from Name page to Address page" in {
      forAll(arbitrary[UserAnswers], arbitrary[Mode]) {
        (answers, mode) =>
          navigator
            .nextPage(NamePage, mode, answers)
            .mustBe(hotRoutes.AddressController.onPageLoad(answers.lrn, mode))
      }
    }

    "must go from Address page to Add Contact page" in {
      forAll(arbitrary[UserAnswers], arbitrary[Mode]) {
        (answers, mode) =>
          navigator
            .nextPage(AddressPage, mode, answers)
            .mustBe(hotRoutes.AddContactController.onPageLoad(answers.lrn, mode))
      }
    }

    "must go from Transit Holder Add Contact page" - {
      //TODO - add nav test for true and false outcomes
      "when Yes selected" - {
        "???" ignore {
          forAll(arbitrary[UserAnswers], arbitrary[Mode]) {
            (answers, mode) =>
              val userAnswers = answers.setValue(AddContactPage, true)
              navigator
                .nextPage(AddContactPage, mode, userAnswers)
                .mustBe(hotRoutes.AddContactController.onPageLoad(userAnswers.lrn, mode))
          }
        }
      }

      "when No selected" - {
        "to ???" ignore {
          forAll(arbitrary[UserAnswers], arbitrary[Mode]) {
            (answers, mode) =>
              val userAnswers = answers.setValue(AddContactPage, false)
              navigator
                .nextPage(AddContactPage, mode, userAnswers)
                .mustBe(hotRoutes.AddContactController.onPageLoad(userAnswers.lrn, mode))
          }
        }
      }

      "when nothing selected" - {
        "to session expired" in {
          forAll(arbitrary[UserAnswers], arbitrary[Mode]) {
            (answers, mode) =>
              val userAnswers = answers.removeValue(AddContactPage)
              navigator
                .nextPage(AddContactPage, mode, userAnswers)
                .mustBe(routes.SessionExpiredController.onPageLoad())
          }
        }
      }
    }
  }
}
