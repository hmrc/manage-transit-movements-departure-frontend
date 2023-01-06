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

package utils.cyaHelpers.authorisations

import base.SpecBase
import generators.Generators
import models.ProcedureType.Simplified
import models.transport.authorisations.AuthorisationType
import models.transport.transportMeans.departure.InlandMode
import models.{Index, Mode, NormalMode, ProcedureType}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.preTaskList.ProcedureTypePage
import pages.traderDetails.consignment.ApprovedOperatorPage
import pages.transport.authorisation.index.{AuthorisationReferenceNumberPage, AuthorisationTypePage}
import pages.transport.transportMeans.departure.InlandModePage
import utils.cyaHelpers.transport.authorisations.AuthorisationsAnswersHelper
import viewModels.ListItem

class AuthorisationsAnswersHelperSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  "AuthorisationsAnswersHelper" - {

    "when empty user answers" - {
      "must return empty list of list items" in {
        val userAnswers = emptyUserAnswers

        val helper = new AuthorisationsAnswersHelper(userAnswers, NormalMode)
        helper.listItems mustBe Nil
      }
    }

    "when user answers populated " - {

      "and reduced data set indicator is 1" - {

        //TODO: WHen nav is implemented remove this ignore
        "and inland mode is 1,2 or 4" in {
          val mode            = arbitrary[Mode].sample.value
          val procedureType   = arbitrary[ProcedureType].sample.value
          val referenceNumber = Gen.alphaNumStr.sample.value
          val inlandMode      = Gen.oneOf(Seq(InlandMode.Maritime, InlandMode.Rail, InlandMode.Air)).sample.value

          val answers = emptyUserAnswers
            .setValue(ApprovedOperatorPage, true)
            .setValue(ProcedureTypePage, procedureType)
            .setValue(InlandModePage, inlandMode)
            .setValue(AuthorisationReferenceNumberPage(Index(0)), referenceNumber)

          val helper = new AuthorisationsAnswersHelper(answers, mode)
          helper.listItems mustBe Seq(
            Right(
              ListItem(
                name = s"${AuthorisationType.TRD} - $referenceNumber",
                changeUrl = controllers.transport.authorisations.index.routes.AuthorisationTypeController.onPageLoad(lrn, mode, index).url,
                removeUrl = Some(controllers.transport.supplyChainActors.index.routes.RemoveSupplyChainActorController.onPageLoad(lrn, mode, Index(0)).url)
              )
            )
          )
        }

        "and inland mode is not 1,2 or 4" - {
          //TODO: WHen nav is implemented remove this ignore
          "and procedure type is simplified" in {

            val mode            = arbitrary[Mode].sample.value
            val referenceNumber = Gen.alphaNumStr.sample.value
            val inlandMode = Gen
              .oneOf(
                InlandMode.values
                  .filterNot(_ == InlandMode.Maritime)
                  .filterNot(_ == InlandMode.Rail)
                  .filterNot(_ == InlandMode.Air)
              )
              .sample
              .value

            val answers = emptyUserAnswers
              .setValue(ApprovedOperatorPage, true)
              .setValue(ProcedureTypePage, Simplified)
              .setValue(InlandModePage, inlandMode)
              .setValue(AuthorisationReferenceNumberPage(Index(0)), referenceNumber)

            val helper = new AuthorisationsAnswersHelper(answers, mode)
            helper.listItems mustBe Seq(
              Right(
                ListItem(
                  name = s"${AuthorisationType.ACR} - $referenceNumber",
                  changeUrl = controllers.transport.authorisations.index.routes.AuthorisationTypeController.onPageLoad(lrn, mode, index).url,
                  removeUrl = Some(controllers.transport.supplyChainActors.index.routes.RemoveSupplyChainActorController.onPageLoad(lrn, mode, Index(0)).url)
                )
              )
            )
          }

          "and procedure type is normal" in {

            val mode              = arbitrary[Mode].sample.value
            val referenceNumber   = Gen.alphaNumStr.sample.value
            val authorisationType = arbitrary[AuthorisationType].sample.value
            val inlandMode = Gen
              .oneOf(
                InlandMode.values
                  .filterNot(_ == InlandMode.Maritime)
                  .filterNot(_ == InlandMode.Rail)
                  .filterNot(_ == InlandMode.Air)
              )
              .sample
              .value

            val answers = emptyUserAnswers
              .setValue(ApprovedOperatorPage, true)
              .setValue(ProcedureTypePage, Simplified)
              .setValue(InlandModePage, inlandMode)
              .setValue(AuthorisationTypePage(Index(0)), authorisationType)
              .setValue(AuthorisationReferenceNumberPage(Index(0)), referenceNumber)

            val helper = new AuthorisationsAnswersHelper(answers, mode)
            helper.listItems mustBe Seq(
              Right(
                ListItem(
                  name = s"$authorisationType - $referenceNumber",
                  changeUrl = controllers.transport.authorisations.index.routes.AuthorisationTypeController.onPageLoad(lrn, mode, index).url,
                  removeUrl = Some(controllers.transport.supplyChainActors.index.routes.RemoveSupplyChainActorController.onPageLoad(lrn, mode, Index(0)).url)
                )
              )
            )
          }
        }
      }
    }
  }
}
