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
import controllers.traderDetails.consignment.{routes => consignmentRoutes}
import controllers.traderDetails.{routes => tdRoutes}
import generators.{Generators, TraderDetailsUserAnswersGenerator}
import models._
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.traderDetails.consignment.{consignee, consignor, ApprovedOperatorPage}

class ConsignmentNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks with Generators with TraderDetailsUserAnswersGenerator {

  private val navigator = new ConsignmentNavigator

  "Consignment Navigator" - {

    val pageGen = Gen.oneOf(
      ApprovedOperatorPage,
      consignor.EoriYesNoPage,
      consignor.EoriPage,
      consignor.NamePage,
      consignor.AddressPage,
      consignor.AddContactPage,
      consignor.contact.NamePage,
      consignor.contact.TelephoneNumberPage,
      consignee.MoreThanOneConsigneePage,
      consignee.EoriYesNoPage,
      consignee.EoriNumberPage,
      consignee.NamePage,
      consignee.AddressPage
    )

    "when in NormalMode" - {

      val mode = NormalMode

      "when answers complete" - {
        "must redirect to check your answers" in {
          forAll(arbitraryConsignmentAnswers(emptyUserAnswers), pageGen) {
            (answers, page) =>
              navigator
                .nextPage(page, mode, answers)
                .mustBe(consignmentRoutes.CheckYourAnswersController.onPageLoad(answers.lrn))
          }
        }
      }
    }

    "when in CheckMode" - {

      val mode = CheckMode

      "when answers complete" - {
        "must redirect to check your answers" in {
          forAll(arbitraryTraderDetailsAnswers(emptyUserAnswers), pageGen) {
            (answers, page) =>
              navigator
                .nextPage(page, mode, answers)
                .mustBe(tdRoutes.CheckYourAnswersController.onPageLoad(answers.lrn))
          }
        }
      }
    }
  }
}
