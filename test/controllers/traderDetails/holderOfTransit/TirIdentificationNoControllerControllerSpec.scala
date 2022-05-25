package controllers.traderDetails.holderOfTransit

import models.{NormalMode, UserAnswers}
import navigation.Navigator
import navigation.annotations.TraderDetails
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, when}
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import forms.EoriNumberFormProvider
import views.html.traderDetails.holderOfTransit.TirIdentificationNoControllerView
import services.UserAnswersService
import pages.traderDetails.holderOfTransit.TirIdentificationNoControllerPage
import base.{AppWithDefaultMockFixtures, SpecBase}

import scala.concurrent.Future

class TirIdentificationNoControllerControllerSpec extends SpecBase with AppWithDefaultMockFixtures {

  private val formProvider = new EoriNumberFormProvider()
  private val form         = formProvider("traderDetails.holderOfTransit.tirIdentificationNoController")
  private val mode         = NormalMode
  private lazy val tirIdentificationNoControllerRoute = routes.TirIdentificationNoControllerController.onPageLoad(lrn, mode).url
  private lazy val mockUserAnswersService = mock[UserAnswersService]

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[Navigator]).qualifiedWith(classOf[TraderDetails]).toInstance(fakeNavigator))
      .overrides(bind[UserAnswersService].toInstance(mockUserAnswersService))

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockUserAnswersService)
  }

  "TirIdentificationNoController Controller" - {

    "must return OK and the correct view for a GET" in {

      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(GET, tirIdentificationNoControllerRoute)

      val result = route(app, request).value

      val view = injector.instanceOf[TirIdentificationNoControllerView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, lrn, mode)(request, messages).toString

    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = UserAnswers(lrn, eoriNumber).set(TirIdentificationNoControllerPage, "test string").success.value
      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(GET, tirIdentificationNoControllerRoute)

      val result = route(app, request).value

      val filledForm = form.bind(Map("value" -> "test string"))

      val view = injector.instanceOf[TirIdentificationNoControllerView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(filledForm, lrn, mode)(request, messages).toString

    }

    "must redirect to the next page when valid data is submitted" in {

      setExistingUserAnswers(emptyUserAnswers)

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)
      when(mockUserAnswersService.getOrCreateUserAnswers(any(), any())) thenReturn Future.successful(emptyUserAnswers)

      val request =
        FakeRequest(POST, tirIdentificationNoControllerRoute)
          .withFormUrlEncodedBody(("value", "test string"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      setExistingUserAnswers(emptyUserAnswers)

      val invalidAnswer = ""

      val request = FakeRequest(POST, tirIdentificationNoControllerRoute).withFormUrlEncodedBody(("value", ""))
      val filledForm = form.bind(Map("value" -> invalidAnswer))

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST

      val view = injector.instanceOf[TirIdentificationNoControllerView]

      contentAsString(result) mustEqual
        view(filledForm, lrn, mode)(request, messages).toString

    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(GET, tirIdentificationNoControllerRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      setNoExistingUserAnswers()

      val request =
        FakeRequest(POST, tirIdentificationNoControllerRoute)
          .withFormUrlEncodedBody(("value", "test string"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

    }
  }
}
