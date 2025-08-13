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
import cats.data.NonEmptySet
import config.FrontendAppConfig
import connectors.ReferenceDataConnector
import models.SelectableList
import models.reference.CustomsOffice
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{verify, when}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class CustomsOfficesServiceSpec extends SpecBase {

  private val mockRefDataConnector: ReferenceDataConnector = mock[ReferenceDataConnector]

  private val mockFrontendAppConfig: FrontendAppConfig = mock[FrontendAppConfig]

  private val service: CustomsOfficesService = new CustomsOfficesService(mockRefDataConnector, mockFrontendAppConfig)

  private val gbCustomsOffice1: CustomsOffice = CustomsOffice("GB1", "BOSTON", None, "GB")
  private val gbCustomsOffice2: CustomsOffice = CustomsOffice("GB2", "Appledore", None, "GB")
  private val xiCustomsOffice1: CustomsOffice = CustomsOffice("XI1", "Belfast", None, "XI")

  private val customsOffices: NonEmptySet[CustomsOffice] = NonEmptySet.of(gbCustomsOffice1, gbCustomsOffice2, xiCustomsOffice1)

  private val countriesOfDeparture: Seq[String] = Seq("GB", "XI")

  "CustomsOfficesService" - {

    "getCustomsOfficesOfDeparture" - {
      "must return a list of sorted GB and NI customs offices" in {

        when(mockRefDataConnector.getCustomsOfficesOfDepartureForCountry(any())(any(), any()))
          .thenReturn(Future.successful(Right(customsOffices)))

        when(mockFrontendAppConfig.countriesOfDeparture).thenReturn(countriesOfDeparture)

        service.getCustomsOfficesOfDeparture.futureValue mustEqual
          SelectableList(Seq(gbCustomsOffice2, xiCustomsOffice1, gbCustomsOffice1))

        val varargsCaptor: ArgumentCaptor[Seq[String]] = ArgumentCaptor.forClass(classOf[Seq[String]])
        verify(mockRefDataConnector).getCustomsOfficesOfDepartureForCountry(varargsCaptor.capture()*)(any(), any())
        varargsCaptor.getValue mustEqual countriesOfDeparture
      }
    }
  }
}
