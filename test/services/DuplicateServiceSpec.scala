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
import forms.NewLocalReferenceNumberFormProvider
import generators.Generators
import models.LocalReferenceNumber
import models.SubmissionState.RejectedPendingChanges
import org.mockito.ArgumentMatchers.{any, eq => eqTo}
import org.mockito.Mockito.{reset, verify, verifyNoInteractions, verifyNoMoreInteractions, when}
import org.scalatest.BeforeAndAfterEach
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.forAll
import viewModels.taskList.TaskStatus

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class DuplicateServiceSpec extends SpecBase with BeforeAndAfterEach with Generators {

  private val formProvider = new NewLocalReferenceNumberFormProvider()

  private val mockCacheConnector: CacheConnector = mock[CacheConnector]
  private val duplicateService: DuplicateService = new DuplicateService(mockCacheConnector, formProvider)

  override def beforeEach(): Unit = {
    reset(mockCacheConnector)
    super.beforeEach()
  }

  "DuplicateService" - {

    "copyUserAnswers" - {

      val newLrn: LocalReferenceNumber = LocalReferenceNumber("DCBA0987654321321").value
      val oldLrnData                   = emptyUserAnswers.copy(lrn = lrn, tasks = Map("task1" -> TaskStatus.Error))
      val newDataToSend                = oldLrnData.copy(lrn = newLrn, isSubmitted = Some(RejectedPendingChanges))
      val newDataWithResubmittedLRrn   = oldLrnData.copy(resubmittedLrn = Some(newLrn), isSubmitted = Some(RejectedPendingChanges))

      "must return true" - {
        "when answers in the cache can be found and data posts to cache" in {

          when(mockCacheConnector.get(eqTo(lrn))(any())) thenReturn Future.successful(Some(oldLrnData))
          when(mockCacheConnector.post(eqTo(newDataToSend))(any())) thenReturn Future.successful(true)
          when(mockCacheConnector.post(eqTo(newDataWithResubmittedLRrn))(any())) thenReturn Future.successful(true)

          duplicateService.copyUserAnswers(lrn, newLrn).futureValue mustBe true

          verify(mockCacheConnector).get(eqTo(lrn))(any())
          verify(mockCacheConnector).post(eqTo(newDataToSend))(any())
          verify(mockCacheConnector).post(eqTo(newDataWithResubmittedLRrn))(any())

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

        "when answers found in the cache, but first post succeed and second post fails" in {

          when(mockCacheConnector.get(eqTo(lrn))(any())) thenReturn Future.successful(Some(oldLrnData))
          when(mockCacheConnector.post(eqTo(newDataToSend))(any())) thenReturn Future.successful(true)
          when(mockCacheConnector.post(eqTo(newDataWithResubmittedLRrn))(any())) thenReturn Future.successful(false)

          duplicateService.copyUserAnswers(lrn, newLrn).futureValue mustBe false

          verify(mockCacheConnector).get(eqTo(lrn))(any())
          verify(mockCacheConnector).post(eqTo(newDataToSend))(any())
        }
      }

    }

    "alreadyExists" - {
      "must return correct boolean" - {

        "when local reference number" in {
          forAll(arbitrary[Boolean]) {
            isDuplicate =>
              when(mockCacheConnector.doesDraftOrSubmissionExistForLrn(eqTo(lrn))(any())).thenReturn(Future.successful(isDuplicate))

              duplicateService.alreadyExists(Some(lrn)).futureValue mustBe isDuplicate
          }
        }

        "when none" in {
          duplicateService.alreadyExists(None).futureValue mustBe false
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

        when(mockCacheConnector.doesSubmissionExistForLrn(eqTo(lrn))(any())).thenReturn(Future.successful(true))

        duplicateService.doesSubmissionExistForLrn(lrn).futureValue mustBe true

        verify(mockCacheConnector).doesSubmissionExistForLrn(eqTo(lrn))(any())
      }

      "must return false if LRN does not exist in API" in {
        when(mockCacheConnector.doesSubmissionExistForLrn(eqTo(lrn))(any())).thenReturn(Future.successful(false))

        duplicateService.doesSubmissionExistForLrn(lrn).futureValue mustBe false

        verify(mockCacheConnector).doesSubmissionExistForLrn(eqTo(lrn))(any())
      }
    }

    "updateResubmittedLrn" - {

      val newLrn: LocalReferenceNumber = LocalReferenceNumber("DCBA0987654321321").value
      val oldLrnData                   = emptyUserAnswers.copy(lrn = lrn, tasks = Map("task1" -> TaskStatus.Error))
      val newDataToSend                = oldLrnData.copy(lrn = newLrn, isSubmitted = Some(RejectedPendingChanges))
      val newDataWithResubmittedLRrn   = oldLrnData.copy(resubmittedLrn = Some(newLrn), isSubmitted = Some(RejectedPendingChanges))

      "must return true when updated lrn post to cache succeeds" in {

        when(mockCacheConnector.post(eqTo(newDataWithResubmittedLRrn))(any())) thenReturn Future.successful(true)

        duplicateService.updateResubmittedLrn(newLrn, newDataWithResubmittedLRrn).futureValue mustBe true

        verify(mockCacheConnector).post(eqTo(newDataWithResubmittedLRrn))(any())

      }

      "must return false when updated lrn post to cache fails" in {

        when(mockCacheConnector.post(eqTo(newDataWithResubmittedLRrn))(any())) thenReturn Future.successful(false)

        duplicateService.updateResubmittedLrn(newLrn, newDataWithResubmittedLRrn).futureValue mustBe false

        verify(mockCacheConnector).post(eqTo(newDataWithResubmittedLRrn))(any())

      }
    }

  }
}
