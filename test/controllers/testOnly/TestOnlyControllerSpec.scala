/*
 * Copyright 2024 HM Revenue & Customs
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

package controllers.testOnly

import base.{AppWithDefaultMockFixtures, SpecBase}
import generators.Generators
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.{any, eq => eqTo}
import org.mockito.Mockito.{verify, when}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.Future

class TestOnlyControllerSpec extends SpecBase with AppWithDefaultMockFixtures with Generators {

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .configure("play.http.router" -> "testOnlyDoNotUseInAppConf.Routes")

  "TestOnlyController" - {

    "setUserAnswers" - {

      val bearerToken        = nonEmptyString.sample.value
      val sessionId          = nonEmptyString.sample.value
      lazy val testOnlyRoute = routes.TestOnlyController.setUserAnswers(sessionId).url

      "when answers successfully submitted to cache" - {
        "must return Ok" in {
          when(mockSessionRepository.set(any())(any())).thenReturn(Future.successful(true))

          val userAnswers = emptyUserAnswers

          val request = FakeRequest(POST, testOnlyRoute)
            .withHeaders("Authorization" -> bearerToken)
            .withJsonBody(Json.toJson(userAnswers))

          val result = route(app, request).value

          status(result) mustEqual OK

          val headerCarrierCaptor: ArgumentCaptor[HeaderCarrier] = ArgumentCaptor.forClass(classOf[HeaderCarrier])

          verify(mockSessionRepository).set(eqTo(userAnswers))(headerCarrierCaptor.capture())

          val headerCarrier = headerCarrierCaptor.getValue
          headerCarrier.authorization.value.value mustBe bearerToken
          headerCarrier.sessionId.value.value mustBe sessionId
        }
      }

      "when answers unsuccessfully submitted to cache" - {
        "must return InternalServerError" in {
          when(mockSessionRepository.set(any())(any())).thenReturn(Future.successful(false))

          val userAnswers = emptyUserAnswers

          val request = FakeRequest(POST, testOnlyRoute)
            .withHeaders("Authorization" -> bearerToken)
            .withJsonBody(Json.toJson(userAnswers))

          val result = route(app, request).value

          status(result) mustEqual INTERNAL_SERVER_ERROR
        }
      }

      "when answers in invalid shape" - {
        "must return BadRequest" in {
          val request = FakeRequest(POST, testOnlyRoute)
            .withHeaders("Authorization" -> bearerToken)
            .withJsonBody(Json.obj("foo" -> "bar"))

          val result = route(app, request).value

          status(result) mustEqual BAD_REQUEST
        }
      }
    }
  }
}
