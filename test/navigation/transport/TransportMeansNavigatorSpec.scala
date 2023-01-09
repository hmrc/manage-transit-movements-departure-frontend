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

package navigation.transport

import base.SpecBase
import generators.Generators
import models._
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

class TransportMeansNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  "Pre Requisites Navigator" - {

    "when in NormalMode" - {

      val mode              = NormalMode
      val navigatorProvider = new TransportMeansNavigatorProviderImpl()
      val navigator         = navigatorProvider.apply(mode)

      "when answers complete" - {
        "must redirect to transportMeans CYA" in {

          forAll(arbitraryTransportMeansAnswers(emptyUserAnswers)) {
            answers =>
              navigator
                .nextPage(answers)
                .mustBe(controllers.transport.transportMeans.routes.TransportMeansCheckYourAnswersController.onPageLoad(answers.lrn, mode))
          }
        }
      }

      "when in CheckMode" - {

        val mode              = CheckMode
        val navigatorProvider = new TransportMeansNavigatorProviderImpl()
        val navigator         = navigatorProvider.apply(mode)

        "when answers complete" - {
          "must redirect to transport check your answers" ignore {
            forAll(arbitraryTransportAnswers(emptyUserAnswers)) {
              answers =>
                navigator
                  .nextPage(answers)
                  .mustBe(???)
            }
          }
        }
      }
    }
  }
}
