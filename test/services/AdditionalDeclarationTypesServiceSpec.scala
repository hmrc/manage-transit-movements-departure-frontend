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
import connectors.ReferenceDataConnector
import models.reference.AdditionalDeclarationType
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, verify, when}
import org.scalatest.BeforeAndAfterEach

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class AdditionalDeclarationTypesServiceSpec extends SpecBase with BeforeAndAfterEach {

  private val mockRefDataConnector: ReferenceDataConnector = mock[ReferenceDataConnector]

  private val service = new AdditionalDeclarationTypesService(mockRefDataConnector)

  override def beforeEach(): Unit = {
    reset(mockRefDataConnector)
    super.beforeEach()
  }

  private val adt1 = AdditionalDeclarationType(
    "A",
    "for a standard customs declaration (under Article 162 of the Code)"
  )

  private val adt2 = AdditionalDeclarationType(
    "D",
    "For lodging a standard customs declaration (such as referred to under code A) in accordance with Article 171 of the Code."
  )

  private val adts = NonEmptySet.of(adt2, adt1)

  "AdditionalDeclarationTypesService" - {

    "getAdditionalDeclarationTypes" - {
      "must return a list of sorted additional declaration types" in {
        when(mockRefDataConnector.getAdditionalDeclarationTypes()(any(), any()))
          .thenReturn(Future.successful(Right(adts)))

        service.getAdditionalDeclarationTypes().futureValue mustEqual
          Seq(adt1, adt2)

        verify(mockRefDataConnector).getAdditionalDeclarationTypes()(any(), any())
      }
    }
  }
}
