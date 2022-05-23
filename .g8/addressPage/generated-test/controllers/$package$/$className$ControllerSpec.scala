package controllers.$package$

import base.{AppWithDefaultMockFixtures, SpecBase}
import org.scalacheck.Gen
import models.{Address, NormalMode, UserAnswers}
import navigation.Navigator
import navigation.annotations.$navRoute$
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import forms.$formProvider$
import views.html.$package$.$className$View
import pages.$package$.$className$Page
import pages.$package$.$addressHolderNamePage$

import scala.concurrent.Future

class $className$ControllerSpec extends SpecBase with AppWithDefaultMockFixtures with MockitoSugar {

  private val addressHolderName = Gen.alphaNumStr.sample.value
  private val testAddress       = Address("buildingAndStreet", "city", "NE99 1XN")
  private val formProvider      = new $formProvider$()
  private val form              = formProvider("$package$.$className;format="decap"$", addressHolderName)
  private val mode              = NormalMode
  private lazy val $className;format="decap"$Route = routes.$className$Controller.onPageLoad(lrn, mode).url

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[Navigator]).qualifiedWith(classOf[$navRoute$]).toInstance(fakeNavigator))

  "$package$.$className$ Controller" - {

    "must return OK and the correct view for a GET" in {

      val userAnswers = emptyUserAnswers.setValue($addressHolderNamePage$, addressHolderName)
      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(GET, $className;format="decap"$Route)
      val result = route(app, request).value

      val view = injector.instanceOf[$className$View]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, lrn, mode, addressHolderName)(request, messages).toString

    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = UserAnswers(lrn, eoriNumber)
        .setValue($addressHolderNamePage$, addressHolderName)
        .setValue($className$Page, testAddress)

      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(GET, $className;format="decap"$Route)

      val result = route(app, request).value

      val filledForm = form.bind(
        Map(
          "buildingAndStreet" -> testAddress.buildingAndStreet,
          "city"              -> testAddress.city,
          "postcode"          -> testAddress.postcode
        )
      )

      val view = injector.instanceOf[$className$View]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(filledForm, lrn, mode, addressHolderName)(request, messages).toString

    }

    "must redirect to the next page when valid data is submitted" in {

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val userAnswers = emptyUserAnswers.setValue($addressHolderNamePage$, addressHolderName)
      setExistingUserAnswers(userAnswers)

      val request =
        FakeRequest(POST, $className;format="decap"$Route)
          .withFormUrlEncodedBody(
            ("buildingAndStreet", testAddress.buildingAndStreet),
            ("city", testAddress.city),
            ("postcode", testAddress.postcode)
      )

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url

    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val userAnswers = emptyUserAnswers.setValue($addressHolderNamePage$, addressHolderName)
      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(POST, $className;format="decap"$Route).withFormUrlEncodedBody(("value", ""))
      val boundForm = form.bind(Map("value" -> ""))

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST

      val view = injector.instanceOf[$className$View]

      contentAsString(result) mustEqual
        view(boundForm, lrn, mode, addressHolderName)(request, messages).toString

    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(GET, $className;format="decap"$Route)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      setNoExistingUserAnswers()

      val request =
        FakeRequest(POST, $className;format="decap"$Route)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

    }
  }
}
