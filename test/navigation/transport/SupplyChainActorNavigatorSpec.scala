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

package navigation.transport

import base.SpecBase
import generators.Generators
import models._
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

class SupplyChainActorNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  "Supply Chain Actor Navigator" - {

    "when in NormalMode" - {

      val mode              = NormalMode
      val navigatorProvider = new SupplyChainActorNavigatorProviderImpl
      val navigator         = navigatorProvider.apply(mode, actorIndex)

      "when answers complete" - {
        "must redirect to add another supply chain actor page" in {
          forAll(arbitrarySupplyChainActorAnswers(emptyUserAnswers, actorIndex)) {
            answers =>
              navigator
                .nextPage(answers)
                .mustBe(controllers.transport.supplyChainActors.routes.AddAnotherSupplyChainActorController.onPageLoad(lrn, mode))
          }
        }
      }
    }

    "when in CheckMode" - {

      val mode              = CheckMode
      val navigatorProvider = new SupplyChainActorNavigatorProviderImpl
      val navigator         = navigatorProvider.apply(mode, actorIndex)

      "when answers complete" - {
        "must redirect to transport check your answers" in {
          forAll(arbitraryTransportAnswers(emptyUserAnswers)) {
            answers =>
              navigator
                .nextPage(answers)
                .mustBe(controllers.transport.routes.TransportAnswersController.onPageLoad(answers.lrn))
          }
        }
      }
    }
  }
}
