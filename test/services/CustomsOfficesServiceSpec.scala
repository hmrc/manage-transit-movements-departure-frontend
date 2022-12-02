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
import models.CustomsOfficeList
import models.reference.{CountryCode, CustomsOffice}
import org.mockito.ArgumentMatchers.{any, eq => eqTo}
import org.mockito.Mockito.{reset, verify, when}
import org.scalacheck.Arbitrary.arbitrary
import pages.routeDetails.routing.OfficeOfDestinationPage
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.transport.transportMeans.active.CustomsOfficeActiveBorderView

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class CustomsOfficesServiceSpec extends SpecBase with AppWithDefaultMockFixtures with Generators {

  val mockRefDataConnector: ReferenceDataConnector = mock[ReferenceDataConnector]
  val service                                      = new CustomsOfficesService(mockRefDataConnector)

  val gbCustomsOffice1: CustomsOffice     = CustomsOffice("GB1", "BOSTON", None)
  val gbCustomsOffice2: CustomsOffice     = CustomsOffice("GB2", "Appledore", None)
  val xiCustomsOffice1: CustomsOffice     = CustomsOffice("XI1", "Belfast", None)
  val gbCustomsOffices: CustomsOfficeList = CustomsOfficeList(Seq(gbCustomsOffice1, gbCustomsOffice2))
  val xiCustomsOffices: CustomsOfficeList = CustomsOfficeList(Seq(xiCustomsOffice1))
  val customsOffices: CustomsOfficeList   = CustomsOfficeList(gbCustomsOffices.getAll ++ xiCustomsOffices.getAll)

  private val exitOffice                                       = arbitrary[CustomsOffice].sample.value
  private val transitOffice                                    = arbitrary[CustomsOffice].sample.value
  private val destinationOffice                                = arbitrary[CustomsOffice].sample.value
  private val mockCustomsOfficesService: CustomsOfficesService = mock[CustomsOfficesService]

  private val allCustomOfficesList = CustomsOfficeList(List(exitOffice, transitOffice, destinationOffice))

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

    "must return OK and the correct view for a GET" - {
      "when only destination office defined" in {

        when(
          mockCustomsOfficesService.getCustomsOffices(any())
        ).thenReturn(CustomsOfficeList(List(destinationOffice)))

        val updatedAnswers = emptyUserAnswers
          .setValue(OfficeOfDestinationPage, destinationOffice)

        setExistingUserAnswers(updatedAnswers)

        mockCustomsOfficesService.getCustomsOffices(any()).getAll mustBe List(destinationOffice)

      }
      //      "when one office of transit is defined" in {
      //
      //        val updatedAnswers = emptyUserAnswers
      //          .setValue(OfficeOfTransitPage(Index(0)), transitOffice)
      //          .setValue(OfficeOfDestinationPage, destinationOffice)
      //
      //        setExistingUserAnswers(updatedAnswers)
      //
      //        val request = FakeRequest(GET, customsOfficeActiveBorderRoute)
      //
      //        val result = route(app, request).value
      //
      //        val view = injector.instanceOf[CustomsOfficeActiveBorderView]
      //
      //        status(result) mustEqual OK
      //
      //        contentAsString(result) mustEqual
      //          view(form, lrn, CustomsOfficeList(List(transitOffice, destinationOffice)), mode, index)(request, messages).toString
      //      }
      //      "when one office of exit is defined" in {
      //
      //        val updatedAnswers = emptyUserAnswers
      //          .setValue(OfficeOfExitPage(Index(0)), exitOffice)
      //          .setValue(OfficeOfDestinationPage, destinationOffice)
      //
      //        setExistingUserAnswers(updatedAnswers)
      //
      //        val request = FakeRequest(GET, customsOfficeActiveBorderRoute)
      //
      //        val result = route(app, request).value
      //
      //        val view = injector.instanceOf[CustomsOfficeActiveBorderView]
      //
      //        status(result) mustEqual OK
      //
      //        contentAsString(result) mustEqual
      //          view(form, lrn, CustomsOfficeList(List(exitOffice, destinationOffice)), mode, index)(request, messages).toString
      //      }
      //      "when one of each office is defined" in {
      //        val updatedAnswers = emptyUserAnswers
      //          .setValue(OfficeOfExitPage(Index(0)), exitOffice)
      //          .setValue(OfficeOfTransitPage(Index(0)), transitOffice)
      //          .setValue(OfficeOfDestinationPage, destinationOffice)
      //
      //        setExistingUserAnswers(updatedAnswers)
      //
      //        val request = FakeRequest(GET, customsOfficeActiveBorderRoute)
      //
      //        val result = route(app, request).value
      //
      //        val view = injector.instanceOf[CustomsOfficeActiveBorderView]
      //
      //        status(result) mustEqual OK
      //
      //        contentAsString(result) mustEqual
      //          view(form, lrn, allCustomOfficesList, mode, index)(request, messages).toString
      //      }
      //      "when multiple office of transit and exit are defined" in {
      //
      //        val exitOffice2 = arbitrary[CustomsOffice].sample.value
      //        val transitOffice2 = arbitrary[CustomsOffice].sample.value
      //        val allCustomOfficesList = CustomsOfficeList(List(exitOffice, exitOffice2, transitOffice, transitOffice2, destinationOffice))
      //
      //        val updatedAnswers = emptyUserAnswers
      //          .setValue(OfficeOfExitPage(Index(0)), exitOffice)
      //          .setValue(OfficeOfExitPage(Index(1)), exitOffice2)
      //          .setValue(OfficeOfTransitPage(Index(0)), transitOffice)
      //          .setValue(OfficeOfTransitPage(Index(1)), transitOffice2)
      //          .setValue(OfficeOfDestinationPage, destinationOffice)
      //
      //        setExistingUserAnswers(updatedAnswers)
      //
      //        val request = FakeRequest(GET, customsOfficeActiveBorderRoute)
      //
      //        val result = route(app, request).value
      //
      //        val view = injector.instanceOf[CustomsOfficeActiveBorderView]
      //
      //        status(result) mustEqual OK
      //
      //        contentAsString(result) mustEqual
      //          view(form, lrn, allCustomOfficesList, mode, index)(request, messages).toString
      //      }
      //    }
    }
  }
}
