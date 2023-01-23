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

package utils.cyaHelpers.authorisations

import base.SpecBase
import controllers.transport.authorisationsAndLimit.authorisations.index.routes
import forms.Constants.maxAuthorisationRefNumberLength
import generators.Generators
import models.ProcedureType.{Normal, Simplified}
import models.transport.authorisations.AuthorisationType
import models.transport.transportMeans.departure.InlandMode
import models.{DeclarationType, Index, Mode, NormalMode, ProcedureType}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.preTaskList.{DeclarationTypePage, ProcedureTypePage}
import pages.traderDetails.consignment.ApprovedOperatorPage
import pages.transport.authorisationsAndLimit.authorisations.index.{AuthorisationReferenceNumberPage, AuthorisationTypePage}
import pages.transport.transportMeans.departure.InlandModePage
import utils.cyaHelpers.transport.authorisations.AuthorisationsAnswersHelper
import viewModels.ListItem

class AuthorisationsAnswersHelperSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  private val mode                         = arbitrary[Mode].sample.value
  private val referenceNumber              = Gen.alphaNumStr.sample.value.take(maxAuthorisationRefNumberLength)
  private val authorisationTypeInlandModes = List(InlandMode.Maritime, InlandMode.Rail, InlandMode.Air)

  "AuthorisationsAnswersHelper" - {

    "when empty user answers" - {
      "must return empty list of list items" in {
        val userAnswers = emptyUserAnswers

        val helper = new AuthorisationsAnswersHelper(userAnswers, NormalMode)
        helper.listItems mustBe Nil
      }
    }

    "when user answers populated with a complete authorisation" - {

      "and reduced data set indicator is 1" - {

        "and inland mode is 1, 2 or 4" in {
          val inlandModeGen = Gen.oneOf(authorisationTypeInlandModes).sample.value

          forAll(arbitrary[ProcedureType], arbitrary[DeclarationType](arbitraryNonOption4DeclarationType), inlandModeGen) {
            (procedureType, declarationType, inlandMode) =>
              val answers = emptyUserAnswers
                .setValue(ProcedureTypePage, procedureType)
                .setValue(DeclarationTypePage, declarationType)
                .setValue(ApprovedOperatorPage, true)
                .setValue(InlandModePage, inlandMode)
                .setValue(AuthorisationReferenceNumberPage(Index(0)), referenceNumber)

              val helper = new AuthorisationsAnswersHelper(answers, mode)
              helper.listItems mustBe Seq(
                Right(
                  ListItem(
                    name = s"TRD - $referenceNumber",
                    changeUrl = routes.AuthorisationReferenceNumberController.onPageLoad(lrn, mode, authorisationIndex).url,
                    removeUrl = None
                  )
                )
              )
          }
        }

        "and inland mode is not 1, 2 or 4" - {

          val inlandModeGen = Gen.oneOf(InlandMode.values.diff(authorisationTypeInlandModes))

          "and procedure type is simplified" in {
            forAll(arbitrary[DeclarationType](arbitraryNonOption4DeclarationType), inlandModeGen) {
              (declarationType, inlandMode) =>
                val answers = emptyUserAnswers
                  .setValue(ProcedureTypePage, Simplified)
                  .setValue(DeclarationTypePage, declarationType)
                  .setValue(ApprovedOperatorPage, true)
                  .setValue(InlandModePage, inlandMode)
                  .setValue(AuthorisationReferenceNumberPage(Index(0)), referenceNumber)

                val helper = new AuthorisationsAnswersHelper(answers, mode)
                helper.listItems mustBe Seq(
                  Right(
                    ListItem(
                      name = s"ACR - $referenceNumber",
                      changeUrl = routes.AuthorisationReferenceNumberController.onPageLoad(lrn, mode, authorisationIndex).url,
                      removeUrl = None
                    )
                  )
                )
            }
          }

          "and procedure type is normal" in {
            forAll(arbitrary[DeclarationType](arbitraryNonOption4DeclarationType), inlandModeGen, arbitrary[AuthorisationType]) {
              (declarationType, inlandMode, authorisationType) =>
                val answers = emptyUserAnswers
                  .setValue(ProcedureTypePage, Normal)
                  .setValue(DeclarationTypePage, declarationType)
                  .setValue(ApprovedOperatorPage, true)
                  .setValue(InlandModePage, inlandMode)
                  .setValue(AuthorisationTypePage(Index(0)), authorisationType)
                  .setValue(AuthorisationReferenceNumberPage(Index(0)), referenceNumber)

                val helper = new AuthorisationsAnswersHelper(answers, mode)
                helper.listItems mustBe Seq(
                  Right(
                    ListItem(
                      name = s"${authorisationType.forDisplay} - $referenceNumber",
                      changeUrl = routes.AuthorisationReferenceNumberController.onPageLoad(lrn, mode, authorisationIndex).url,
                      removeUrl = Some(routes.RemoveAuthorisationYesNoController.onPageLoad(lrn, mode, authorisationIndex).url)
                    )
                  )
                )
            }
          }
        }
      }
    }

    "when user answers populated with an in progress authorisation" in {
      val inlandModeGen = Gen.oneOf(InlandMode.values.diff(authorisationTypeInlandModes))
      forAll(arbitrary[DeclarationType](arbitraryNonOption4DeclarationType), inlandModeGen, arbitrary[AuthorisationType]) {
        (declarationType, inlandMode, authorisationType) =>
          val answers = emptyUserAnswers
            .setValue(ProcedureTypePage, Normal)
            .setValue(DeclarationTypePage, declarationType)
            .setValue(ApprovedOperatorPage, true)
            .setValue(InlandModePage, inlandMode)
            .setValue(AuthorisationTypePage(Index(0)), authorisationType)

          val helper = new AuthorisationsAnswersHelper(answers, mode)
          helper.listItems mustBe Seq(
            Left(
              ListItem(
                name = authorisationType.forDisplay,
                changeUrl = routes.AuthorisationReferenceNumberController.onPageLoad(lrn, mode, authorisationIndex).url,
                removeUrl = Some(routes.RemoveAuthorisationYesNoController.onPageLoad(lrn, mode, authorisationIndex).url)
              )
            )
          )
      }
    }
  }
}
