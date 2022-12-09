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

package controllers.transport.transportMeans.active

import base.{AppWithDefaultMockFixtures, SpecBase}
import forms.EnumerableFormProvider
import generators.Generators
import models.{Index, NormalMode}
import models.transport.transportMeans.BorderModeOfTransport
import models.transport.transportMeans.active.Identification
import navigation.transport.TransportMeansActiveNavigatorProvider
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import pages.transport.transportMeans.BorderModeOfTransportPage
import pages.transport.transportMeans.active.IdentificationPage
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.govukfrontend.views.html.components.implicits._
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem
import views.html.transport.transportMeans.active.IdentificationView

import scala.concurrent.Future

class IdentificationControllerSpec extends SpecBase with AppWithDefaultMockFixtures with Generators {

  private val formProvider             = new EnumerableFormProvider()
  private val form                     = formProvider[Identification]("transport.transportMeans.active.identification")
  private val mode                     = NormalMode
  private lazy val identificationRoute = routes.IdentificationController.onPageLoad(lrn, mode, activeIndex).url

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[TransportMeansActiveNavigatorProvider]).toInstance(fakeTransportMeansActiveNavigatorProvider))

  "Identification Controller" - {

    "must return OK and the correct view for a GET" - {
      "at index position '0'" - {
        "when BorderModeOfTransport is 'Maritime'" in {

          val radioItems: Seq[RadioItem] = Seq(
            RadioItem(content = "IMO ship identification number".toText, id = Some("value"), value = Some("imoShipIdNumber"), checked = false),
            RadioItem(content = "Name of a sea-going vessel".toText, id = Some("value_1"), value = Some("seaGoingVessel"), checked = false)
          )

          val updatedUserAnswers = emptyUserAnswers
            .setValue(BorderModeOfTransportPage, BorderModeOfTransport.Maritime)

          setExistingUserAnswers(updatedUserAnswers)

          val request = FakeRequest(GET, identificationRoute)

          val result = route(app, request).value

          val view = injector.instanceOf[IdentificationView]

          status(result) mustEqual OK

          contentAsString(result) mustEqual
            view(form, lrn, (_, _) => radioItems, mode, activeIndex)(request, messages).toString
        }

        "when BorderModeOfTransport is 'Air'" in {

          val radioItems: Seq[RadioItem] = Seq(
            RadioItem(content = "IATA flight number".toText, id = Some("value"), value = Some("iataFlightNumber"), checked = false),
            RadioItem(content = "Registration number of an aircraft".toText, id = Some("value_1"), value = Some("regNumberAircraft"), checked = false)
          )

          val updatedUserAnswers = emptyUserAnswers
            .setValue(BorderModeOfTransportPage, BorderModeOfTransport.Air)

          setExistingUserAnswers(updatedUserAnswers)

          val request = FakeRequest(GET, identificationRoute)

          val result = route(app, request).value

          val view = injector.instanceOf[IdentificationView]

          status(result) mustEqual OK

          contentAsString(result) mustEqual
            view(form, lrn, (_, _) => radioItems, mode, activeIndex)(request, messages).toString
        }

        "when BorderModeOfTransport is 'Mail'" in {

          val radioItems: Seq[RadioItem] = Seq(
            RadioItem(content = "IMO ship identification number".toText, id = Some("value"), value = Some("imoShipIdNumber"), checked = false),
            RadioItem(content = "Name of a sea-going vessel".toText, id = Some("value_1"), value = Some("seaGoingVessel"), checked = false),
            RadioItem(content = "Train number".toText, id = Some("value_2"), value = Some("trainNumber"), checked = false),
            RadioItem(content = "Registration number of a road vehicle".toText, id = Some("value_3"), value = Some("regNumberRoadVehicle"), checked = false),
            RadioItem(content = "IATA flight number".toText, id = Some("value_4"), value = Some("iataFlightNumber"), checked = false),
            RadioItem(content = "Registration number of an aircraft".toText, id = Some("value_5"), value = Some("regNumberAircraft"), checked = false),
            RadioItem(content = "European vessel identification number (ENI code)".toText,
                      id = Some("value_6"),
                      value = Some("europeanVesselIdNumber"),
                      checked = false
            ),
            RadioItem(content = "Name of an inland waterways vehicle".toText, id = Some("value_7"), value = Some("inlandWaterwaysVehicle"), checked = false)
          )

          val updatedUserAnswers = emptyUserAnswers
            .setValue(BorderModeOfTransportPage, BorderModeOfTransport.Mail)

          setExistingUserAnswers(updatedUserAnswers)

          val request = FakeRequest(GET, identificationRoute)

          val result = route(app, request).value

          val view = injector.instanceOf[IdentificationView]

          status(result) mustEqual OK

          contentAsString(result) mustEqual
            view(form, lrn, (_, _) => radioItems, mode, activeIndex)(request, messages).toString
        }

        "when BorderModeOfTransport is 'Fixed'" in {

          val radioItems: Seq[RadioItem] = Seq(
            RadioItem(content = "IMO ship identification number".toText, id = Some("value"), value = Some("imoShipIdNumber"), checked = false),
            RadioItem(content = "Name of a sea-going vessel".toText, id = Some("value_1"), value = Some("seaGoingVessel"), checked = false),
            RadioItem(content = "Train number".toText, id = Some("value_2"), value = Some("trainNumber"), checked = false),
            RadioItem(content = "Registration number of a road vehicle".toText, id = Some("value_3"), value = Some("regNumberRoadVehicle"), checked = false),
            RadioItem(content = "IATA flight number".toText, id = Some("value_4"), value = Some("iataFlightNumber"), checked = false),
            RadioItem(content = "Registration number of an aircraft".toText, id = Some("value_5"), value = Some("regNumberAircraft"), checked = false),
            RadioItem(content = "European vessel identification number (ENI code)".toText,
                      id = Some("value_6"),
                      value = Some("europeanVesselIdNumber"),
                      checked = false
            ),
            RadioItem(content = "Name of an inland waterways vehicle".toText, id = Some("value_7"), value = Some("inlandWaterwaysVehicle"), checked = false)
          )

          val updatedUserAnswers = emptyUserAnswers
            .setValue(BorderModeOfTransportPage, BorderModeOfTransport.Fixed)

          setExistingUserAnswers(updatedUserAnswers)

          val request = FakeRequest(GET, identificationRoute)

          val result = route(app, request).value

          val view = injector.instanceOf[IdentificationView]

          status(result) mustEqual OK

          contentAsString(result) mustEqual
            view(form, lrn, (_, _) => radioItems, mode, activeIndex)(request, messages).toString
        }

        "when BorderModeOfTransport is 'Waterway'" in {

          val radioItems: Seq[RadioItem] = Seq(
            RadioItem(content = "European vessel identification number (ENI code)".toText,
                      id = Some("value"),
                      value = Some("europeanVesselIdNumber"),
                      checked = false
            ),
            RadioItem(content = "Name of an inland waterways vehicle".toText, id = Some("value_1"), value = Some("inlandWaterwaysVehicle"), checked = false)
          )

          val updatedUserAnswers = emptyUserAnswers
            .setValue(BorderModeOfTransportPage, BorderModeOfTransport.Waterway)

          setExistingUserAnswers(updatedUserAnswers)

          val request = FakeRequest(GET, identificationRoute)

          val result = route(app, request).value

          val view = injector.instanceOf[IdentificationView]

          status(result) mustEqual OK

          contentAsString(result) mustEqual
            view(form, lrn, (_, _) => radioItems, mode, activeIndex)(request, messages).toString
        }
      }

      "at index position '1'" in {
        val radioItems: Seq[RadioItem] = Seq(
          RadioItem(content = "IMO ship identification number".toText, id = Some("value"), value = Some("imoShipIdNumber"), checked = false),
          RadioItem(content = "Name of a sea-going vessel".toText, id = Some("value_1"), value = Some("seaGoingVessel"), checked = false),
          RadioItem(content = "Train number".toText, id = Some("value_2"), value = Some("trainNumber"), checked = false),
          RadioItem(content = "Registration number of a road vehicle".toText, id = Some("value_3"), value = Some("regNumberRoadVehicle"), checked = false),
          RadioItem(content = "IATA flight number".toText, id = Some("value_4"), value = Some("iataFlightNumber"), checked = false),
          RadioItem(content = "Registration number of an aircraft".toText, id = Some("value_5"), value = Some("regNumberAircraft"), checked = false),
          RadioItem(content = "European vessel identification number (ENI code)".toText,
                    id = Some("value_6"),
                    value = Some("europeanVesselIdNumber"),
                    checked = false
          ),
          RadioItem(content = "Name of an inland waterways vehicle".toText, id = Some("value_7"), value = Some("inlandWaterwaysVehicle"), checked = false)
        )

        val updatedUserAnswers = emptyUserAnswers
          .setValue(BorderModeOfTransportPage, arbitraryBorderModeOfTransport.arbitrary.sample.get)

        setExistingUserAnswers(updatedUserAnswers)

        val request = FakeRequest(GET, routes.IdentificationController.onPageLoad(lrn, mode, Index(1)).url)

        val result = route(app, request).value

        val view = injector.instanceOf[IdentificationView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(form, lrn, (_, _) => radioItems, mode, Index(1))(request, messages).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val radioItems: Seq[RadioItem] = Seq(
        RadioItem(content = "IMO ship identification number".toText, id = Some("value"), value = Some("imoShipIdNumber"), checked = false),
        RadioItem(content = "Name of a sea-going vessel".toText, id = Some("value_1"), value = Some("seaGoingVessel"), checked = false),
        RadioItem(content = "Train number".toText, id = Some("value_2"), value = Some("trainNumber"), checked = true),
        RadioItem(content = "Registration number of a road vehicle".toText, id = Some("value_3"), value = Some("regNumberRoadVehicle"), checked = false),
        RadioItem(content = "IATA flight number".toText, id = Some("value_4"), value = Some("iataFlightNumber"), checked = false),
        RadioItem(content = "Registration number of an aircraft".toText, id = Some("value_5"), value = Some("regNumberAircraft"), checked = false),
        RadioItem(content = "European vessel identification number (ENI code)".toText,
                  id = Some("value_6"),
                  value = Some("europeanVesselIdNumber"),
                  checked = false
        ),
        RadioItem(content = "Name of an inland waterways vehicle".toText, id = Some("value_7"), value = Some("inlandWaterwaysVehicle"), checked = false)
      )

      val userAnswers = emptyUserAnswers.setValue(IdentificationPage(activeIndex), Identification.TrainNumber)
      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(GET, identificationRoute)

      val result = route(app, request).value

      val filledForm = form.bind(Map("value" -> Identification.values.head.toString))

      val view = injector.instanceOf[IdentificationView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(filledForm, lrn, (_, _) => radioItems, mode, activeIndex)(request, messages).toString
    }

    "must redirect to the next page when valid data is submitted" in {

      when(mockSessionRepository.set(any())(any())) thenReturn Future.successful(true)

      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(POST, identificationRoute)
        .withFormUrlEncodedBody(("value", Identification.values.head.toString))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val radioItems: Seq[RadioItem] = Seq(
        RadioItem(content = "IMO ship identification number".toText, id = Some("value"), value = Some("imoShipIdNumber"), checked = false),
        RadioItem(content = "Name of a sea-going vessel".toText, id = Some("value_1"), value = Some("seaGoingVessel"), checked = false),
        RadioItem(content = "Train number".toText, id = Some("value_2"), value = Some("trainNumber"), checked = false),
        RadioItem(content = "Registration number of a road vehicle".toText, id = Some("value_3"), value = Some("regNumberRoadVehicle"), checked = false),
        RadioItem(content = "IATA flight number".toText, id = Some("value_4"), value = Some("iataFlightNumber"), checked = false),
        RadioItem(content = "Registration number of an aircraft".toText, id = Some("value_5"), value = Some("regNumberAircraft"), checked = false),
        RadioItem(content = "European vessel identification number (ENI code)".toText,
                  id = Some("value_6"),
                  value = Some("europeanVesselIdNumber"),
                  checked = false
        ),
        RadioItem(content = "Name of an inland waterways vehicle".toText, id = Some("value_7"), value = Some("inlandWaterwaysVehicle"), checked = false)
      )

      setExistingUserAnswers(emptyUserAnswers)

      val request   = FakeRequest(POST, identificationRoute).withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm = form.bind(Map("value" -> "invalid value"))

      val result = route(app, request).value

      val view = injector.instanceOf[IdentificationView]

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, lrn, (_, _) => radioItems, mode, activeIndex)(request, messages).toString
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(GET, identificationRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(POST, identificationRoute)
        .withFormUrlEncodedBody(("value", Identification.values.head.toString))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }
  }
}
