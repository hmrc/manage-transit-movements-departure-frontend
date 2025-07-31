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
import models.reference.SecurityType
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, verify, when}
import org.scalatest.BeforeAndAfterEach

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class SecurityTypesServiceSpec extends SpecBase with BeforeAndAfterEach {

  private val mockRefDataConnector: ReferenceDataConnector = mock[ReferenceDataConnector]

  private val mockFrontendAppConfig: FrontendAppConfig = mock[FrontendAppConfig]

  private val securityType3 = SecurityType("3", "ENS &amp; EXS")
  private val securityType2 = SecurityType("2", "EXS")
  private val securityType1 = SecurityType("1", "ENS")
  private val securityType0 = SecurityType("0", "Not used for safety and security purposes")

  private val securityTypes = NonEmptySet.of(securityType3, securityType2, securityType1, securityType0)

  override def beforeEach(): Unit = {
    reset(mockRefDataConnector)
    super.beforeEach()
  }

  "SecurityTypesService" - {

    "getSecurityTypes" - {
      "must return a list of sorted security types" - {
        "when other countries aren't on phase 6 rules" in {
          val service = new SecurityTypesService(mockRefDataConnector, mockFrontendAppConfig)

          when(mockRefDataConnector.getSecurityTypes()(any(), any()))
            .thenReturn(Future.successful(Right(securityTypes)))

          when(mockFrontendAppConfig.phase6RulesEnabled).thenReturn(false)

          service.getSecurityTypes().futureValue mustEqual
            Seq(securityType0, securityType1, securityType2, securityType3)

          verify(mockRefDataConnector).getSecurityTypes()(any(), any())
        }

        "when other countries are on phase 6 rules" in {
          val service = new SecurityTypesService(mockRefDataConnector, mockFrontendAppConfig)

          when(mockRefDataConnector.getSecurityTypes()(any(), any()))
            .thenReturn(Future.successful(Right(securityTypes)))

          when(mockFrontendAppConfig.phase6RulesEnabled).thenReturn(true)

          service.getSecurityTypes().futureValue mustEqual
            Seq(securityType0, securityType2)

          verify(mockRefDataConnector).getSecurityTypes()(any(), any())
        }
      }
    }
  }
}
