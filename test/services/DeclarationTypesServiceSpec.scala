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
import cats.data.NonEmptySet
import connectors.ReferenceDataConnector
import generators.Generators
import models.ProcedureType
import models.reference.{CustomsOffice, DeclarationType}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, verify, when}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class DeclarationTypesServiceSpec extends SpecBase with BeforeAndAfterEach with ScalaCheckPropertyChecks with Generators {

  private val mockRefDataConnector: ReferenceDataConnector = mock[ReferenceDataConnector]
  private val service                                      = new DeclarationTypesService(mockRefDataConnector)

  private val declarationType1 = DeclarationType("TIR", "TIR description")
  private val declarationType2 = DeclarationType("T2SM", "T2SM description")
  private val declarationType3 = DeclarationType("T2F", "T2F description")
  private val declarationType4 = DeclarationType("T2", "T2 description")
  private val declarationType5 = DeclarationType("T1", "T1 description")
  private val declarationType6 = DeclarationType("T", "T description")

  private val declarationTypes = NonEmptySet.of(
    declarationType1,
    declarationType2,
    declarationType3,
    declarationType4,
    declarationType5,
    declarationType6
  )

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockRefDataConnector)

    when(mockRefDataConnector.getDeclarationTypes()(any(), any()))
      .thenReturn(Future.successful(declarationTypes))
  }

  "DeclarationTypesService" - {

    "getDeclarationTypes" - {
      "must return a list of sorted declaration types" - {
        "when XI office and departure and normal procedure type" in {
          forAll(arbitrary[CustomsOffice](arbitraryXiCustomsOffice)) {
            officeOfDeparture =>
              beforeEach()

              service.getDeclarationTypes(officeOfDeparture, ProcedureType.Normal).futureValue mustBe
                Seq(declarationType6, declarationType5, declarationType4, declarationType3, declarationType1)

              verify(mockRefDataConnector).getDeclarationTypes()(any(), any())
          }
        }

        "when GB office of departure" in {
          forAll(arbitrary[CustomsOffice](arbitraryGbCustomsOffice), arbitrary[ProcedureType]) {
            (officeOfDeparture, procedureType) =>
              beforeEach()

              service.getDeclarationTypes(officeOfDeparture, procedureType).futureValue mustBe
                Seq(declarationType6, declarationType5, declarationType4, declarationType3)

              verify(mockRefDataConnector).getDeclarationTypes()(any(), any())
          }
        }

        "when XI office of departure simplified procedure type" in {
          forAll(arbitrary[CustomsOffice](arbitraryXiCustomsOffice)) {
            officeOfDeparture =>
              beforeEach()

              service.getDeclarationTypes(officeOfDeparture, ProcedureType.Simplified).futureValue mustBe
                Seq(declarationType6, declarationType5, declarationType4, declarationType3)

              verify(mockRefDataConnector).getDeclarationTypes()(any(), any())
          }
        }
      }
    }
  }
}
