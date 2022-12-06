package controllers.transport.transportMeans.active

import base.{SpecBase, AppWithDefaultMockFixtures}
import forms.YesNoFormProvider
import models.NormalMode
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.transport.transportMeans.active.AddAnotherBorderTransportPage
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.transport.transportMeans.active.AddAnotherBorderTransportView

import scala.concurrent.Future

class AddAnotherBorderTransportControllerSpec extends SpecBase with AppWithDefaultMockFixtures with MockitoSugar {

  private val formProvider = new YesNoFormProvider()
  private val form         = formProvider("transport.transportMeans.active.addAnotherBorderTransport")
  private val mode         = NormalMode
  private lazy val addAnotherBorderTransportRoute = routes.AddAnotherBorderTransportController.onPageLoad(lrn, mode).url

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[TransportMeansNavigatorProvider]).toInstance(fakeTransportMeansNavigatorProvider))

  "AddAnotherBorderTransport Controller" - {

    "must return OK and the correct view for a GET" in {

      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(GET, addAnotherBorderTransportRoute)
      val result = route(app, request).value

      val view = injector.instanceOf[AddAnotherBorderTransportView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, lrn, mode)(request, messages).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers.setValue(AddAnotherBorderTransportPage, true)
      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(GET, addAnotherBorderTransportRoute)

      val result = route(app, request).value

      val filledForm = form.bind(Map("value" -> "true"))

      val view = injector.instanceOf[AddAnotherBorderTransportView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(filledForm, lrn, mode)(request, messages).toString
    }

    "must redirect to the next page when valid data is submitted" in {

      when(mockSessionRepository.set(any())(any())) thenReturn Future.successful(true)

      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(POST, addAnotherBorderTransportRoute)
      .withFormUrlEncodedBody(("value", "true"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(POST, addAnotherBorderTransportRoute).withFormUrlEncodedBody(("value", ""))
      val boundForm = form.bind(Map("value" -> ""))

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST

      val view = injector.instanceOf[AddAnotherBorderTransportView]

      contentAsString(result) mustEqual
        view(boundForm, lrn, mode)(request, messages).toString
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(GET, addAnotherBorderTransportRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(POST, addAnotherBorderTransportRoute)
        .withFormUrlEncodedBody(("value", "true"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }
  }
}
