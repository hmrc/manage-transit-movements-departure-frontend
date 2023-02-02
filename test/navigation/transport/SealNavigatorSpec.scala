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

class SealNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  "Seal Navigator" - {

    "when in NormalMode" - {

      val mode              = NormalMode
      val navigatorProvider = new SealNavigatorProviderImpl
      val navigator         = navigatorProvider.apply(mode, equipmentIndex, sealIndex)

      "when answers complete" - {
        "must redirect to add another seal page" in {
          forAll(arbitrarySealAnswers(emptyUserAnswers, equipmentIndex, sealIndex)) {
            answers =>
              navigator
                .nextPage(answers)
                .mustBe(controllers.transport.equipment.index.routes.AddAnotherSealController.onPageLoad(lrn, mode, equipmentIndex))
          }
        }
      }
    }

    "when in CheckMode" - {

      val mode              = CheckMode
      val navigatorProvider = new SealNavigatorProviderImpl
      val navigator         = navigatorProvider.apply(mode, equipmentIndex, sealIndex)

      "when answers complete" - {
        "must redirect to transport answers" ignore {
          forAll(arbitrarySealAnswers(emptyUserAnswers, equipmentIndex, sealIndex)) {
            answers =>
              navigator
                .nextPage(answers)
                .mustBe(???) // TODO: Transport CYA page
          }
        }
      }
    }
  }
}
