package controllers.$package$

import base.{AppWithDefaultMockFixtures, SpecBase}
import forms.EnumerableFormProvider
import models.NormalMode
import models.$package$.$className$
import navigation.$navRoute$NavigatorProvider
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import pages.$package$.$className$Page
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.$package$.$className$View

import scala.concurrent.Future

class $className$ControllerSpec extends SpecBase with AppWithDefaultMockFixtures {

  private val formProvider = new EnumerableFormProvider()
  private val form         = formProvider[$className$]("$package$.$className;format="decap"$")
  private val mode         = NormalMode
  private lazy val $className;format="decap"$Route = routes.$className$Controller.onPageLoad(lrn, mode).url

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[$navRoute$NavigatorProvider]).toInstance(fake$navRoute$NavigatorProvider))

  "$className$ Controller" - {

    "must return OK and the correct view for a GET" in {

      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(GET, $className;format="decap"$Route)

      val result = route(app, request).value

      val view = injector.instanceOf[$className$View]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, lrn, $className$.values, mode)(request, messages).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers.setValue($className$Page, $className$.values.head)
      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(GET, $className;format="decap"$Route)

      val result = route(app, request).value

      val filledForm = form.bind(Map("value" -> $className$.values.head.toString))

      val view = injector.instanceOf[$className$View]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(filledForm, lrn, $className$.values, mode)(request, messages).toString
    }

    "must redirect to the next page when valid data is submitted" in {

      when(mockSessionRepository.set(any())(any())).thenReturn(Future.successful(true))

      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(POST, $className;format="decap"$Route)
        .withFormUrlEncodedBody(("value", $className$.values.head.toString))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(POST, $className;format="decap"$Route).withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm = form.bind(Map("value" -> "invalid value"))

      val result = route(app, request).value

      val view = injector.instanceOf[$className$View]

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, lrn, $className$.values, mode)(request, messages).toString
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(GET, $className;format="decap"$Route)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad(Some(lrn)).url
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(POST, $className;format="decap"$Route)
        .withFormUrlEncodedBody(("value", $className$.values.head.toString))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad(Some(lrn)).url
    }
  }
}
