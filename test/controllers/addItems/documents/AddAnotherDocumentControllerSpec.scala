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

package controllers.addItems.documents

import base.{AppWithDefaultMockFixtures, SpecBase}
import controllers.{routes => mainRoutes}
import forms.addItems.AddAnotherDocumentFormProvider
import matchers.JsonMatchers
import models.reference.DocumentType
import models.{DocumentTypeList, Index, NormalMode}
import navigation.Navigator
import navigation.annotations.addItems.AddItemsDocument
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{times, verify, when}
import org.scalatestplus.mockito.MockitoSugar
import pages.addItems.DocumentTypePage
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsObject, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import services.DocumentTypesService
import uk.gov.hmrc.viewmodels.{NunjucksSupport, Radios}

import scala.concurrent.Future

class AddAnotherDocumentControllerSpec extends SpecBase with AppWithDefaultMockFixtures with MockitoSugar with NunjucksSupport with JsonMatchers {

  private val formProvider                                   = new AddAnotherDocumentFormProvider()
  private val form                                           = formProvider(true)
  private val template                                       = "addItems/addAnotherDocument.njk"
  private val mockDocumentTypesService: DocumentTypesService = mock[DocumentTypesService]
  val documentType1: DocumentType                            = DocumentType("1", "11", transportDocument = true)
  val documentType2: DocumentType                            = DocumentType("2", "22", transportDocument = true)
  val documentTypeList: DocumentTypeList                     = DocumentTypeList(Seq(documentType1, documentType2))
  private lazy val addAnotherDocumentRoute                   = controllers.addItems.documents.routes.AddAnotherDocumentController.onPageLoad(lrn, index, NormalMode).url

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[Navigator]).qualifiedWith(classOf[AddItemsDocument]).toInstance(fakeNavigator))
      .overrides(bind(classOf[DocumentTypesService]).toInstance(mockDocumentTypesService))

  "AddAnotherDocument Controller" - {

    "must return OK and the correct view for a GET" in {
      setUserAnswers(Some(emptyUserAnswers))
      when(mockDocumentTypesService.getDocumentTypes()(any())).thenReturn(Future.successful(documentTypeList))

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val request                                = FakeRequest(GET, addAnotherDocumentRoute)
      val templateCaptor: ArgumentCaptor[String] = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor: ArgumentCaptor[JsObject]   = ArgumentCaptor.forClass(classOf[JsObject])
      val result                                 = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form"               -> form,
        "index"              -> index.display,
        "lrn"                -> lrn,
        "mode"               -> NormalMode,
        "pageTitle"          -> msg"addAnotherDocument.title.plural".withArgs(1),
        "heading"            -> msg"addAnotherDocument.heading.plural".withArgs(1),
        "radios"             -> Radios.yesNo(form("value")),
        "allowMoreDocuments" -> true
      )
      templateCaptor.getValue mustEqual template
      jsonCaptor.getValue must containJson(expectedJson)

    }

    "must redirect to the next page when valid data is submitted" in {
      setUserAnswers(Some(emptyUserAnswers))
      when(mockDocumentTypesService.getDocumentTypes()(any())).thenReturn(Future.successful(documentTypeList))
      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val request =
        FakeRequest(POST, addAnotherDocumentRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.addItems.documents.routes.DocumentTypeController.onPageLoad(lrn, itemIndex, index, NormalMode).url
    }

    "must redirect to correct page when reached maximum number of documents" in {

      val userAnswers = emptyUserAnswers
        .set(DocumentTypePage(itemIndex, Index(0)), "12345")
        .success
        .value
        .set(DocumentTypePage(itemIndex, Index(1)), "12345")
        .success
        .value
        .set(DocumentTypePage(itemIndex, Index(2)), "12345")
        .success
        .value

      setUserAnswers(Some(userAnswers))

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val request =
        FakeRequest(POST, addAnotherDocumentRoute)
          .withFormUrlEncodedBody(("value", ""))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url

    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      setUserAnswers(Some(emptyUserAnswers))
      when(mockDocumentTypesService.getDocumentTypes()(any())).thenReturn(Future.successful(documentTypeList))

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val request                                = FakeRequest(POST, addAnotherDocumentRoute).withFormUrlEncodedBody(("value", ""))
      val boundForm                              = form.bind(Map("value" -> ""))
      val templateCaptor: ArgumentCaptor[String] = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor: ArgumentCaptor[JsObject]   = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form"               -> boundForm,
        "pageTitle"          -> msg"addAnotherDocument.title.plural".withArgs(1),
        "heading"            -> msg"addAnotherDocument.heading.plural".withArgs(1),
        "lrn"                -> lrn,
        "mode"               -> NormalMode,
        "radios"             -> Radios.yesNo(boundForm("value")),
        "allowMoreDocuments" -> true
      )

      templateCaptor.getValue mustEqual template
      jsonCaptor.getValue must containJson(expectedJson)

    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      setUserAnswers(None)

      val request = FakeRequest(GET, addAnotherDocumentRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual mainRoutes.SessionExpiredController.onPageLoad().url

    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      setUserAnswers(None)

      val request =
        FakeRequest(POST, addAnotherDocumentRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual mainRoutes.SessionExpiredController.onPageLoad().url

    }
  }
}
