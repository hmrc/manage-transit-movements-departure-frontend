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

package utils.cyaHelpers.transport.supplyChainActors

import base.SpecBase
import controllers.transport.supplyChainActors.index.routes
import generators.Generators
import models.transport.supplyChainActors.SupplyChainActorType
import models.{Index, Mode}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.transport.supplyChainActors.SupplyChainActorYesNoPage
import pages.transport.supplyChainActors.index.{IdentificationNumberPage, SupplyChainActorTypePage}
import viewModels.ListItem

class SupplyChainActorsAnswersHelperSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  "SupplyChainActorsAnswersHelper" - {

    "listItems" - {

      "when empty user answers" - {
        "must return empty list of list items" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val userAnswers = emptyUserAnswers

              val helper = new SupplyChainActorsAnswersHelper(userAnswers, mode)
              helper.listItems mustBe Nil
          }
        }
      }

      "when user answers populated with complete supply chain actors" - {
        "must return list items with remove links" in {
          forAll(arbitrary[Mode], arbitrary[SupplyChainActorType], Gen.alphaNumStr) {
            (mode, actorRole, actorId) =>
              val userAnswers = emptyUserAnswers
                .setValue(SupplyChainActorYesNoPage, true)
                .setValue(SupplyChainActorTypePage(Index(0)), actorRole)
                .setValue(IdentificationNumberPage(Index(0)), actorId)
                .setValue(SupplyChainActorTypePage(Index(1)), actorRole)
                .setValue(IdentificationNumberPage(Index(1)), actorId)

              val helper = new SupplyChainActorsAnswersHelper(userAnswers, mode)
              helper.listItems mustBe Seq(
                Right(
                  ListItem(
                    name = s"${actorRole.asString} - $actorId",
                    changeUrl = routes.SupplyChainActorTypeController.onPageLoad(userAnswers.lrn, mode, Index(0)).url,
                    removeUrl = Some(routes.RemoveSupplyChainActorController.onPageLoad(userAnswers.lrn, mode, Index(0)).url)
                  )
                ),
                Right(
                  ListItem(
                    name = s"${actorRole.asString} - $actorId",
                    changeUrl = routes.SupplyChainActorTypeController.onPageLoad(userAnswers.lrn, mode, Index(1)).url,
                    removeUrl = Some(routes.RemoveSupplyChainActorController.onPageLoad(userAnswers.lrn, mode, Index(1)).url)
                  )
                )
              )
          }
        }
      }
    }
  }

}
