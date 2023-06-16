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
import models.reference.{Country, CustomsOffice}
import play.api.Logging
import play.api.http.Status.{NOT_FOUND, OK}
import play.api.libs.json.Format.GenericFormat
import play.api.libs.json.Reads
import sttp.model.HeaderNames
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpReads, HttpResponse}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ReferenceDataConnector @Inject() (config: FrontendAppConfig, http: HttpClient) extends Logging {

  def getCustomsOfficesOfDepartureForCountry(
    countryCode: String
  )(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[Seq[CustomsOffice]] = {

    val queryParams: Seq[(String, String)] = Seq(
      "data.countryId"  -> countryCode,
      "data.roles.role" -> "DEP"
    )

    val serviceUrl = s"${config.customsReferenceDataUrl}/filtered-lists/CustomsOffices"

    http.GET[Seq[CustomsOffice]](serviceUrl, headers = version2Header, queryParams = queryParams)
  }

  def getCountryCodesCTC()(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[Seq[Country]] = {
    val serviceUrl = s"${config.customsReferenceDataUrl}/lists/CountryCodesCommonTransit"
    http.GET[Seq[Country]](serviceUrl, headers = version2Header)
  }

  def getCustomsSecurityAgreementAreaCountries()(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[Seq[Country]] = {
    val serviceUrl = s"${config.customsReferenceDataUrl}/lists/CountryCustomsSecurityAgreementArea"
    http.GET[Seq[Country]](serviceUrl, headers = version2Header)
  }

  private def version2Header: Seq[(String, String)] = Seq(
    HeaderNames.Accept -> "application/vnd.hmrc.2.0+json"
  )

  // TODO - May need readers like this for other reference data too given different response format?
  // TODO Q. Can we use generics so this method could fit all ref data calls?
//  implicit val responseHandlerCustomsOfficeList: HttpReads[Seq[CustomsOffice]] =
//    (_: String, _: String, response: HttpResponse) => {
//      response.status match {
//        case OK =>
//          val cols = (response.json \ "data").get
//          cols.as[Seq[CustomsOffice]]
//        case NOT_FOUND => // TODO - why do we allow an empty COL but not other reference data?
//          Nil
//        case other =>
//          logger.info(s"[ReferenceDataConnector][getCustomsOfficesOfDepartureForCountry] Invalid downstream status $other")
//          throw new IllegalStateException(s"Invalid Downstream Status $other")
//      }
//    }

  implicit def responseHandlerGeneric[A](implicit reads: Reads[A]): HttpReads[Seq[A]] =
    (_: String, _: String, response: HttpResponse) => {
      response.status match {
        case OK =>
          val referenceData = (response.json \ "data").getOrElse(
            throw new IllegalArgumentException("[ReferenceDataConnector][responseHandlerGeneric] Reference data could not be found")
          )

          referenceData.as[Seq[A]]
        case NOT_FOUND => // TODO Q. - why do we allow an empty COL but not other reference data?
          Nil
        case other =>
          logger.info(s"[ReferenceDataConnector][responseHandlerGeneric] Invalid downstream status $other")
          throw new IllegalStateException(s"[ReferenceDataConnector][responseHandlerGeneric] Invalid Downstream Status $other")
      }
    }
}
