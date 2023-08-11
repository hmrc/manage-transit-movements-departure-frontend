/*
 * Copyright 2023 HM Revenue & Customs
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

package connectors

import base.{AppWithDefaultMockFixtures, SpecBase}
import com.github.tomakehurst.wiremock.client.WireMock._
import helper.WireMockServerHandler
import models.SubmissionState
import org.scalacheck.Gen
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsNumber, JsString}
import play.api.test.Helpers._
import uk.gov.hmrc.http.HttpResponse

class SubmissionConnectorSpec extends SpecBase with AppWithDefaultMockFixtures with WireMockServerHandler {

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .configure(conf = "microservice.services.manage-transit-movements-departure-cache.port" -> server.port())

  private lazy val connector: SubmissionConnector = app.injector.instanceOf[SubmissionConnector]

  "SubmissionConnector" - {

    "post" - {

      val url = s"/manage-transit-movements-departure-cache/declaration/submit"

      "must return true when status is Ok" in {
        server.stubFor(
          post(urlEqualTo(url))
            .willReturn(aResponse().withStatus(OK))
        )

        val result: HttpResponse = await(connector.post(lrn.value))

        result.status mustBe OK
      }

      "return false for 4xx response" in {
        val status = Gen.choose(400: Int, 499: Int).sample.value

        server.stubFor(
          post(urlEqualTo(url))
            .willReturn(aResponse().withStatus(status))
        )

        val result: HttpResponse = await(connector.post(lrn.value))

        result.status mustBe status
      }

      "return false for 5xx response" in {
        val status = Gen.choose(500: Int, 599: Int).sample.value

        server.stubFor(
          post(urlEqualTo(url))
            .willReturn(aResponse().withStatus(status))
        )

        val result: HttpResponse = await(connector.post(lrn.value))

        result.status mustBe status
      }
    }

    "getSubmissionStatus" - {

      val url = s"/manage-transit-movements-departure-cache/submission-status/$lrn"

      "must return submission status when status is Ok" in {
        server.stubFor(
          get(urlEqualTo(url))
            .willReturn(okJson(JsString("submitted").toString()))
        )

        val result: SubmissionState = await(connector.getSubmissionStatus(lrn.value))

        result mustBe SubmissionState.Submitted
      }
    }

    "getExpiryInDays" - {

      val url = s"/manage-transit-movements-departure-cache/user-answers/expiry/$lrn"

      "must return expiry in days when status is Ok" in {
        server.stubFor(
          get(urlEqualTo(url))
            .willReturn(okJson(JsNumber(30).toString()))
        )

        val result: Long = await(connector.getExpiryInDays(lrn.value))

        result mustBe 30
      }
    }
  }

}
