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

package connectors

import config.FrontendAppConfig
import play.api.Logging
import play.api.http.{HeaderNames, Status}
import play.api.libs.json.{Json, Writes}
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpErrorFunctions, HttpReads, HttpResponse}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

// TODO - what does the request look like? Simplest possible
case class DeclarationRequest(eori: String)

object DeclarationRequest {
  implicit val declarationRequestFormat = Json.format[DeclarationRequest]
}

// TODO -
case class DeclarationResponse(status: Int)

object DeclarationResponse {
  implicit val declarationResponseFormat = Json.format[DeclarationResponse]
}

class ApiConnector @Inject() (httpClient: HttpClient, appConfig: FrontendAppConfig)(implicit ec: ExecutionContext) extends HttpErrorFunctions with Logging {

  private val requestHeaders = Seq(
    HeaderNames.ACCEPT -> "application/vnd.hmrc.2.0+json"
  )

  def submitDeclaration(request: DeclarationRequest)(implicit hc: HeaderCarrier): Future[Either[HttpResponse, DeclarationResponse]] = {

    val declarationUrl = s"${appConfig.apiUrl}/customs/transits/movements/departures"

    implicit val declarationReads: HttpReads[Either[HttpResponse, DeclarationResponse]] =
      HttpReads[HttpResponse].map {
        response =>
          response.status match {
            case Status.ACCEPTED    => Right(DeclarationResponse(Status.ACCEPTED)) // TODO - what might we want to return if success?
            case Status.OK          => Right(DeclarationResponse(Status.ACCEPTED))
            case Status.BAD_REQUEST => Left(response) // TODO - Could do something differently here?
            case _                  => Left(response)
          }
      }

    httpClient.PUT[DeclarationRequest, Either[HttpResponse, DeclarationResponse]](
      url = declarationUrl,
      headers = requestHeaders,
      body = request
    )
  }

}
