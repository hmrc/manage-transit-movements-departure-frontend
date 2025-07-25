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
import connectors.testOnly.TestOnlyCacheConnector
import generators.Generators
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.{any, eq as eqTo}
import org.mockito.Mockito.{verify, when}
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.Future

class TestOnlyControllerSpec extends SpecBase with AppWithDefaultMockFixtures with Generators {

  private val mockConnector: TestOnlyCacheConnector = mock[TestOnlyCacheConnector]

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .configure("play.http.router" -> "testOnlyDoNotUseInAppConf.Routes")
      .bindings(bind[TestOnlyCacheConnector].toInstance(mockConnector))

  "TestOnlyController" - {

    "setUserAnswers" - {

      val bearerToken        = nonEmptyString.sample.value
      val sessionId          = nonEmptyString.sample.value
      lazy val testOnlyRoute = routes.TestOnlyController.setUserAnswers(sessionId).url

      val lrn = nonEmptyString.sample.value

      "when answers successfully submitted to cache" - {
        "must return Ok" in {
          val json = Json.parse(s"""
                 |{
                 |  "lrn" : "$lrn",
                 |  "eoriNumber" : "eori123",
                 |  "isSubmitted" : "notSubmitted",
                 |  "tasks" : {}
                 |}
                 |""".stripMargin)

          when(mockConnector.put(any())(any())).thenReturn(Future.successful(true))
          when(mockConnector.post(any(), any())(any())).thenReturn(Future.successful(true))

          val request = FakeRequest(POST, testOnlyRoute)
            .withHeaders("Authorization" -> bearerToken)
            .withJsonBody(json)

          val result = route(app, request).value

          status(result) mustEqual OK

          val headerCarrierCaptor: ArgumentCaptor[HeaderCarrier] = ArgumentCaptor.forClass(classOf[HeaderCarrier])

          verify(mockConnector).put(eqTo(lrn))(headerCarrierCaptor.capture())
          val headerCarrier = headerCarrierCaptor.getValue
          verify(mockConnector).post(eqTo(lrn), eqTo(json))(eqTo(headerCarrier))

          headerCarrier.authorization.value.value mustEqual bearerToken
          headerCarrier.sessionId.value.value mustEqual sessionId

        }
      }

      "when PUT fails" - {
        "must return InternalServerError" in {
          val json = Json.parse(s"""
               |{
               |  "lrn" : "$lrn",
               |  "eoriNumber" : "eori123",
               |  "isSubmitted" : "notSubmitted",
               |  "tasks" : {}
               |}
               |""".stripMargin)

          when(mockConnector.put(any())(any())).thenReturn(Future.successful(false))

          val request = FakeRequest(POST, testOnlyRoute)
            .withHeaders("Authorization" -> bearerToken)
            .withJsonBody(json)

          val result = route(app, request).value

          status(result) mustEqual INTERNAL_SERVER_ERROR
        }
      }

      "when POST fails" - {
        "must return InternalServerError" in {
          val json = Json.parse(s"""
               |{
               |  "lrn" : "$lrn",
               |  "eoriNumber" : "eori123",
               |  "isSubmitted" : "notSubmitted",
               |  "tasks" : {}
               |}
               |""".stripMargin)

          when(mockConnector.put(any())(any())).thenReturn(Future.successful(false))
          when(mockConnector.post(any(), any())(any())).thenReturn(Future.successful(false))

          val request = FakeRequest(POST, testOnlyRoute)
            .withHeaders("Authorization" -> bearerToken)
            .withJsonBody(json)

          val result = route(app, request).value

          status(result) mustEqual INTERNAL_SERVER_ERROR
        }
      }

      "when json is missing an LRN" - {
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
