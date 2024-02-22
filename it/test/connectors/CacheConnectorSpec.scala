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

import com.github.tomakehurst.wiremock.client.WireMock._
import helpers.{ItSpecBase, WireMockServerHandler}
import models.{LockCheck, UserAnswers}
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsBoolean, Json}
import play.api.test.Helpers._

class CacheConnectorSpec extends ItSpecBase with WireMockServerHandler with ScalaCheckPropertyChecks {

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .configure(conf = "microservice.services.manage-transit-movements-departure-cache.port" -> server.port())

  private lazy val connector: CacheConnector = app.injector.instanceOf[CacheConnector]

  private val json: String =
    """
      |{
      |    "_id" : "2e8ede47-dbfb-44ea-a1e3-6c57b1fe6fe2",
      |    "lrn" : "1234567890",
      |    "eoriNumber" : "GB1234567",
      |    "isSubmitted" : "notSubmitted",
      |    "data" : {},
      |    "tasks" : {},
      |    "createdAt" : "2022-09-05T15:58:44.188Z",
      |    "lastUpdated" : "2022-09-07T10:33:23.472Z"
      |}
      |""".stripMargin

  private val userAnswers = Json.parse(json).as[UserAnswers]

  "CacheConnector" - {

    "get" - {

      val url = s"/manage-transit-movements-departure-cache/user-answers/${lrn.value}"

      "must return user answers when status is Ok" in {
        server.stubFor(
          get(urlEqualTo(url))
            .willReturn(okJson(json))
        )

        connector.get(lrn).futureValue mustBe Some(userAnswers)
      }

      "return None when no cached data found for provided LRN" in {
        server.stubFor(
          get(urlEqualTo(url))
            .willReturn(notFound())
        )

        val result: Option[UserAnswers] = await(connector.get(lrn))

        result mustBe None
      }
    }

    "post" - {

      val url = s"/manage-transit-movements-departure-cache/user-answers/${userAnswers.lrn}"

      "must return true when status is Ok" in {
        server.stubFor(post(urlEqualTo(url)) willReturn aResponse().withStatus(OK))

        val result: Boolean = await(connector.post(userAnswers))

        result mustBe true
      }

      "return false for 4xx or 5xx response" in {
        val status = Gen.choose(400: Int, 599: Int).sample.value

        server.stubFor(
          post(urlEqualTo(url))
            .willReturn(aResponse().withStatus(status))
        )

        val result: Boolean = await(connector.post(userAnswers))

        result mustBe false
      }
    }

    "put" - {

      val url = s"/manage-transit-movements-departure-cache/user-answers"

      "must return true when status is Ok" in {
        server.stubFor(
          put(urlEqualTo(url))
            .willReturn(aResponse().withStatus(OK))
        )

        val result: Boolean = await(connector.put(lrn))

        result mustBe true
      }

      "return false for 4xx or 5xx response" in {
        val status = Gen.choose(400: Int, 599: Int).sample.value

        server.stubFor(
          put(urlEqualTo(url))
            .willReturn(aResponse().withStatus(status))
        )

        val result: Boolean = await(connector.put(lrn))

        result mustBe false
      }
    }

    "checkLock" - {

      val url = s"/manage-transit-movements-departure-cache/user-answers/${userAnswers.lrn.toString}/lock"

      "must return Unlocked when status is Ok (200)" in {
        server.stubFor(get(urlEqualTo(url)) willReturn aResponse().withStatus(OK))

        val result: LockCheck = await(connector.checkLock(userAnswers))

        result mustBe LockCheck.Unlocked
      }

      "must return Locked when status is Locked (423)" in {
        server.stubFor(get(urlEqualTo(url)) willReturn aResponse().withStatus(LOCKED))

        val result: LockCheck = await(connector.checkLock(userAnswers))

        result mustBe LockCheck.Locked
      }

      "return LockCheckFailure for other 4xx/5xx responses" in {

        forAll(Gen.choose(400: Int, 599: Int).retryUntil(_ != LOCKED)) {
          errorStatus =>
            server.stubFor(get(urlEqualTo(url)) willReturn aResponse().withStatus(errorStatus))

            val result: LockCheck = await(connector.checkLock(userAnswers))

            result mustBe LockCheck.LockCheckFailure
        }
      }
    }

    "deleteLock" - {

      val url = s"/manage-transit-movements-departure-cache/user-answers/${userAnswers.lrn}/lock"

      "must return true when status is Ok" in {
        server.stubFor(delete(urlEqualTo(url)) willReturn aResponse().withStatus(OK))

        val result: Boolean = await(connector.deleteLock(userAnswers))

        result mustBe true
      }

      "return false for other responses" in {

        val errorResponses: Gen[Int] = Gen
          .chooseNum(400: Int, 599: Int)

        forAll(errorResponses) {
          error =>
            server.stubFor(
              delete(urlEqualTo(url))
                .willReturn(aResponse().withStatus(error))
            )

            val result: Boolean = await(connector.deleteLock(userAnswers))

            result mustBe false
        }
      }
    }

    "delete" - {

      val url = s"/manage-transit-movements-departure-cache/user-answers/${userAnswers.lrn}"

      "must return true when status is Ok" in {
        server.stubFor(delete(urlEqualTo(url)) willReturn aResponse().withStatus(OK))

        val result: Boolean = await(connector.delete(userAnswers.lrn))

        result mustBe true
      }

      "return false for other responses" in {

        val errorResponses: Gen[Int] = Gen
          .chooseNum(400: Int, 599: Int)

        forAll(errorResponses) {
          error =>
            server.stubFor(
              delete(urlEqualTo(url))
                .willReturn(aResponse().withStatus(error))
            )

            val result: Boolean = await(connector.delete(userAnswers.lrn))

            result mustBe false
        }
      }
    }

    "doesDraftOrSubmissionExistForLrn" - {
      val url = s"/manage-transit-movements-departure-cache/does-draft-or-submission-exist-for-lrn/${lrn.value}"

      "must return false when status is Ok and lrn does not exists in cache/API" in {
        server.stubFor(
          get(urlEqualTo(url))
            .willReturn(okJson(Json.stringify(JsBoolean(false))))
        )

        connector.doesDraftOrSubmissionExistForLrn(lrn).futureValue mustBe false
      }

      "must return true when status is Ok and lrn does exists in cache/API" in {
        server.stubFor(
          get(urlEqualTo(url))
            .willReturn(okJson(Json.stringify(JsBoolean(true))))
        )

        connector.doesDraftOrSubmissionExistForLrn(lrn).futureValue mustBe true
      }

      "return an exception for 4xx or 5xx response" in {
        val status = Gen.choose(400: Int, 599: Int).sample.value

        server.stubFor(
          get(urlEqualTo(url))
            .willReturn(aResponse().withStatus(status))
        )

        assertThrows[Exception] {
          await(connector.doesDraftOrSubmissionExistForLrn(lrn))
        }
      }
    }

    "doesIE028ExistForLrn" - {
      val url = s"/manage-transit-movements-departure-cache/does-ie028-exist-for-lrn/${lrn.value}"

      "must return false when status is Ok and lrn does not exists in API" in {
        server.stubFor(
          get(urlEqualTo(url))
            .willReturn(okJson(Json.stringify(JsBoolean(false))))
        )

        connector.doesIE028ExistForLrn(lrn).futureValue mustBe false
      }

      "must return true when status is Ok and lrn does exists in API" in {
        server.stubFor(
          get(urlEqualTo(url))
            .willReturn(okJson(Json.stringify(JsBoolean(true))))
        )

        connector.doesIE028ExistForLrn(lrn).futureValue mustBe true
      }

      "return an exception for 4xx or 5xx response" in {
        val status = Gen.choose(400: Int, 599: Int).sample.value

        server.stubFor(
          get(urlEqualTo(url))
            .willReturn(aResponse().withStatus(status))
        )

        assertThrows[Exception] {
          await(connector.doesIE028ExistForLrn(lrn))
        }
      }
    }

  }
}
