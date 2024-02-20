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
import models.reference.Country
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, verify, when}
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class CountriesServiceSpec extends SpecBase with BeforeAndAfterEach with Generators with ScalaCheckPropertyChecks {

  private val mockRefDataConnector: ReferenceDataConnector = mock[ReferenceDataConnector]
  private val service                                      = new CountriesService(mockRefDataConnector)

  private val country1: Country = Country("GB")
  private val country2: Country = Country("FR")
  private val country3: Country = Country("ES")
  private val countries         = NonEmptySet.of(country1, country2, country3)

  override def beforeEach(): Unit = {
    reset(mockRefDataConnector)
    super.beforeEach()
  }

  "CountriesService" - {

    "getCommunityCountries" - {
      "must return a list of sorted EU transit countries" in {

        when(mockRefDataConnector.getCountries())
          .thenReturn(Future.successful(countries))

        service.getCommunityCountries().futureValue mustBe
          Seq(country3, country2, country1)

        verify(mockRefDataConnector).getCountries()(any(), any())
      }
    }

    "getCustomsSecurityAgreementAreaCountries" - {
      "must return a list of sorted customs security agreement area countries" in {

        when(mockRefDataConnector.getCustomsSecurityAgreementAreaCountries()(any(), any()))
          .thenReturn(Future.successful(countries))

        service.getCustomsSecurityAgreementAreaCountries().futureValue mustBe
          Seq(country3, country2, country1)

        verify(mockRefDataConnector).getCustomsSecurityAgreementAreaCountries()(any(), any())
      }
    }

    "getCountryCodesCTC" - {
      "must return a list of sorted customs security agreement area countries" in {

        when(mockRefDataConnector.getCountryCodesCTC()(any(), any()))
          .thenReturn(Future.successful(countries))

        service.getCountryCodesCTC().futureValue mustBe
          Seq(country3, country2, country1)

        verify(mockRefDataConnector).getCountryCodesCTC()(any(), any())
      }
    }

  }
}
