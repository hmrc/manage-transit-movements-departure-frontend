/*
 * Copyright 2024 HM Revenue & Customs
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
import connectors.CacheConnector
import controllers.routes
import models.requests.{DataRequest, OptionalDataRequest}
import models.{DepartureMessage, DepartureMessages, LocalReferenceNumber, SubmissionState, UserAnswers}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, when}
import org.scalacheck.Gen
import org.scalatest.{Assertion, EitherValues}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.libs.json.Json
import play.api.mvc.Result
import play.api.test.Helpers._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class DataRequiredActionSpec extends SpecBase with EitherValues with AppWithDefaultMockFixtures with ScalaCheckPropertyChecks {

  private class Harness(
    cacheConnector: CacheConnector
  )(
    lrn: LocalReferenceNumber,
    ignoreSubmissionState: Boolean
  ) extends DataRequiredAction(cacheConnector)(lrn, ignoreSubmissionState) {
    def callRefine[A](request: OptionalDataRequest[A]): Future[Either[Result, DataRequest[A]]] = refine(request)
  }

  private val mockCacheConnector = mock[CacheConnector]

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockCacheConnector)
  }

  "Data Required Action" - {

    "when not ignoring submission status" - {

      val ignoreSubmissionState = false

      "when there are no UserAnswers" - {

        "must return Left and redirect to session expired" in {
          when(mockCacheConnector.getMessages(any())(any()))
            .thenReturn(Future.successful(DepartureMessages()))

          val harness = new Harness(mockCacheConnector)(lrn, ignoreSubmissionState)

          val result = harness.callRefine(OptionalDataRequest(fakeRequest, eoriNumber, None)).map(_.left.value)

          status(result) mustBe 303
          redirectLocation(result).value mustBe routes.SessionExpiredController.onPageLoad(lrn).url
        }
      }

      "when there are UserAnswers" - {

        "and answers have previously been submitted" - {
          "must return Left and redirect to session expired" in {
            when(mockCacheConnector.getMessages(any())(any()))
              .thenReturn(Future.successful(DepartureMessages()))

            val userAnswers = UserAnswers(lrn, eoriNumber, Json.obj(), status = SubmissionState.Submitted)

            val harness = new Harness(mockCacheConnector)(lrn, ignoreSubmissionState)

            val result = harness.callRefine(OptionalDataRequest(fakeRequest, eoriNumber, Some(userAnswers))).map(_.left.value)

            status(result) mustBe 303
            redirectLocation(result).value mustBe routes.SessionExpiredController.onPageLoad(lrn).url
          }
        }

        "and answers have not previously been submitted" - {
          "must return Right with DataRequest" in {
            forAll(Gen.oneOf(SubmissionState.NotSubmitted, SubmissionState.Amendment)) {
              submissionStatus =>
                beforeEach()

                when(mockCacheConnector.getMessages(any())(any()))
                  .thenReturn(Future.successful(DepartureMessages()))

                val userAnswers = UserAnswers(lrn = lrn, eoriNumber = eoriNumber, status = submissionStatus)

                val harness = new Harness(mockCacheConnector)(lrn, ignoreSubmissionState)

                val result = harness.callRefine(OptionalDataRequest(fakeRequest, eoriNumber, Some(userAnswers)))

                whenReady[Either[Result, DataRequest[?]], Assertion](result) {
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
          when(mockCacheConnector.getMessages(any())(any()))
            .thenReturn(Future.successful(DepartureMessages()))

          val harness = new Harness(mockCacheConnector)(lrn, ignoreSubmissionState)

          val result = harness.callRefine(OptionalDataRequest(fakeRequest, eoriNumber, None)).map(_.left.value)

          status(result) mustBe 303
          redirectLocation(result).value mustBe routes.SessionExpiredController.onPageLoad(lrn).url
        }
      }

      "when there are UserAnswers" - {

        "and amending" - {
          val submissionStatusGen = Gen.oneOf(SubmissionState.RejectedPendingChanges, SubmissionState.Amendment, SubmissionState.GuaranteeAmendment)

          "and messages includes IE029" - {
            "must return Left and redirect to session expired" in {
              forAll(submissionStatusGen) {
                submissionStatus =>
                  beforeEach()

                  val departureMessages = DepartureMessages(
                    Seq(
                      DepartureMessage("IE015"),
                      DepartureMessage("IE928"),
                      DepartureMessage("IE028"),
                      DepartureMessage("IE029")
                    )
                  )

                  when(mockCacheConnector.getMessages(any())(any()))
                    .thenReturn(Future.successful(departureMessages))

                  val userAnswers = UserAnswers(lrn, eoriNumber, status = submissionStatus)

                  val harness = new Harness(mockCacheConnector)(lrn, ignoreSubmissionState)

                  val result = harness.callRefine(OptionalDataRequest(fakeRequest, eoriNumber, Some(userAnswers))).map(_.left.value)

                  status(result) mustBe 303
                  redirectLocation(result).value mustBe routes.SessionExpiredController.onPageLoad(lrn).url
              }
            }
          }

          "and messages doesn't include IE029" - {
            "must return Right with DataRequest" in {
              forAll(submissionStatusGen) {
                submissionStatus =>
                  beforeEach()

                  val departureMessages = DepartureMessages(
                    Seq(
                      DepartureMessage("IE015"),
                      DepartureMessage("IE928"),
                      DepartureMessage("IE028")
                    )
                  )

                  when(mockCacheConnector.getMessages(any())(any()))
                    .thenReturn(Future.successful(departureMessages))

                  val userAnswers = UserAnswers(lrn, eoriNumber, status = submissionStatus)

                  val harness = new Harness(mockCacheConnector)(lrn, ignoreSubmissionState)

                  val result = harness.callRefine(OptionalDataRequest(fakeRequest, eoriNumber, Some(userAnswers)))

                  whenReady[Either[Result, DataRequest[?]], Assertion](result) {
                    result =>
                      result.value.userAnswers mustBe userAnswers
                      result.value.eoriNumber mustBe eoriNumber
                  }
              }
            }
          }
        }

        "and not amending" - {
          "must return Right with DataRequest" in {
            when(mockCacheConnector.getMessages(any())(any()))
              .thenReturn(Future.successful(DepartureMessages()))

            val submissionStatus = SubmissionState.NotSubmitted

            val userAnswers = UserAnswers(lrn, eoriNumber, status = submissionStatus)

            val harness = new Harness(mockCacheConnector)(lrn, ignoreSubmissionState)

            val result = harness.callRefine(OptionalDataRequest(fakeRequest, eoriNumber, Some(userAnswers)))

            whenReady[Either[Result, DataRequest[?]], Assertion](result) {
              result =>
                result.value.userAnswers mustBe userAnswers
                result.value.eoriNumber mustBe eoriNumber
            }
          }
        }

        "and already submitted" - {
          "must return Right with DataRequest" in {
            val submissionStatus = SubmissionState.Submitted

            val userAnswers = UserAnswers(lrn, eoriNumber, status = submissionStatus)

            val harness = new Harness(mockCacheConnector)(lrn, ignoreSubmissionState)

            val result = harness.callRefine(OptionalDataRequest(fakeRequest, eoriNumber, Some(userAnswers)))

            whenReady[Either[Result, DataRequest[?]], Assertion](result) {
              result =>
                result.value.userAnswers mustBe userAnswers
                result.value.eoriNumber mustBe eoriNumber
            }
          }
        }
      }
    }
  }
}
