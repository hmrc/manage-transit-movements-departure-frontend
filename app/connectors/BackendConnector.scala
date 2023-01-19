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
import models.{LocalReferenceNumber, UserAnswers}
import play.api.Logging
import play.api.http.Status._
import uk.gov.hmrc.http.HttpReads.Implicits._
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpResponse, UpstreamErrorResponse}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class BackendConnector @Inject() (
  config: FrontendAppConfig,
  http: HttpClient
)(implicit ec: ExecutionContext)
    extends Logging {

  private val baseUrl = s"${config.cacheUrl}"

  def submit(lrn: LocalReferenceNumber)(implicit hc: HeaderCarrier): Future[HttpResponse] = {
    val url = s"$baseUrl/submit-declaration/$lrn"
    http.GET[HttpResponse](url)
  }
}
