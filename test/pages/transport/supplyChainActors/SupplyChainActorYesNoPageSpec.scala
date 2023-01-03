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

package pages.transport.supplyChainActors

import org.scalacheck.Arbitrary._
import pages.behaviours.PageBehaviours
import pages.sections.transport.SupplyChainActorListSection
import play.api.libs.json.{JsArray, Json}

class SupplyChainActorYesNoPageSpec extends PageBehaviours {

  "SupplyChainActorYesNoPage" - {

    beRetrievable[Boolean](SupplyChainActorYesNoPage)

    beSettable[Boolean](SupplyChainActorYesNoPage)

    beRemovable[Boolean](SupplyChainActorYesNoPage)
  }

  "cleanup" - {
    "when no is selected" - {
      "must remove supply chain actors section" in {
        val userAnswers = emptyUserAnswers
          .setValue(SupplyChainActorListSection, JsArray(Seq(Json.obj("foo" -> "bar"))))

        val result = userAnswers.setValue(SupplyChainActorYesNoPage, false)

        result.get(SupplyChainActorListSection) mustNot be(defined)
      }
    }
  }
}
