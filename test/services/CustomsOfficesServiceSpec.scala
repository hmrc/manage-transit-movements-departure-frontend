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

import base.{AppWithDefaultMockFixtures, SpecBase}
import connectors.ReferenceDataConnector
import generators.Generators
import models.reference.{CountryCode, CustomsOffice}
import models.{CustomsOfficeList, Index}
import org.mockito.ArgumentMatchers.{any, eq => eqTo}
import org.mockito.Mockito.{reset, verify, when}
import org.scalacheck.Arbitrary.arbitrary
import pages.routeDetails.exit.index.OfficeOfExitPage
import pages.routeDetails.routing.OfficeOfDestinationPage
import pages.routeDetails.transit.index.OfficeOfTransitPage
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class CustomsOfficesServiceSpec extends SpecBase with AppWithDefaultMockFixtures with Generators {

  val mockRefDataConnector: ReferenceDataConnector             = mock[ReferenceDataConnector]
  val service                                                  = new CustomsOfficesService(mockRefDataConnector)
  private val mockCustomsOfficesService: CustomsOfficesService = mock[CustomsOfficesService]

  val gbCustomsOffice1: CustomsOffice     = CustomsOffice("GB1", "BOSTON", None)
  val gbCustomsOffice2: CustomsOffice     = CustomsOffice("GB2", "Appledore", None)
  val xiCustomsOffice1: CustomsOffice     = CustomsOffice("XI1", "Belfast", None)
  val gbCustomsOffices: CustomsOfficeList = CustomsOfficeList(Seq(gbCustomsOffice1, gbCustomsOffice2))
  val xiCustomsOffices: CustomsOfficeList = CustomsOfficeList(Seq(xiCustomsOffice1))
  val customsOffices: CustomsOfficeList   = CustomsOfficeList(gbCustomsOffices.getAll ++ xiCustomsOffices.getAll)

  private val exitOffice1       = arbitrary[CustomsOffice].sample.value
  private val exitOffice2       = arbitrary[CustomsOffice].sample.value
  private val transitOffice1    = arbitrary[CustomsOffice].sample.value
  private val transitOffice2    = arbitrary[CustomsOffice].sample.value
  private val destinationOffice = arbitrary[CustomsOffice].sample.value

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[CustomsOfficesService]).toInstance(mockCustomsOfficesService))

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

        service.getCustomsOfficesOfDeparture.futureValue.getAll mustBe
          CustomsOfficeList(Seq(gbCustomsOffice2, xiCustomsOffice1, gbCustomsOffice1)).getAll

        verify(mockRefDataConnector).getCustomsOfficesOfDepartureForCountry(eqTo("XI"))(any(), any())
        verify(mockRefDataConnector).getCustomsOfficesOfDepartureForCountry(eqTo("GB"))(any(), any())
      }
    }

    "getCustomsOfficesOfTransitForCountry" - {
      "must return a list of sorted customs offices of transit for a given country" in {

        when(mockRefDataConnector.getCustomsOfficesOfTransitForCountry(eqTo(CountryCode("GB")))(any(), any()))
          .thenReturn(Future.successful(gbCustomsOffices))

        service.getCustomsOfficesOfTransitForCountry(CountryCode("GB")).futureValue.getAll mustBe
          CustomsOfficeList(Seq(gbCustomsOffice2, gbCustomsOffice1)).getAll

        verify(mockRefDataConnector).getCustomsOfficesOfTransitForCountry(eqTo(CountryCode("GB")))(any(), any())
      }
    }

    "getCustomsOfficesOfDestinationForCountry" - {
      "must return a list of sorted customs offices of destination for a given country" in {

        when(mockRefDataConnector.getCustomsOfficesOfDestinationForCountry(eqTo(CountryCode("GB")))(any(), any()))
          .thenReturn(Future.successful(gbCustomsOffices))

        service.getCustomsOfficesOfDestinationForCountry(CountryCode("GB")).futureValue.getAll mustBe
          CustomsOfficeList(Seq(gbCustomsOffice2, gbCustomsOffice1)).getAll

        verify(mockRefDataConnector).getCustomsOfficesOfDestinationForCountry(eqTo(CountryCode("GB")))(any(), any())
      }
    }

    "getCustomsOffices" - {
      "must return a list of sorted customs offices of destination" in {

        when(mockCustomsOfficesService.getCustomsOffices(any()))
          .thenReturn(CustomsOfficeList(List(destinationOffice)))

        val updatedAnswers = emptyUserAnswers
          .setValue(OfficeOfDestinationPage, destinationOffice)

        setExistingUserAnswers(updatedAnswers)

        mockCustomsOfficesService.getCustomsOffices(any()) mustBe CustomsOfficeList(List(destinationOffice))

      }

      "must return a list of sorted customs offices of transit" in {

        when(mockCustomsOfficesService.getCustomsOffices(any()))
          .thenReturn(CustomsOfficeList(List(transitOffice1, transitOffice2)))

        val updatedAnswers = emptyUserAnswers
          .setValue(OfficeOfTransitPage(Index(0)), transitOffice1)
          .setValue(OfficeOfTransitPage(Index(1)), transitOffice2)

        setExistingUserAnswers(updatedAnswers)

        mockCustomsOfficesService.getCustomsOffices(any()) mustBe CustomsOfficeList(List(transitOffice1, transitOffice2))

      }

      "must return a list of sorted customs offices of exit" in {

        when(mockCustomsOfficesService.getCustomsOffices(any()))
          .thenReturn(CustomsOfficeList(List(exitOffice1, exitOffice2)))

        val updatedAnswers = emptyUserAnswers
          .setValue(OfficeOfExitPage(Index(0)), exitOffice1)
          .setValue(OfficeOfExitPage(Index(1)), exitOffice2)

        setExistingUserAnswers(updatedAnswers)

        mockCustomsOfficesService.getCustomsOffices(any()) mustBe CustomsOfficeList(List(exitOffice1, exitOffice2))

      }

      "must return a list of sorted customs offices of exit, transit and destination" in {

        when(mockCustomsOfficesService.getCustomsOffices(any()))
          .thenReturn(CustomsOfficeList(List(destinationOffice, exitOffice1, transitOffice1)))

        val updatedAnswers = emptyUserAnswers
          .setValue(OfficeOfDestinationPage, destinationOffice)
          .setValue(OfficeOfExitPage(Index(0)), exitOffice1)
          .setValue(OfficeOfTransitPage(Index(0)), transitOffice1)

        setExistingUserAnswers(updatedAnswers)

        mockCustomsOfficesService.getCustomsOffices(any()) mustBe CustomsOfficeList(List(destinationOffice, exitOffice1, transitOffice1))

      }

    }

  }
}
