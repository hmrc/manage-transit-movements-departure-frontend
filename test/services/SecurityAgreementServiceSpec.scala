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
import generators.Generators
import models.CountryList
import models.reference.{Country, CountryCode}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, when}
import org.scalatest.BeforeAndAfterEach

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class SecurityAgreementServiceSpec extends SpecBase with BeforeAndAfterEach with Generators {

  private val mockCountriesService = mock[CountriesService]
  private val service              = new SecurityAgreementService(mockCountriesService)

  private val country1: Country = Country(CountryCode("GB"), "United Kingdom")
  private val country2: Country = Country(CountryCode("FR"), "France")
  private val country3: Country = Country(CountryCode("ES"), "Spain")

  private val nonSecurityCountry1: Country = Country(CountryCode("IT"), "Italy")
  private val nonSecurityCountry2: Country = Country(CountryCode("NZ"), "New Zealand")

  private val securityAgreementCountries = Seq(country1, country2, country3)

  override def beforeEach(): Unit = {
    reset(mockCountriesService)
    super.beforeEach()
  }

  "SecurityAgreementService" - {

    "areAllCountriesInSecurityAgreement" - {
      "must return true all countries are in the security agreement area" in {

        when(mockCountriesService.getCustomsSecurityAgreementAreaCountries()(any()))
          .thenReturn(Future.successful(CountryList(securityAgreementCountries)))

        service.areAllCountriesInSecurityAgreement(securityAgreementCountries).futureValue mustBe true
      }

      "must return false if at least one country is not in the security agreement area" in {

        when(mockCountriesService.getCustomsSecurityAgreementAreaCountries()(any()))
          .thenReturn(Future.successful(CountryList(securityAgreementCountries)))

        val countriesWithNonSecurityArea = securityAgreementCountries ++ Seq(nonSecurityCountry1)

        service.areAllCountriesInSecurityAgreement(countriesWithNonSecurityArea).futureValue mustBe false
      }

      "must return false if more than one country is not in the security agreement area" in {

        when(mockCountriesService.getCustomsSecurityAgreementAreaCountries()(any()))
          .thenReturn(Future.successful(CountryList(securityAgreementCountries)))

        val countriesWithNonSecurityArea = securityAgreementCountries ++ Seq(nonSecurityCountry1, nonSecurityCountry2)

        service.areAllCountriesInSecurityAgreement(countriesWithNonSecurityArea).futureValue mustBe false
      }
    }
  }
}
