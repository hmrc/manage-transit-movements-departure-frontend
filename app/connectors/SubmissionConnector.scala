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

import config.FrontendAppConfig
import models.{DepartureMessages, LocalReferenceNumber}
import play.api.Logging
import play.api.http.HeaderNames._
import play.api.http.Status.NOT_FOUND
import play.api.libs.json.Json
import uk.gov.hmrc.http.HttpReads.Implicits._
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse, StringContextOps, UpstreamErrorResponse}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

sealed trait SubmissionConnector extends Logging {

  val config: FrontendAppConfig

  val http: HttpClientV2

  implicit val ec: ExecutionContext

  val acceptHeader: String

  private val baseUrl = s"${config.cacheUrl}"

  def post(lrn: String)(implicit hc: HeaderCarrier): Future[HttpResponse] = {
    val url = url"$baseUrl/declaration/submit"
    http
      .post(url)
      .setHeader(ACCEPT -> acceptHeader)
      .withBody(Json.toJson(lrn))
      .execute[HttpResponse]
  }

  def postAmendment(lrn: String)(implicit hc: HeaderCarrier): Future[HttpResponse] = {
    val url = url"$baseUrl/declaration/submit-amendment"
    http
      .post(url)
      .setHeader(ACCEPT -> acceptHeader)
      .withBody(Json.toJson(lrn))
      .execute[HttpResponse]
  }

  def getExpiryInDays(lrn: String)(implicit hc: HeaderCarrier): Future[Long] = {
    val url = url"$baseUrl/user-answers/expiry/$lrn"
    http
      .get(url)
      .execute[Long]
  }

  def getMessages(lrn: LocalReferenceNumber)(implicit hc: HeaderCarrier): Future[DepartureMessages] = {
    val url = url"$baseUrl/messages/$lrn"
    http
      .get(url)
      .execute[DepartureMessages]
      .recover {
        case e: UpstreamErrorResponse if e.statusCode == NOT_FOUND => DepartureMessages()
      }
  }
}

object SubmissionConnector {

  class TransitionSubmissionConnector @Inject() (
    override val config: FrontendAppConfig,
    override val http: HttpClientV2
  )(implicit override val ec: ExecutionContext)
      extends SubmissionConnector {

    override val acceptHeader: String = "application/vnd.hmrc.transition+json"
  }

  class PostTransitionSubmissionConnector @Inject() (
    override val config: FrontendAppConfig,
    override val http: HttpClientV2
  )(implicit override val ec: ExecutionContext)
      extends SubmissionConnector {

    override val acceptHeader: String = "application/vnd.hmrc.final+json"
  }
}
