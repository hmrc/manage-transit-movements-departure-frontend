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
import models._
import models.reference._
import play.api.Logging
import play.api.http.Status.{NOT_FOUND, OK}
import play.api.mvc.RequestHeader
import uk.gov.hmrc.http.HttpReads.Implicits._
import uk.gov.hmrc.http.{HeaderCarrier, HeaderNames, HttpClient, HttpReads, HttpResponse}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ReferenceDataConnector @Inject() (config: FrontendAppConfig, http: HttpClient) extends Logging {

  implicit val responseHandlerCustomsOfficeList: HttpReads[CustomsOfficeList] =
    (_: String, _: String, response: HttpResponse) =>
      response.status match {
        case OK =>
          CustomsOfficeList(
            response.json
              .as[Seq[CustomsOffice]]
          )
        case NOT_FOUND =>
          CustomsOfficeList(Nil)
        case other =>
          logger.info(s"[ReferenceDataConnector][getCustomsOfficesForCountry] Invalid downstream status $other")
          throw new IllegalStateException(s"Invalid Downstream Status $other")
      }

  private def roleQueryParams(roles: Seq[String]): Seq[(String, String)] = roles.map("role" -> _)

  def getCustomsOffices(roles: Seq[String] = Nil)(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[Seq[CustomsOffice]] = {
    val serviceUrl = s"${config.referenceDataUrl}/customs-offices"
    http.GET[Seq[CustomsOffice]](serviceUrl, roleQueryParams(roles))
  }

  def getCustomsOfficesForCountry(
    countryCode: CountryCode,
    roles: Seq[String] = Nil
  )(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[CustomsOfficeList] = {
    val serviceUrl = s"${config.referenceDataUrl}/customs-offices/${countryCode.code}"
    http.GET[CustomsOfficeList](serviceUrl, roleQueryParams(roles))
  }

  def getCustomsOffice(id: String)(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[CustomsOffice] = {
    val serviceUrl = s"${config.referenceDataUrl}/customs-office/$id"
    http.GET[CustomsOffice](serviceUrl)
  }

  def getCountries(queryParameters: Seq[(String, String)])(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[Seq[Country]] = {
    val serviceUrl = s"${config.referenceDataUrl}/countries"
    http.GET[Seq[Country]](serviceUrl, queryParameters, headers = setHeaders)
  }

  private def setHeaders = Seq(
    "Accept" -> "application/vnd.hmrc.2.0+json"
  )

}
