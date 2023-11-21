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
import connectors.ReferenceDataConnector.NoReferenceDataFoundException
import models.reference._
import play.api.Logging
import play.api.http.Status.OK
import play.api.libs.json.Format.GenericFormat
import play.api.libs.json.{JsError, JsResultException, JsSuccess, Reads}
import sttp.model.HeaderNames
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpReads, HttpResponse}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ReferenceDataConnector @Inject() (config: FrontendAppConfig, http: HttpClient) extends Logging {

  def getCountries()(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[Seq[Country]] = {
    val url = s"${config.customsReferenceDataUrl}/lists/CountryCodesCommunity"
    http.GET[Seq[Country]](url, headers = version2Header)
  }

  def getCustomsOfficesOfDepartureForCountry(
    countryCode: String
  )(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[Seq[CustomsOffice]] = {

    // TODO - do we want to specify this in config?
    val queryParams: Seq[(String, String)] = Seq(
      "data.countryId"  -> countryCode,
      "data.roles.role" -> "DEP"
    )

    val url = s"${config.customsReferenceDataUrl}/filtered-lists/CustomsOffices"

    http.GET[Seq[CustomsOffice]](url, headers = version2Header, queryParams = queryParams)
  }

  def getCountryCodesCTC()(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[Seq[Country]] = {
    val url = s"${config.customsReferenceDataUrl}/lists/CountryCodesCTC"
    http.GET[Seq[Country]](url, headers = version2Header)
  }

  def getCustomsSecurityAgreementAreaCountries()(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[Seq[Country]] = {
    val url = s"${config.customsReferenceDataUrl}/lists/CountryCustomsSecurityAgreementArea"
    http.GET[Seq[Country]](url, headers = version2Header)
  }

  def getSecurityTypes()(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[Seq[SecurityType]] = {
    val url = s"${config.customsReferenceDataUrl}/lists/DeclarationTypeSecurity"
    http.GET[Seq[SecurityType]](url, headers = version2Header)
  }

  def getDeclarationTypes()(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[Seq[DeclarationType]] = {
    val url = s"${config.customsReferenceDataUrl}/lists/DeclarationType"
    http.GET[Seq[DeclarationType]](url, headers = version2Header)
  }

  def getAdditionalDeclarationTypes()(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[Seq[AdditionalDeclarationType]] = {
    val url = s"${config.customsReferenceDataUrl}/lists/DeclarationTypeAdditional"
    http.GET[Seq[AdditionalDeclarationType]](url, headers = version2Header)
  }

  private def version2Header: Seq[(String, String)] = Seq(
    HeaderNames.Accept -> "application/vnd.hmrc.2.0+json"
  )

  implicit def responseHandlerGeneric[A](implicit reads: Reads[A]): HttpReads[Seq[A]] =
    (_: String, _: String, response: HttpResponse) => {
      response.status match {
        case OK =>
          (response.json \ "data").validate[Seq[A]] match {
            case JsSuccess(Nil, _) =>
              throw new NoReferenceDataFoundException
            case JsSuccess(value, _) =>
              value
            case JsError(errors) =>
              throw JsResultException(errors)
          }
        case e =>
          logger.warn(s"[ReferenceDataConnector][responseHandlerGeneric] Reference data call returned $e")
          throw new Exception(s"[ReferenceDataConnector][responseHandlerGeneric] $e - ${response.body}")
      }
    }
}

object ReferenceDataConnector {

  class NoReferenceDataFoundException extends Exception("The reference data call was successful but the response body is empty.")
}
