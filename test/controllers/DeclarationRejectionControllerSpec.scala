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

package controllers

import base.{AppWithDefaultMockFixtures, SpecBase}
import matchers.JsonMatchers
import models.{DeclarationRejectionMessage, RejectionError}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, times, verify, when}
import org.scalatestplus.mockito.MockitoSugar
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsObject, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import services.DepartureMessageService

import java.time.LocalDate
import scala.concurrent.Future

class DeclarationRejectionControllerSpec extends SpecBase with AppWithDefaultMockFixtures with MockitoSugar with JsonMatchers {

  private val mockDepartureMessageService = mock[DepartureMessageService]
  private val template                    = "declarationRejection.njk"

  override def beforeEach(): Unit = {
    reset(mockDepartureMessageService)
    super.beforeEach()
  }

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind[DepartureMessageService].toInstance(mockDepartureMessageService))

  "DeclarationRejection Controller" - {

    "return OK and the correct view for a GET" in {

      val rejectionErrors = Seq(RejectionError("type1", "pointer"), RejectionError("type1", "pointer"))

      val message = DeclarationRejectionMessage(
        reference = "123456",
        rejectionDate = LocalDate.parse("2010-10-10"),
        reason = None,
        errors = rejectionErrors
      )

      when(mockDepartureMessageService.declarationRejectionMessage(any())(any(), any()))
        .thenReturn(Future.successful(Some(message)))

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      setUserAnswers(Some(emptyUserAnswers))

      val request                                = FakeRequest(GET, routes.DeclarationRejectionController.onPageLoad(departureId).url)
      val templateCaptor: ArgumentCaptor[String] = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor: ArgumentCaptor[JsObject]   = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson =
        Json.obj(
          "contactUrl"   -> "https://www.gov.uk/government/organisations/hm-revenue-customs/contact/new-computerised-transit-system-enquiries",
          "departureUrl" -> routes.LocalReferenceNumberController.onPageLoad().url
        )

      val jsonWithoutConfig = jsonCaptor.getValue - configKey - "detailsSection" - "errorsSection"

      templateCaptor.getValue mustEqual template
      jsonWithoutConfig mustBe expectedJson
    }
  }

  "show technical difficulties page when no response from service" in {

    when(mockDepartureMessageService.declarationRejectionMessage(any())(any(), any()))
      .thenReturn(Future.successful(None))

    when(mockRenderer.render(any(), any())(any()))
      .thenReturn(Future.successful(Html("")))

    setUserAnswers(Some(emptyUserAnswers))

    val request = FakeRequest(GET, routes.DeclarationRejectionController.onPageLoad(departureId).url)

    val result = route(app, request).value

    status(result) mustEqual INTERNAL_SERVER_ERROR
  }
}
