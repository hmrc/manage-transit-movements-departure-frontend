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

package controllers.actions

import base.{AppWithDefaultMockFixtures, SpecBase}
import controllers.routes
import generators.Generators
import models.requests.{DataRequest, OptionalDataRequest}
import models.{AllowList, LocalReferenceNumber, SubmissionState, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatest.{Assertion, EitherValues}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.libs.json.Json
import play.api.mvc.Result
import play.api.test.Helpers._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class DataRequiredActionSpec extends SpecBase with EitherValues with AppWithDefaultMockFixtures with ScalaCheckPropertyChecks with Generators {

  private class Harness(
    lrn: LocalReferenceNumber,
    ignoreSubmissionState: Boolean,
    allowList: Option[AllowList]
  ) extends DataRequiredAction(lrn, ignoreSubmissionState, allowList) {
    def callRefine[A](request: OptionalDataRequest[A]): Future[Either[Result, DataRequest[A]]] = refine(request)
  }

  "Data Required Action" - {

    "when there is no allow list" - {
      "when not ignoring submission status" - {

        val ignoreSubmissionState = false

        "when there are no UserAnswers" - {

          "must return Left and redirect to session expired" in {

            val harness = new Harness(lrn, ignoreSubmissionState, None)

            val result = harness.callRefine(OptionalDataRequest(fakeRequest, eoriNumber, None)).map(_.left.value)

            status(result) mustBe 303
            redirectLocation(result).value mustBe routes.SessionExpiredController.onPageLoad(lrn).url
          }
        }

        "when there are UserAnswers" - {

          "and answers have previously been submitted" - {
            "must return Left and redirect to session expired" in {
              val userAnswers = UserAnswers(lrn, eoriNumber, Json.obj(), status = SubmissionState.Submitted)

              val harness = new Harness(lrn, ignoreSubmissionState, None)

              val result = harness.callRefine(OptionalDataRequest(fakeRequest, eoriNumber, Some(userAnswers))).map(_.left.value)

              status(result) mustBe 303
              redirectLocation(result).value mustBe routes.SessionExpiredController.onPageLoad(lrn).url
            }
          }

          "and answers have not previously been submitted" - {
            "must return Right with DataRequest" in {
              forAll(Gen.oneOf(SubmissionState.NotSubmitted, SubmissionState.Amendment)) {
                submissionStatus =>
                  val userAnswers = UserAnswers(lrn = lrn, eoriNumber = eoriNumber, status = submissionStatus)

                  val harness = new Harness(lrn, ignoreSubmissionState, None)

                  val result = harness.callRefine(OptionalDataRequest(fakeRequest, eoriNumber, Some(userAnswers)))

                  whenReady[Either[Result, DataRequest[_]], Assertion](result) {
                    result =>
                      result.value.userAnswers mustBe userAnswers
                      result.value.eoriNumber mustBe eoriNumber
                  }
              }
            }
          }
        }
      }

      "when ignoring submission status" - {

        val ignoreSubmissionState = true

        "when there are no UserAnswers" - {

          "must return Left and redirect to session expired" in {

            val harness = new Harness(lrn, ignoreSubmissionState, None)

            val result = harness.callRefine(OptionalDataRequest(fakeRequest, eoriNumber, None)).map(_.left.value)

            status(result) mustBe 303
            redirectLocation(result).value mustBe routes.SessionExpiredController.onPageLoad(lrn).url
          }
        }

        "when there are UserAnswers" - {

          "must return Right with DataRequest" in {
            forAll(arbitrary[SubmissionState.Value]) {
              submissionStatus =>
                val userAnswers = UserAnswers(lrn, eoriNumber, status = submissionStatus)

                val harness = new Harness(lrn, ignoreSubmissionState, None)

                val result = harness.callRefine(OptionalDataRequest(fakeRequest, eoriNumber, Some(userAnswers)))

                whenReady[Either[Result, DataRequest[_]], Assertion](result) {
                  result =>
                    result.value.userAnswers mustBe userAnswers
                    result.value.eoriNumber mustBe eoriNumber
                }
            }
          }
        }
      }
    }

    "when there is an allow list" - {
      val ignoreSubmissionState = true

      "and eori is allowed" - {
        val allowList = AllowList(Seq(eoriNumber.value))

        "must return Right with DataRequest" in {
          forAll(arbitrary[SubmissionState.Value]) {
            submissionStatus =>
              val userAnswers = UserAnswers(lrn, eoriNumber, status = submissionStatus)

              val harness = new Harness(lrn, ignoreSubmissionState, Some(allowList))

              val result = harness.callRefine(OptionalDataRequest(fakeRequest, eoriNumber, Some(userAnswers)))

              whenReady[Either[Result, DataRequest[_]], Assertion](result) {
                result =>
                  result.value.userAnswers mustBe userAnswers
                  result.value.eoriNumber mustBe eoriNumber
              }
          }
        }
      }

      "and eori is not allowed" - {
        "must return Left with service unavailable" in {
          forAll(arbitrary[SubmissionState.Value], nonEmptyString) {
            (submissionStatus, eori) =>
              val allowList = AllowList(Seq(eori))

              val userAnswers = UserAnswers(lrn, eoriNumber, status = submissionStatus)

              val harness = new Harness(lrn, ignoreSubmissionState, Some(allowList))

              val result = harness.callRefine(OptionalDataRequest(fakeRequest, eoriNumber, Some(userAnswers))).map(_.left.value)

              status(result) mustBe 503
          }
        }
      }
    }
  }
}
