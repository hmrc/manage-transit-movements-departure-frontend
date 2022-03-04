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
import models.CustomsOfficeList
import models.reference.{CountryCode, CustomsOffice}
import org.mockito.ArgumentMatchers.{any, eq => eqTo}
import org.mockito.Mockito.{reset, verify, when}
import org.scalatest.BeforeAndAfterEach

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class CustomsOfficesServiceSpec extends SpecBase with BeforeAndAfterEach {

  val mockRefDataConnector: ReferenceDataConnector = mock[ReferenceDataConnector]
  val service                                      = new CustomsOfficesService(mockRefDataConnector)

  val gbCustomsOffice1: CustomsOffice     = CustomsOffice("1", "BOSTON", CountryCode("GB"), None)
  val gbCustomsOffice2: CustomsOffice     = CustomsOffice("2", "Appledore", CountryCode("GB"), None)
  val xiCustomsOffice1: CustomsOffice     = CustomsOffice("3", "Belfast", CountryCode("XI"), None)
  val gbCustomsOffices: CustomsOfficeList = CustomsOfficeList(Seq(gbCustomsOffice1, gbCustomsOffice2))
  val xiCustomsOffices: CustomsOfficeList = CustomsOfficeList(Seq(xiCustomsOffice1))
  val customsOffices: CustomsOfficeList   = CustomsOfficeList(gbCustomsOffices.getAll ++ xiCustomsOffices.getAll)

  override def beforeEach(): Unit = {
    reset(mockRefDataConnector)
    super.beforeEach()
  }

  "CustomsOfficesService" - {

    "getCustomsOffices" - {
      "must return a list of sorted GB and NI customs offices" in {

        when(mockRefDataConnector.getCustomsOffices(any())(any(), any()))
          .thenReturn(Future.successful(gbCustomsOffices.customsOffices))

        service.getCustomsOffices().futureValue.getAll mustBe
          CustomsOfficeList(Seq(gbCustomsOffice2, gbCustomsOffice1)).getAll

        verify(mockRefDataConnector).getCustomsOffices(eqTo(Nil))(any(), any())
      }
    }

    "getCustomsOfficesOfDeparture" - {
      "must return a list of sorted GB and NI customs offices" in {

        when(mockRefDataConnector.getCustomsOfficesForCountry(eqTo(CountryCode("XI")), eqTo(Seq("DEP")))(any(), any()))
          .thenReturn(Future.successful(xiCustomsOffices))
        when(mockRefDataConnector.getCustomsOfficesForCountry(eqTo(CountryCode("GB")), eqTo(Seq("DEP")))(any(), any()))
          .thenReturn(Future.successful(gbCustomsOffices))

        service.getCustomsOfficesOfDeparture.futureValue.getAll mustBe
          CustomsOfficeList(Seq(gbCustomsOffice2, xiCustomsOffice1, gbCustomsOffice1)).getAll

        verify(mockRefDataConnector).getCustomsOfficesForCountry(eqTo(CountryCode("XI")), eqTo(Seq("DEP")))(any(), any())
        verify(mockRefDataConnector).getCustomsOfficesForCountry(eqTo(CountryCode("GB")), eqTo(Seq("DEP")))(any(), any())
      }
    }

    "getCustomsOfficesForCountry" - {
      "must return a list of sorted customs offices for a given country" in {

        when(mockRefDataConnector.getCustomsOfficesForCountry(eqTo(CountryCode("GB")), eqTo(Nil))(any(), any()))
          .thenReturn(Future.successful(gbCustomsOffices))

        service.getCustomsOfficesForCountry(CountryCode("GB")).futureValue.getAll mustBe
          CustomsOfficeList(Seq(gbCustomsOffice2, gbCustomsOffice1)).getAll

        verify(mockRefDataConnector).getCustomsOfficesForCountry(eqTo(CountryCode("GB")), eqTo(Nil))(any(), any())
      }
    }

  }
}
