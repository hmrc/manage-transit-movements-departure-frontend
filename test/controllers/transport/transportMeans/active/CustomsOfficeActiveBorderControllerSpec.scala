package controllers.transport.transportMeans.active

import base.{AppWithDefaultMockFixtures, SpecBase}
import forms.CustomsOfficeFormProvider
import models.{CustomsOfficeList, NormalMode}
import generators.Generators
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import pages.transport.transportMeans.active.CustomsOfficeActiveBorderPage
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.CustomsOfficesService
import views.html.transport.transportMeans.active.CustomsOfficeActiveBorderView

import scala.concurrent.Future

class CustomsOfficeActiveBorderControllerSpec extends SpecBase with AppWithDefaultMockFixtures with Generators {

  private val customsOffice1    = arbitraryCustomsOffice.arbitrary.sample.get
  private val customsOffice2    = arbitraryCustomsOffice.arbitrary.sample.get
  private val customsOfficeList = CustomsOfficeList(Seq(customsOffice1, customsOffice2))

  private val formProvider = new CustomsOfficeFormProvider()
  private val form         = formProvider("transport.transportMeans.active.customsOfficeActiveBorder", customsOfficeList)
  private val mode         = NormalMode

  private val mockCustomsOfficesService: CustomsOfficesService = mock[CustomsOfficesService]
  private lazy val customsOfficeActiveBorderRoute              = routes.CustomsOfficeActiveBorderController.onPageLoad(lrn, mode).url

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[PreTaskListDetailsNavigatorProvider]).toInstance(fakePreTaskListDetailsNavigatorProvider))
      .overrides(bind(classOf[CustomsOfficesService]).toInstance(mockCustomsOfficesService))

  "CustomsOfficeActiveBorder Controller" - {

    "must return OK and the correct view for a GET" in {

      when(mockCustomsOfficesService.getAllCustomsOffices(any())).thenReturn(Future.successful(customsOfficeList))
      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(GET, customsOfficeActiveBorderRoute)

      val result = route(app, request).value

      val view = injector.instanceOf[CustomsOfficeActiveBorderView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, lrn, customsOfficeList.customsOfficeList, mode)(request, messages).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      when(mockCustomsOfficesService.getAllCustomsOffices(any())).thenReturn(Future.successful(customsOfficeList))
      val userAnswers = emptyUserAnswers.setValue(CustomsOfficeActiveBorderPage, customsOffice1)
      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(GET, customsOfficeActiveBorderRoute)

      val result = route(app, request).value

      val filledForm = form.bind(Map("value" -> customsOffice1.id))

      val view = injector.instanceOf[CustomsOfficeActiveBorderView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(filledForm, lrn, customsOfficeList.customsOfficeList, mode)(request, messages).toString
    }

    "must redirect to the next page when valid data is submitted" in {

      when(mockCustomsOfficesService.getAllCustomsOffices(any())).thenReturn(Future.successful(customsOfficeList))
      when(mockSessionRepository.set(any())(any())) thenReturn Future.successful(true)

      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(POST, customsOfficeActiveBorderRoute)
        .withFormUrlEncodedBody(("value", customsOffice1.id))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      when(mockCustomsOfficesService.getAllCustomsOffices(any())).thenReturn(Future.successful(customsOfficeList))
      setExistingUserAnswers(emptyUserAnswers)

      val request   = FakeRequest(POST, customsOfficeActiveBorderRoute).withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm = form.bind(Map("value" -> "invalid value"))

      val result = route(app, request).value

      val view = injector.instanceOf[CustomsOfficeActiveBorderView]

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, lrn, customsOfficeList.customsOfficeList, mode)(request, messages).toString
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(GET, customsOfficeActiveBorderRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(POST, customsOfficeActiveBorderRoute)
        .withFormUrlEncodedBody(("value", customsOffice1.id))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }
  }
}
