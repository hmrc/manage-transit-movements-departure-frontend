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
import connectors.ReferenceDataConnector
import models.SelectableList
import models.reference.CustomsOffice
import org.mockito.ArgumentMatchers.{any, eq => eqTo}
import org.mockito.Mockito.{reset, verify, when}
import org.scalatest.BeforeAndAfterEach

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class CustomsOfficesServiceSpec extends SpecBase with BeforeAndAfterEach {

  val mockRefDataConnector: ReferenceDataConnector = mock[ReferenceDataConnector]
  val service                                      = new CustomsOfficesService(mockRefDataConnector)

  val gbCustomsOffice1: CustomsOffice      = CustomsOffice("GB1", "BOSTON", None)
  val gbCustomsOffice2: CustomsOffice      = CustomsOffice("GB2", "Appledore", None)
  val xiCustomsOffice1: CustomsOffice      = CustomsOffice("XI1", "Belfast", None)
  val gbCustomsOffices: Seq[CustomsOffice] = Seq(gbCustomsOffice1, gbCustomsOffice2)
  val xiCustomsOffices: Seq[CustomsOffice] = Seq(xiCustomsOffice1)

  override def beforeEach(): Unit = {
    reset(mockRefDataConnector)
    super.beforeEach()
  }

  "CustomsOfficesService" - {

    "getCustomsOfficesOfDeparture" - {
      "must return a list of sorted GB and NI customs offices" in {

        when(mockRefDataConnector.getCustomsOfficesOfDepartureForCountry(eqTo("XI"))(any(), any()))
          .thenReturn(Future.successful(xiCustomsOffices))

        when(mockRefDataConnector.getCustomsOfficesOfDepartureForCountry(eqTo("GB"))(any(), any()))
          .thenReturn(Future.successful(gbCustomsOffices))

        service.getCustomsOfficesOfDeparture.futureValue mustBe
          SelectableList(Seq(gbCustomsOffice2, xiCustomsOffice1, gbCustomsOffice1))

        verify(mockRefDataConnector).getCustomsOfficesOfDepartureForCountry(eqTo("XI"))(any(), any())
        verify(mockRefDataConnector).getCustomsOfficesOfDepartureForCountry(eqTo("GB"))(any(), any())
      }
    }
  }
}
