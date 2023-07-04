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

      "must return true" - {
        "when answers in the cache can be found and data posts to cache" in {

          when(mockCacheConnector.get(eqTo(lrn))(any())) thenReturn Future.successful(Some(oldLrnData))
          when(mockCacheConnector.post(eqTo(newDataToSend))(any())) thenReturn Future.successful(true)

          duplicateService.copyUserAnswers(lrn, newLrn).futureValue mustBe true

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

    "populateForm" - {
      "must return correct form" - {

        "when local reference number" in {
          forAll(arbitrary[Boolean]) {
            isDuplicate =>
              when(mockCacheConnector.isDuplicateLRN(eqTo(lrn))(any())).thenReturn(Future.successful(isDuplicate))

              duplicateService.populateForm(Some(lrn)).futureValue mustBe formProvider(alreadyExists = isDuplicate)
          }
        }

        "when none" in {
          duplicateService.populateForm(None).futureValue mustBe formProvider(alreadyExists = false)
        }

      }

    }

    "isDuplicateLRN" - {
      "must return true if LRN is a duplicate" in {

        when(mockCacheConnector.isDuplicateLRN(eqTo(lrn))(any())).thenReturn(Future.successful(true))

        duplicateService.isDuplicateLRN(lrn).futureValue mustBe true

        verify(mockCacheConnector).isDuplicateLRN(eqTo(lrn))(any())
      }

      "must return false if LRN is not a duplicate" in {
        when(mockCacheConnector.isDuplicateLRN(eqTo(lrn))(any())).thenReturn(Future.successful(false))

        duplicateService.isDuplicateLRN(lrn).futureValue mustBe false

        verify(mockCacheConnector).isDuplicateLRN(eqTo(lrn))(any())
      }
    }

    "isDuplicateLRN" - {
      "must return true if LRN exists in API" in {

        when(mockCacheConnector.apiLRNCheck(eqTo(lrn))(any())).thenReturn(Future.successful(true))

        duplicateService.apiLRNCheck(lrn).futureValue mustBe true

        verify(mockCacheConnector).apiLRNCheck(eqTo(lrn))(any())
      }

      "must return false if LRN does not exist in API" in {
        when(mockCacheConnector.apiLRNCheck(eqTo(lrn))(any())).thenReturn(Future.successful(false))

        duplicateService.apiLRNCheck(lrn).futureValue mustBe false

        verify(mockCacheConnector).apiLRNCheck(eqTo(lrn))(any())
      }
    }

  }
}
