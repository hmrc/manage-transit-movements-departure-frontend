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

package models.journeyDomain.transport.authorisationsAndLimit.authorisations

import base.SpecBase
import forms.Constants.maxAuthorisationRefNumberLength
import generators.Generators
import models.ProcedureType.{Normal, Simplified}
import models.domain.{EitherType, UserAnswersReader}
import models.transport.authorisations.AuthorisationType
import models.transport.transportMeans.departure.InlandMode
import models.{DeclarationType, Index, ProcedureType}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.forAll
import pages.preTaskList.{DeclarationTypePage, ProcedureTypePage}
import pages.traderDetails.consignment.ApprovedOperatorPage
import pages.transport.authorisationsAndLimit.authorisations.index.{AuthorisationReferenceNumberPage, AuthorisationTypePage}
import pages.transport.transportMeans.departure.InlandModePage

class AuthorisationDomainSpec extends SpecBase with Generators {

  "AuthorisationDomain" - {

    val referenceNumber              = Gen.alphaNumStr.sample.value.take(maxAuthorisationRefNumberLength)
    val authorisationTypeInlandModes = List(InlandMode.Maritime, InlandMode.Rail, InlandMode.Air)

    "can be parsed from UserAnswers" - {

      "when DeclarationType is TIR (reduced data set is 0)" in {
        val inlandModeGen = Gen.oneOf(InlandMode.values.diff(authorisationTypeInlandModes))

        forAll(inlandModeGen, arbitrary[AuthorisationType]) {
          (inlandMode, authorisationType) =>
            val userAnswers = emptyUserAnswers
              .setValue(ProcedureTypePage, Normal)
              .setValue(DeclarationTypePage, DeclarationType.Option4)
              .setValue(InlandModePage, inlandMode)
              .setValue(AuthorisationTypePage(Index(0)), authorisationType)
              .setValue(AuthorisationReferenceNumberPage(Index(0)), referenceNumber)

            val expectedResult = AuthorisationDomain(
              authorisationType = authorisationType,
              referenceNumber = referenceNumber
            )(authorisationIndex)

            val result: EitherType[AuthorisationDomain] =
              UserAnswersReader[AuthorisationDomain](AuthorisationDomain.userAnswersReader(authorisationIndex))
                .run(userAnswers)

            result.value mustBe expectedResult
        }
      }

      "when reduced data set indicator is 1" - {
        val declarationTypeGen = arbitrary[DeclarationType](arbitraryNonOption4DeclarationType)

        "and inland mode is 1,2 or 4" in {
          val inlandModeGen = Gen.oneOf(authorisationTypeInlandModes)

          forAll(inlandModeGen, arbitrary[ProcedureType], declarationTypeGen) {
            (inlandMode, procedureType, declarationType) =>
              val userAnswers = emptyUserAnswers
                .setValue(ProcedureTypePage, procedureType)
                .setValue(DeclarationTypePage, declarationType)
                .setValue(ApprovedOperatorPage, true)
                .setValue(InlandModePage, inlandMode)
                .setValue(AuthorisationReferenceNumberPage(Index(0)), referenceNumber)

              val expectedResult = AuthorisationDomain(
                authorisationType = AuthorisationType.TRD,
                referenceNumber = referenceNumber
              )(authorisationIndex)

              val result: EitherType[AuthorisationDomain] = UserAnswersReader[AuthorisationDomain](AuthorisationDomain.userAnswersReader(authorisationIndex))
                .run(userAnswers)

              result.value mustBe expectedResult
          }
        }

        "and inland mode is not 1,2 or 4" - {
          val inlandModeGen = Gen.oneOf(InlandMode.values.diff(authorisationTypeInlandModes))
          "and procedure type is simplified" in {

            forAll(inlandModeGen, declarationTypeGen) {
              (inlandMode, declarationType) =>
                val userAnswers = emptyUserAnswers
                  .setValue(ProcedureTypePage, Simplified)
                  .setValue(DeclarationTypePage, declarationType)
                  .setValue(ApprovedOperatorPage, true)
                  .setValue(InlandModePage, inlandMode)
                  .setValue(AuthorisationReferenceNumberPage(Index(0)), referenceNumber)

                val expectedResult = AuthorisationDomain(
                  authorisationType = AuthorisationType.ACR,
                  referenceNumber = referenceNumber
                )(authorisationIndex)

                val result: EitherType[AuthorisationDomain] =
                  UserAnswersReader[AuthorisationDomain](AuthorisationDomain.userAnswersReader(authorisationIndex))
                    .run(userAnswers)

                result.value mustBe expectedResult
            }
          }

          "and procedure type is normal" in {
            forAll(inlandModeGen, arbitrary[AuthorisationType], declarationTypeGen) {
              (inlandMode, authorisationType, declarationType) =>
                val userAnswers = emptyUserAnswers
                  .setValue(ProcedureTypePage, Normal)
                  .setValue(DeclarationTypePage, declarationType)
                  .setValue(ApprovedOperatorPage, true)
                  .setValue(InlandModePage, inlandMode)
                  .setValue(AuthorisationTypePage(Index(0)), authorisationType)
                  .setValue(AuthorisationReferenceNumberPage(Index(0)), referenceNumber)

                val expectedResult = AuthorisationDomain(
                  authorisationType = authorisationType,
                  referenceNumber = referenceNumber
                )(authorisationIndex)

                val result: EitherType[AuthorisationDomain] =
                  UserAnswersReader[AuthorisationDomain](AuthorisationDomain.userAnswersReader(authorisationIndex))
                    .run(userAnswers)

                result.value mustBe expectedResult
            }
          }
        }
      }

      "when reduced data set indicator is 0" in {

        forAll(arbitrary[ProcedureType], arbitrary[InlandMode], arbitrary[AuthorisationType], arbitrary[DeclarationType](arbitraryNonOption4DeclarationType)) {
          (procedureType, inlandMode, authorisationType, declarationType) =>
            val userAnswers = emptyUserAnswers
              .setValue(ProcedureTypePage, procedureType)
              .setValue(DeclarationTypePage, declarationType)
              .setValue(ApprovedOperatorPage, false)
              .setValue(InlandModePage, inlandMode)
              .setValue(AddAuthorisationsYesNoPage, true)
              .setValue(AuthorisationTypePage(Index(0)), authorisationType)
              .setValue(AuthorisationReferenceNumberPage(Index(0)), referenceNumber)

            val expectedResult = AuthorisationDomain(
              authorisationType = authorisationType,
              referenceNumber = referenceNumber
            )(authorisationIndex)

            val result: EitherType[AuthorisationDomain] =
              UserAnswersReader[AuthorisationDomain](AuthorisationDomain.userAnswersReader(authorisationIndex))
                .run(userAnswers)

            result.value mustBe expectedResult
        }

      }
    }

    "cannot be parsed from user answers" - {

      "and reduced data set is 0" - {
        "must go to authorisation type page" in {
          forAll(arbitrary[ProcedureType], arbitrary[InlandMode], arbitrary[DeclarationType](arbitraryNonOption4DeclarationType)) {
            (procedureType, inlandMode, declarationType) =>
              val userAnswers = emptyUserAnswers
                .setValue(ProcedureTypePage, procedureType)
                .setValue(DeclarationTypePage, declarationType)
                .setValue(ApprovedOperatorPage, false)
                .setValue(InlandModePage, inlandMode)
                .setValue(AddAuthorisationsYesNoPage, true)

              val result: EitherType[AuthorisationDomain] = UserAnswersReader[AuthorisationDomain](
                AuthorisationDomain.userAnswersReader(index)
              ).run(userAnswers)

              result.left.value.page mustBe AuthorisationTypePage(index)
          }
        }
      }

      "and DeclarationType is TIR (reduced data set is 0)" - {
        "must go to authorisation type" in {
          val inlandModeGen = Gen.oneOf(authorisationTypeInlandModes)
          forAll(inlandModeGen) {
            inlandMode =>
              val userAnswers = emptyUserAnswers
                .setValue(ProcedureTypePage, Normal)
                .setValue(DeclarationTypePage, DeclarationType.Option4)
                .setValue(InlandModePage, inlandMode)

              val result: EitherType[AuthorisationDomain] = UserAnswersReader[AuthorisationDomain](
                AuthorisationDomain.userAnswersReader(index)
              ).run(userAnswers)

              result.left.value.page mustBe AuthorisationTypePage(index)
          }
        }
      }

      "and reduced data set indicator is 1" - {
        val declarationTypeGen = arbitrary[DeclarationType](arbitraryNonOption4DeclarationType)

        "and inland mode is 1,2 or 4 " - {
          "must bypass authorisation type and go to authorisation reference number" in {
            val inlandModeGen = Gen.oneOf(authorisationTypeInlandModes)
            forAll(inlandModeGen, declarationTypeGen) {
              (inlandMode, declarationType) =>
                val userAnswers = emptyUserAnswers
                  .setValue(ProcedureTypePage, Normal)
                  .setValue(DeclarationTypePage, declarationType)
                  .setValue(ApprovedOperatorPage, true)
                  .setValue(InlandModePage, inlandMode)

                val result: EitherType[AuthorisationDomain] = UserAnswersReader[AuthorisationDomain](
                  AuthorisationDomain.userAnswersReader(index)
                ).run(userAnswers)

                result.left.value.page mustBe AuthorisationReferenceNumberPage(index)
            }
          }
        }

        "and inland mode is not 1,2 or 4" - {
          "and procedure type is simplified" - {
            "must bypass authorisation type and go to authorisation reference number" in {
              val inlandModeGen = Gen.oneOf(InlandMode.values.diff(authorisationTypeInlandModes))
              forAll(inlandModeGen, declarationTypeGen) {
                (inlandMode, declarationType) =>
                  val userAnswers = emptyUserAnswers
                    .setValue(ProcedureTypePage, Simplified)
                    .setValue(DeclarationTypePage, declarationType)
                    .setValue(ApprovedOperatorPage, true)
                    .setValue(InlandModePage, inlandMode)

                  val result: EitherType[AuthorisationDomain] = UserAnswersReader[AuthorisationDomain](
                    AuthorisationDomain.userAnswersReader(index)
                  ).run(userAnswers)

                  result.left.value.page mustBe AuthorisationReferenceNumberPage(index)
              }
            }
          }
        }
      }
    }
  }
}
