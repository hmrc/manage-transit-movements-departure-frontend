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

package services

import base.SpecBase
import connectors.CacheConnector
import generators.Generators
import models.LocalReferenceNumber
import models.SubmissionState.RejectedPendingChanges
import org.mockito.ArgumentMatchers.{any, eq => eqTo}
import org.mockito.Mockito.{reset, verify, verifyNoMoreInteractions, when}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.forAll
import viewModels.taskList.TaskStatus

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class DuplicateServiceSpec extends SpecBase with BeforeAndAfterEach with Generators {

  private val mockCacheConnector: CacheConnector = mock[CacheConnector]
  private val duplicateService: DuplicateService = new DuplicateService(mockCacheConnector)

  override def beforeEach(): Unit = {
    reset(mockCacheConnector)
    super.beforeEach()
  }

  "DuplicateService" - {

    "copyUserAnswers" - {

      val newLrn: LocalReferenceNumber = LocalReferenceNumber("DCBA0987654321321").value
      val oldLrnData                   = emptyUserAnswers.copy(lrn = lrn, tasks = Map("task1" -> TaskStatus.Error), status = RejectedPendingChanges)
      val newDataToSend                = oldLrnData.copy(lrn = newLrn)

      "must return true" - {
        "when answers in the cache can be found and data posts to cache" in {

          when(mockCacheConnector.get(eqTo(lrn))(any())) thenReturn Future.successful(Some(oldLrnData))
          when(mockCacheConnector.post(eqTo(newDataToSend))(any())) thenReturn Future.successful(true)

          duplicateService.copyUserAnswers(lrn, newLrn, RejectedPendingChanges).futureValue mustBe true

          verify(mockCacheConnector).get(eqTo(lrn))(any())
          verify(mockCacheConnector).post(eqTo(newDataToSend))(any())

        }
      }

      "must return false" - {

        "when answers not found in the cache" in {

          when(mockCacheConnector.get(eqTo(lrn))(any())) thenReturn Future.successful(None)

          duplicateService.copyUserAnswers(lrn, newLrn).futureValue mustBe false

          verify(mockCacheConnector).get(eqTo(lrn))(any())
          verifyNoMoreInteractions(mockCacheConnector)

        }

        "when answers found in the cache, but post fails" in {

          when(mockCacheConnector.get(eqTo(lrn))(any())) thenReturn Future.successful(Some(oldLrnData))
          when(mockCacheConnector.post(eqTo(newDataToSend))(any())) thenReturn Future.successful(false)

          duplicateService.copyUserAnswers(lrn, newLrn).futureValue mustBe false

          verify(mockCacheConnector).get(eqTo(lrn))(any())
          verify(mockCacheConnector).post(eqTo(newDataToSend))(any())
        }
      }

    }

    "alreadySubmitted" - {
      "must return correct boolean" - {

        "when local reference number" in {
          forAll(arbitrary[Boolean]) {
            isDuplicate =>
              when(mockCacheConnector.doesIE028ExistForLrn(eqTo(lrn))(any())).thenReturn(Future.successful(isDuplicate))

              duplicateService.alreadySubmitted(Some(lrn)).futureValue mustBe isDuplicate
          }
        }

        "when none" in {
          duplicateService.alreadySubmitted(None).futureValue mustBe false
        }

      }

    }

    "doesDraftOrSubmissionExistForLrn" - {
      "must return true if LRN is a duplicate" in {

        when(mockCacheConnector.doesDraftOrSubmissionExistForLrn(eqTo(lrn))(any())).thenReturn(Future.successful(true))

        duplicateService.doesDraftOrSubmissionExistForLrn(lrn).futureValue mustBe true

        verify(mockCacheConnector).doesDraftOrSubmissionExistForLrn(eqTo(lrn))(any())
      }

      "must return false if LRN is not a duplicate" in {
        when(mockCacheConnector.doesDraftOrSubmissionExistForLrn(eqTo(lrn))(any())).thenReturn(Future.successful(false))

        duplicateService.doesDraftOrSubmissionExistForLrn(lrn).futureValue mustBe false

        verify(mockCacheConnector).doesDraftOrSubmissionExistForLrn(eqTo(lrn))(any())
      }
    }

    "doesDraftOrSubmissionExistForLrn" - {
      "must return true if LRN exists in API" in {

        when(mockCacheConnector.doesIE028ExistForLrn(eqTo(lrn))(any())).thenReturn(Future.successful(true))

        duplicateService.doesIE028ExistForLrn(lrn).futureValue mustBe true

        verify(mockCacheConnector).doesIE028ExistForLrn(eqTo(lrn))(any())
      }

      "must return false if LRN does not exist in API" in {
        when(mockCacheConnector.doesIE028ExistForLrn(eqTo(lrn))(any())).thenReturn(Future.successful(false))

        duplicateService.doesIE028ExistForLrn(lrn).futureValue mustBe false

        verify(mockCacheConnector).doesIE028ExistForLrn(eqTo(lrn))(any())
      }
    }

  }
}
