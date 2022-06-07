package controllers.$package$

import base.{AppWithDefaultMockFixtures, SpecBase}
import forms.$formProvider$
import generators.Generators
import models.{Address, CountryList, NormalMode, UserAnswers}
import navigation.Navigator
import navigation.annotations.$navRoute$
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, when}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import pages.$package$.$className$Page
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.CountriesService
import views.html.$package$.$className$View
import $addressHolderNameImport$
import pages.$package$.$className$Page

import scala.concurrent.Future

class $className$ControllerSpec extends SpecBase with AppWithDefaultMockFixtures with Generators {

  private val addressHolderName = Gen.alphaNumStr.sample.value

  private val testAddress = arbitrary[Address].sample.value
  private val countryList = CountryList(Seq(testAddress.country))

  private val formProvider      = new $formProvider$()
  private val form              = formProvider("$package$.$className;format="decap"$", addressHolderName, countryList)

  private val mode                                 = NormalMode
  private lazy val $className;format="decap"$Route = routes.$className$Controller.onPageLoad(lrn, mode).url

  private lazy val mockCountriesService: CountriesService = mock[CountriesService]

  override def beforeEach(): Unit = {
    reset(mockCountriesService)
    super.beforeEach()
  }

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[Navigator]).qualifiedWith(classOf[$navRoute$]).toInstance(fakeNavigator))
      .overrides(bind(classOf[CountriesService]).toInstance(mockCountriesService))

  "$className$ Controller" - {

    "must return OK and the correct view for a GET" in {

      when(mockCountriesService.getCountries()(any())).thenReturn(Future.successful(countryList))

      val userAnswers = emptyUserAnswers.setValue($addressHolderNamePage$, addressHolderName)
      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(GET, $className;format="decap"$Route)
      val result = route(app, request).value

      val view = injector.instanceOf[$className$View]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, lrn, mode, countryList.countries, addressHolderName)(request, messages).toString

    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      when(mockCountriesService.getCountries()(any())).thenReturn(Future.successful(countryList))

      val userAnswers = UserAnswers(lrn, eoriNumber)
        .setValue($addressHolderNamePage$, addressHolderName)
        .setValue($className$Page, testAddress)

      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(GET, $className;format="decap"$Route)

      val result = route(app, request).value

      val filledForm = form.bind(
        Map(
          "addressLine1" -> testAddress.line1,
          "addressLine2" -> testAddress.line2,
          "postalCode"   -> testAddress.postalCode,
          "country"      -> testAddress.country.code.code
        )
      )

      val view = injector.instanceOf[$className$View]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(filledForm, lrn, mode, countryList.countries, addressHolderName)(request, messages).toString

    }

    "must redirect to the next page when valid data is submitted" in {

      when(mockCountriesService.getCountries()(any())).thenReturn(Future.successful(countryList))
      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val userAnswers = emptyUserAnswers.setValue($addressHolderNamePage$, addressHolderName)
      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(POST, $className;format="decap"$Route)
        .withFormUrlEncodedBody(
          ("addressLine1", testAddress.line1),
          ("addressLine2", testAddress.line2),
          ("postalCode", testAddress.postalCode),
          ("country", testAddress.country.code.code)
        )

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url

    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      when(mockCountriesService.getCountries()(any())).thenReturn(Future.successful(countryList))

      val userAnswers = emptyUserAnswers.setValue($addressHolderNamePage$, addressHolderName)
      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(POST, $className;format="decap"$Route).withFormUrlEncodedBody(("value", ""))
      val boundForm = form.bind(Map("value" -> ""))

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST

      val view = injector.instanceOf[$className$View]

      contentAsString(result) mustEqual
        view(boundForm, lrn, mode, countryList.countries, addressHolderName)(request, messages).toString

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

      val request = FakeRequest(POST, $className;format="decap"$Route)
        .withFormUrlEncodedBody(("value", "true"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

    }
  }
}
