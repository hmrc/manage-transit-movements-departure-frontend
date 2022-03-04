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

package controllers.goodsSummary

import base.{AppWithDefaultMockFixtures, SpecBase}
import controllers.{routes => mainRoutes}
import forms.SealsInformationFormProvider
import matchers.JsonMatchers
import models.{Index, NormalMode}
import navigation.annotations.GoodsSummary
import navigation.{FakeNavigator, Navigator}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{times, verify, when}
import org.scalatestplus.mockito.MockitoSugar
import pages.SealIdDetailsPage
import play.api.data.Form
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsObject, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import uk.gov.hmrc.viewmodels.{NunjucksSupport, Radios}

import scala.concurrent.Future

class SealsInformationControllerSpec extends SpecBase with AppWithDefaultMockFixtures with MockitoSugar with NunjucksSupport with JsonMatchers {

  val formProvider        = new SealsInformationFormProvider()
  val form: Form[Boolean] = formProvider(true)

  lazy val sealsInformationRoute: String = routes.SealsInformationController.onPageLoad(lrn, NormalMode).url

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[Navigator]).qualifiedWith(classOf[GoodsSummary]).toInstance(new FakeNavigator(onwardRoute)))

  "SealsInformation Controller" - {

    "must return OK and the correct view for a GET with a single seal" in {

      val updatedAnswers = emptyUserAnswers
        .set(SealIdDetailsPage(Index(0)), sealDomain)
        .success
        .value

      setUserAnswers(Some(updatedAnswers))

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val request                                = FakeRequest(GET, sealsInformationRoute)
      val templateCaptor: ArgumentCaptor[String] = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor: ArgumentCaptor[JsObject]   = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form"           -> form,
        "mode"           -> NormalMode,
        "lrn"            -> lrn,
        "radios"         -> Radios.yesNo(form("value")),
        "pageTitle"      -> "sealsInformation.title.singular",
        "heading"        -> "sealsInformation.heading.singular",
        "allowMoreSeals" -> true
      )

      templateCaptor.getValue mustEqual "sealsInformation.njk"
      jsonCaptor.getValue must containJson(expectedJson)
    }

    "must return OK and the correct view for a GET with multiple seals" in {

      val updatedAnswers = emptyUserAnswers
        .set(SealIdDetailsPage(Index(0)), sealDomain)
        .success
        .value
        .set(SealIdDetailsPage(Index(1)), sealDomain2)
        .success
        .value
      setUserAnswers(Some(updatedAnswers))

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val request                                = FakeRequest(GET, sealsInformationRoute)
      val templateCaptor: ArgumentCaptor[String] = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor: ArgumentCaptor[JsObject]   = ArgumentCaptor.forClass(classOf[JsObject])
      val result                                 = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())
      val expectedJson = Json.obj(
        "form"           -> form,
        "mode"           -> NormalMode,
        "lrn"            -> lrn,
        "radios"         -> Radios.yesNo(form("value")),
        "pageTitle"      -> "sealsInformation.title.plural",
        "heading"        -> "sealsInformation.heading.plural",
        "allowMoreSeals" -> true
      )

      templateCaptor.getValue mustEqual "sealsInformation.njk"
      jsonCaptor.getValue must containJson(expectedJson)
    }

    "must return OK and the correct view for a GET with max seals" in {

      val updatedAnswers = emptyUserAnswers
        .set(SealIdDetailsPage(Index(0)), sealDomain)
        .success
        .value
        .set(SealIdDetailsPage(Index(1)), sealDomain2)
        .success
        .value
        .set(SealIdDetailsPage(Index(2)), sealDomain3)
        .success
        .value

      setUserAnswers(Some(updatedAnswers))

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val request                                = FakeRequest(GET, sealsInformationRoute)
      val templateCaptor: ArgumentCaptor[String] = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor: ArgumentCaptor[JsObject]   = ArgumentCaptor.forClass(classOf[JsObject])
      val result                                 = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())
      val expectedJson = Json.obj(
        "form"           -> form,
        "mode"           -> NormalMode,
        "lrn"            -> lrn,
        "radios"         -> Radios.yesNo(form("value")),
        "pageTitle"      -> "sealsInformation.title.plural",
        "heading"        -> "sealsInformation.heading.plural",
        "allowMoreSeals" -> false
      )

      templateCaptor.getValue mustEqual "sealsInformation.njk"
      jsonCaptor.getValue must containJson(expectedJson)
    }

    "must redirect to the next page when valid data is submitted" in {

      setUserAnswers(Some(emptyUserAnswers))

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val request =
        FakeRequest(POST, sealsInformationRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must redirect to the next page when we have the max seals" in {

      val updatedAnswers = emptyUserAnswers
        .set(SealIdDetailsPage(Index(0)), sealDomain)
        .success
        .value
        .set(SealIdDetailsPage(Index(1)), sealDomain2)
        .success
        .value
        .set(SealIdDetailsPage(Index(2)), sealDomain3)
        .success
        .value

      setUserAnswers(Some(updatedAnswers))

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val request =
        FakeRequest(POST, sealsInformationRoute)
          .withFormUrlEncodedBody(("value", ""))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val updatedAnswers = emptyUserAnswers
        .set(SealIdDetailsPage(Index(0)), sealDomain)
        .success
        .value
      setUserAnswers(Some(updatedAnswers))

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val request                                = FakeRequest(POST, sealsInformationRoute).withFormUrlEncodedBody(("value", ""))
      val boundForm                              = form.bind(Map("value" -> ""))
      val templateCaptor: ArgumentCaptor[String] = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor: ArgumentCaptor[JsObject]   = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form"           -> boundForm,
        "mode"           -> NormalMode,
        "lrn"            -> lrn,
        "radios"         -> Radios.yesNo(boundForm("value")),
        "pageTitle"      -> "sealsInformation.title.singular",
        "heading"        -> "sealsInformation.heading.singular",
        "allowMoreSeals" -> true
      )

      templateCaptor.getValue mustEqual "sealsInformation.njk"
      jsonCaptor.getValue must containJson(expectedJson)
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      setUserAnswers(None)

      val request = FakeRequest(GET, sealsInformationRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual mainRoutes.SessionExpiredController.onPageLoad().url
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      setUserAnswers(None)

      val request =
        FakeRequest(POST, sealsInformationRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual mainRoutes.SessionExpiredController.onPageLoad().url
    }
  }
}
