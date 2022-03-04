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
import matchers.JsonMatchers
import models.{DocumentTypeList, NormalMode}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{times, verify, when}
import org.scalatestplus.mockito.MockitoSugar
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsObject, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import services.DocumentTypesService
import uk.gov.hmrc.viewmodels.NunjucksSupport

import scala.concurrent.Future

class DocumentCheckYourAnswersControllerSpec extends SpecBase with AppWithDefaultMockFixtures with MockitoSugar with NunjucksSupport with JsonMatchers {

  lazy val documentCyaRoute: String =
    routes.DocumentCheckYourAnswersController.onPageLoad(lrn, itemIndex, documentIndex, NormalMode).url

  private val mockDocumentTypesService: DocumentTypesService = mock[DocumentTypesService]

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[DocumentTypesService]).toInstance(mockDocumentTypesService))

  "DocumentCheckYourAnswersController" - {

    "must return OK and the correct view for a GET" in {

      setUserAnswers(Some(emptyUserAnswers))

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      when(mockDocumentTypesService.getDocumentTypes()(any()))
        .thenReturn(Future.successful(DocumentTypeList(Nil)))

      val request                                = FakeRequest(GET, documentCyaRoute)
      val templateCaptor: ArgumentCaptor[String] = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor: ArgumentCaptor[JsObject]   = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "nextPageUrl" -> routes.AddAnotherDocumentController.onPageLoad(lrn, itemIndex, NormalMode).url
      )

      val jsonWithoutConfig = jsonCaptor.getValue - configKey - "section"

      templateCaptor.getValue mustEqual "addItems/documentCheckYourAnswers.njk"
      jsonWithoutConfig mustBe expectedJson
    }
  }
}
