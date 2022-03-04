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
import models.DangerousGoodsCodeList
import models.reference.DangerousGoodsCode
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, verify, when}
import org.scalatest.BeforeAndAfterEach

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class DangerousGoodsCodesServiceSpec extends SpecBase with BeforeAndAfterEach {

  private val mockRefDataConnector: ReferenceDataConnector = mock[ReferenceDataConnector]
  private val service                                      = new DangerousGoodsCodesService(mockRefDataConnector)

  private val dangerousGoodsCode1 = DangerousGoodsCode("1", "CARTRIDGES FOR WEAPONS with bursting charge")
  private val dangerousGoodsCode2 = DangerousGoodsCode("2", "AMMONIUM PICRATE dry or wetted with less than 10% water, by mass")

  override def beforeEach(): Unit = {
    reset(mockRefDataConnector)
    super.beforeEach()
  }

  "DangerousGoodsCodesService" - {

    "getDangerousGoodsCodes" - {
      "must return a list of sorted dangerous goods codes" in {

        when(mockRefDataConnector.getDangerousGoodsCodes()(any(), any()))
          .thenReturn(Future.successful(Seq(dangerousGoodsCode1, dangerousGoodsCode2)))

        service.getDangerousGoodsCodes().futureValue mustBe
          DangerousGoodsCodeList(Seq(dangerousGoodsCode2, dangerousGoodsCode1))

        verify(mockRefDataConnector).getDangerousGoodsCodes()(any(), any())
      }
    }

  }
}
