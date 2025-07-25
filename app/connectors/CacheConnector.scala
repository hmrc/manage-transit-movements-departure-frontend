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

package connectors

import config.FrontendAppConfig
import models.LockCheck.*
import models.{DepartureMessages, LocalReferenceNumber, LockCheck, UserAnswers, UserAnswersResponse}
import play.api.Logging
import play.api.http.Status.*
import play.api.libs.json.Json
import play.api.libs.ws.JsonBodyWritables.*
import uk.gov.hmrc.http.HttpReads.Implicits.*
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse, StringContextOps}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class CacheConnector @Inject() (
  config: FrontendAppConfig,
  http: HttpClientV2
)(implicit ec: ExecutionContext)
    extends Logging {

  private val baseUrl = s"${config.cacheUrl}"

  def get(lrn: LocalReferenceNumber)(implicit hc: HeaderCarrier): Future[UserAnswersResponse] = {
    val url = url"$baseUrl/user-answers/$lrn"
    http
      .get(url)
      .execute[UserAnswersResponse]
  }

  def post(userAnswers: UserAnswers)(implicit hc: HeaderCarrier): Future[Boolean] = {
    val url = url"$baseUrl/user-answers/${userAnswers.lrn}"
    http
      .post(url)
      .withBody(Json.toJson(userAnswers))
      .execute[HttpResponse]
      .map(_.status == OK)
  }

  def checkLock(userAnswers: UserAnswers)(implicit hc: HeaderCarrier): Future[LockCheck] = {
    val url = url"$baseUrl/user-answers/${userAnswers.lrn}/lock"
    http
      .get(url)
      .execute[HttpResponse]
      .map {
        _.status match {
          case OK     => Unlocked
          case LOCKED => Locked
          case _      => LockCheckFailure
        }
      }
  }

  def deleteLock(userAnswers: UserAnswers)(implicit hc: HeaderCarrier): Future[Boolean] = {
    val url = url"$baseUrl/user-answers/${userAnswers.lrn}/lock"
    http
      .delete(url)
      .execute[HttpResponse]
      .map(_.status == OK)
  }

  def put(lrn: LocalReferenceNumber)(implicit hc: HeaderCarrier): Future[Boolean] = {
    val url = url"$baseUrl/user-answers"
    http
      .put(url)
      .withBody(Json.toJson(lrn.toString))
      .execute[HttpResponse]
      .map(_.status == OK)
  }

  def delete(lrn: LocalReferenceNumber)(implicit hc: HeaderCarrier): Future[Boolean] = {
    val url = url"$baseUrl/user-answers/$lrn"
    http
      .delete(url)
      .execute[HttpResponse]
      .map(_.status == OK)
  }

  def submit(lrn: String)(implicit hc: HeaderCarrier): Future[HttpResponse] = {
    val url = url"$baseUrl/declaration/submit"
    http
      .post(url)
      .setHeader("API-Version" -> config.apiVersion)
      .withBody(Json.toJson(lrn))
      .execute[HttpResponse]
  }

  def submitAmendment(lrn: String)(implicit hc: HeaderCarrier): Future[HttpResponse] = {
    val url = url"$baseUrl/declaration/submit-amendment"
    http
      .post(url)
      .setHeader("API-Version" -> config.apiVersion)
      .withBody(Json.toJson(lrn))
      .execute[HttpResponse]
  }

  def getExpiryInDays(lrn: String)(implicit hc: HeaderCarrier): Future[Long] = {
    val url = url"$baseUrl/user-answers/$lrn/expiry"
    http
      .get(url)
      .execute[Long]
  }

  def getMessages(lrn: LocalReferenceNumber)(implicit hc: HeaderCarrier): Future[DepartureMessages] = {
    import models.DepartureMessages.httpReads
    val url = url"$baseUrl/messages/$lrn"
    http
      .get(url)
      .setHeader("API-Version" -> config.apiVersion)
      .execute[DepartureMessages]
  }

  def copy(oldLrn: LocalReferenceNumber, newLrn: LocalReferenceNumber)(implicit hc: HeaderCarrier): Future[Boolean] = {
    val url = url"$baseUrl/user-answers/$oldLrn/copy"
    http
      .post(url)
      .withBody(Json.toJson(newLrn))
      .execute[HttpResponse]
      .map(_.status == OK)
  }
}
