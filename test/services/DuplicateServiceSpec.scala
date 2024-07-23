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
import models.SubmissionState.RejectedPendingChanges
import models.{DepartureMessage, DepartureMessages, LocalReferenceNumber}
import org.mockito.ArgumentMatchers.{any, eq => eqTo}
import org.mockito.Mockito._
import org.scalatest.BeforeAndAfterEach
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

    "doesDraftOrSubmissionExistForLrn" - {
      "must return true" - {
        "if draft exists" in {
          when(mockCacheConnector.get(any())(any())).thenReturn(Future.successful(Some(emptyUserAnswers)))

          duplicateService.doesDraftOrSubmissionExistForLrn(lrn).futureValue mustBe true

          verify(mockCacheConnector).get(eqTo(lrn))(any())
          verify(mockCacheConnector, never()).getMessages(any())(any())
        }

        "if draft doesn't exist but IE028 does" in {
          val messages = DepartureMessages(
            Seq(
              DepartureMessage("IE015"),
              DepartureMessage("IE928"),
              DepartureMessage("IE028")
            )
          )
          when(mockCacheConnector.get(any())(any())).thenReturn(Future.successful(None))
          when(mockCacheConnector.getMessages(any())(any())).thenReturn(Future.successful(messages))

          duplicateService.doesDraftOrSubmissionExistForLrn(lrn).futureValue mustBe true

          verify(mockCacheConnector).get(eqTo(lrn))(any())
          verify(mockCacheConnector).getMessages(eqTo(lrn))(any())
        }
      }

      "must return false" - {
        "if draft doesn't exist and IE028 doesn't exist" in {
          val messages = DepartureMessages(
            Seq(
              DepartureMessage("IE015"),
              DepartureMessage("IE928")
            )
          )
          when(mockCacheConnector.get(any())(any())).thenReturn(Future.successful(None))
          when(mockCacheConnector.getMessages(any())(any())).thenReturn(Future.successful(messages))

          duplicateService.doesDraftOrSubmissionExistForLrn(lrn).futureValue mustBe false

          verify(mockCacheConnector).get(eqTo(lrn))(any())
          verify(mockCacheConnector).getMessages(eqTo(lrn))(any())
        }
      }
    }

    "doesIE028ExistForLrn" - {
      "must return true if IE028 exists for LRN" in {

        val messages = DepartureMessages(
          Seq(
            DepartureMessage("IE015"),
            DepartureMessage("IE928"),
            DepartureMessage("IE028")
          )
        )

        when(mockCacheConnector.getMessages(eqTo(lrn))(any()))
          .thenReturn(Future.successful(messages))

        duplicateService.doesIE028ExistForLrn(lrn).futureValue mustBe true

        verify(mockCacheConnector).getMessages(eqTo(lrn))(any())
      }

      "must return false if IE028 does not exist for LRN" in {

        val messages = DepartureMessages(
          Seq(
            DepartureMessage("IE015"),
            DepartureMessage("IE928")
          )
        )

        when(mockCacheConnector.getMessages(eqTo(lrn))(any()))
          .thenReturn(Future.successful(messages))

        duplicateService.doesIE028ExistForLrn(lrn).futureValue mustBe false

        verify(mockCacheConnector).getMessages(eqTo(lrn))(any())
      }
    }
  }
}
