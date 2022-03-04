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

package services

import base.SpecBase
import connectors.ReferenceDataConnector
import models.TransportModeList
import models.reference.TransportMode
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, verify, when}
import org.scalatest.BeforeAndAfterEach

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class TransportModesServiceSpec extends SpecBase with BeforeAndAfterEach {

  private val mockRefDataConnector: ReferenceDataConnector = mock[ReferenceDataConnector]
  private val service                                      = new TransportModesService(mockRefDataConnector)

  private val transportMode1 = TransportMode("1", "ROAD TRANSPORT")
  private val transportMode2 = TransportMode("2", "Air transport")
  private val transportMode3 = TransportMode("3", "Rail transport")

  override def beforeEach(): Unit = {
    reset(mockRefDataConnector)
    super.beforeEach()
  }

  "TransportModesService" - {

    "getTransportModes" - {
      "must return a list of sorted transport modes" in {

        when(mockRefDataConnector.getTransportModes()(any(), any()))
          .thenReturn(Future.successful(Seq(transportMode1, transportMode2, transportMode3)))

        service.getTransportModes().futureValue mustBe
          TransportModeList(Seq(transportMode2, transportMode3, transportMode1))

        verify(mockRefDataConnector).getTransportModes()(any(), any())
      }
    }

  }
}
