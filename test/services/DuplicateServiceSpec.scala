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

package services

import base.SpecBase
import connectors.CacheConnector
import generators.Generators
import models.UserAnswersResponse.Answers
import models.{DepartureMessage, DepartureMessages, LocalReferenceNumber, UserAnswersResponse}
import org.mockito.ArgumentMatchers.{any, eq as eqTo}
import org.mockito.Mockito.*
import org.scalatest.BeforeAndAfterEach

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

      "must return true" - {
        "when copy returns true" in {
          when(mockCacheConnector.copy(eqTo(lrn), eqTo(newLrn))(any())).thenReturn(Future.successful(true))

          duplicateService.copyUserAnswers(lrn, newLrn).futureValue mustEqual true

          verify(mockCacheConnector).copy(eqTo(lrn), eqTo(newLrn))(any())
        }
      }

      "must return false" - {
        "when copy returns false" in {
          when(mockCacheConnector.copy(eqTo(lrn), eqTo(newLrn))(any())).thenReturn(Future.successful(false))

          duplicateService.copyUserAnswers(lrn, newLrn).futureValue mustEqual false

          verify(mockCacheConnector).copy(eqTo(lrn), eqTo(newLrn))(any())
        }
      }
    }

    "doesDraftOrSubmissionExistForLrn" - {
      "must return true" - {
        "if draft exists" in {
          when(mockCacheConnector.get(any())(any())).thenReturn(Future.successful(Answers(emptyUserAnswers)))

          duplicateService.doesDraftOrSubmissionExistForLrn(lrn).futureValue mustEqual true

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
          when(mockCacheConnector.get(any())(any())).thenReturn(Future.successful(UserAnswersResponse.NoAnswers))
          when(mockCacheConnector.getMessages(any())(any())).thenReturn(Future.successful(messages))

          duplicateService.doesDraftOrSubmissionExistForLrn(lrn).futureValue mustEqual true

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
          when(mockCacheConnector.get(any())(any())).thenReturn(Future.successful(UserAnswersResponse.NoAnswers))
          when(mockCacheConnector.getMessages(any())(any())).thenReturn(Future.successful(messages))

          duplicateService.doesDraftOrSubmissionExistForLrn(lrn).futureValue mustEqual false

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

        duplicateService.doesIE028ExistForLrn(lrn).futureValue mustEqual true

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

        duplicateService.doesIE028ExistForLrn(lrn).futureValue mustEqual false

        verify(mockCacheConnector).getMessages(eqTo(lrn))(any())
      }
    }
  }
}
