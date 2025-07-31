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
import connectors.ReferenceDataConnector
import connectors.ReferenceDataConnector.NoReferenceDataFoundException
import generators.Generators
import models.reference.{Country, CustomsOffice}
import org.mockito.ArgumentMatchers.{any, eq => eqTo}
import org.mockito.Mockito.{reset, verify, when}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class CountriesServiceSpec extends SpecBase with BeforeAndAfterEach with Generators with ScalaCheckPropertyChecks {

  private val mockRefDataConnector: ReferenceDataConnector = mock[ReferenceDataConnector]

  private val service = new CountriesService(mockRefDataConnector)

  override def beforeEach(): Unit = {
    reset(mockRefDataConnector)
    super.beforeEach()
  }

  "CountriesService" - {

    "isInCL112" - {
      "must return true" - {
        "when connector call returns the country" in {
          forAll(arbitrary[CustomsOffice], arbitrary[Country]) {
            (customsOffice, country) =>
              beforeEach()

              when(mockRefDataConnector.getCountryCodesCTCCountry(any())(any(), any()))
                .thenReturn(Future.successful(Right(country)))

              val result = service.isInCL112(customsOffice).futureValue

              result mustEqual true

              verify(mockRefDataConnector).getCountryCodesCTCCountry(eqTo(customsOffice.countryId))(any(), any())
          }
        }
      }

      "must return false" - {
        "when connector call returns NoReferenceDataFoundException" in {
          forAll(arbitrary[CustomsOffice]) {
            customsOffice =>
              when(mockRefDataConnector.getCountryCodesCTCCountry(any())(any(), any()))
                .thenReturn(Future.successful(Left(new NoReferenceDataFoundException(""))))

              val result = service.isInCL112(customsOffice).futureValue

              result mustEqual false
          }
        }
      }

      "must fail" - {
        "when connector call otherwise fails" in {
          forAll(arbitrary[CustomsOffice]) {
            customsOffice =>
              when(mockRefDataConnector.getCountryCodesCTCCountry(any())(any(), any()))
                .thenReturn(Future.failed(new Throwable("")))

              val result = service.isInCL112(customsOffice)

              result.failed.futureValue mustBe a[Throwable]
          }
        }
      }
    }

    "isInCL147" - {
      "must return true" - {
        "when connector call returns the country" in {
          forAll(arbitrary[CustomsOffice], arbitrary[Country]) {
            (customsOffice, country) =>
              beforeEach()

              when(mockRefDataConnector.getCountryCustomsSecurityAgreementAreaCountry(any())(any(), any()))
                .thenReturn(Future.successful(Right(country)))

              val result = service.isInCL147(customsOffice).futureValue

              result mustEqual true

              verify(mockRefDataConnector).getCountryCustomsSecurityAgreementAreaCountry(eqTo(customsOffice.countryId))(any(), any())
          }
        }
      }

      "must return false" - {
        "when connector call returns NoReferenceDataFoundException" in {
          forAll(arbitrary[CustomsOffice]) {
            customsOffice =>
              when(mockRefDataConnector.getCountryCustomsSecurityAgreementAreaCountry(any())(any(), any()))
                .thenReturn(Future.successful(Left(new NoReferenceDataFoundException(""))))

              val result = service.isInCL147(customsOffice).futureValue

              result mustEqual false
          }
        }
      }

      "must fail" - {
        "when connector call otherwise fails" in {
          forAll(arbitrary[CustomsOffice]) {
            customsOffice =>
              when(mockRefDataConnector.getCountryCustomsSecurityAgreementAreaCountry(any())(any(), any()))
                .thenReturn(Future.failed(new Throwable("")))

              val result = service.isInCL147(customsOffice)

              result.failed.futureValue mustBe a[Throwable]
          }
        }
      }
    }

    "isInCL010" - {
      "must return true" - {
        "when connector call returns the country" in {
          forAll(arbitrary[CustomsOffice], arbitrary[Country]) {
            (customsOffice, country) =>
              beforeEach()

              when(mockRefDataConnector.getCountryCodeCommunityCountry(any())(any(), any()))
                .thenReturn(Future.successful(Right(country)))

              val result = service.isInCL010(customsOffice).futureValue

              result mustEqual true

              verify(mockRefDataConnector).getCountryCodeCommunityCountry(eqTo(customsOffice.countryId))(any(), any())
          }
        }
      }

      "must return false" - {
        "when connector call returns NoReferenceDataFoundException" in {
          forAll(arbitrary[CustomsOffice]) {
            customsOffice =>
              when(mockRefDataConnector.getCountryCodeCommunityCountry(any())(any(), any()))
                .thenReturn(Future.successful(Left(new NoReferenceDataFoundException(""))))

              val result = service.isInCL010(customsOffice).futureValue

              result mustEqual false
          }
        }
      }

      "must fail" - {
        "when connector call otherwise fails" in {
          forAll(arbitrary[CustomsOffice]) {
            customsOffice =>
              when(mockRefDataConnector.getCountryCodeCommunityCountry(any())(any(), any()))
                .thenReturn(Future.failed(new Throwable("")))

              val result = service.isInCL010(customsOffice)

              result.failed.futureValue mustBe a[Throwable]
          }
        }
      }
    }
  }
}
