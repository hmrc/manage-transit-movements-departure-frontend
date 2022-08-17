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

package navigation.routeDetails

import base.SpecBase
import generators.{Generators, RouteDetailsUserAnswersGenerator}
import models._
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

class TransitNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks with Generators with RouteDetailsUserAnswersGenerator {

  private val navigator = new TransitNavigator(ctcCountryCodes, euCountryCodes, customsSecurityAgreementAreaCountryCodes)

  "Transit Navigator" - {

    "when in NormalMode" - {

      val mode = NormalMode

      "when answers complete" - {
        "must redirect to transit check your answers" ignore {
          forAll(arbitraryTransitAnswers(emptyUserAnswers)) {
            answers =>
              navigator
                .nextPage(answers, mode)
                .mustBe(controllers.routeDetails.transit.routes.AddAnotherOfficeOfTransitController.onPageLoad(answers.lrn))
          }
        }
      }
    }

    "when in CheckMode" - {

      val mode = CheckMode

      "when answers complete" - {
        "must redirect to route details check your answers" ignore {
          forAll(arbitraryRouteDetailsAnswers(emptyUserAnswers)) {
            answers =>
              navigator
                .nextPage(answers, mode)
                .mustBe(???)
          }
        }
      }
    }
  }
}
