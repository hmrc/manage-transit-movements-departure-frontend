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
import generators.{Generators, UserAnswersGenerator}
import models._
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages._
import pages.guaranteeDetails.GuaranteeTypePage

class GuaranteeDetailsNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks with Generators with UserAnswersGenerator {

  private val navigator = new GuaranteeDetailsNavigator

  "Navigator" - {
    "must go from a page that doesn't exist in the route map" - {

      case object UnknownPage extends Page

      "to session expired" ignore {
        forAll(arbitrary[Mode]) {
          mode =>
            navigator
              .nextPage(UnknownPage, mode, emptyUserAnswers)
              .mustBe(controllers.routes.SessionExpiredController.onPageLoad())
        }
      }
      "when in normal mode" - {
        "must go from GuaranteeType page to ???" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              navigator
                .nextPage(GuaranteeTypePage, NormalMode, answers)
                .mustBe(controllers.routes.SessionExpiredController.onPageLoad()) //TODO change when next page built
          }
        }
      }

      "when in check mode" - {}
    }
  }
}
