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

import cats.Order
import cats.data.NonEmptySet
import config.FrontendAppConfig
import connectors.ReferenceDataConnector.*
import models.reference.*
import play.api.Logging
import play.api.http.Status.OK
import play.api.libs.json.{JsError, JsResultException, JsSuccess, Reads}
import sttp.model.HeaderNames
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpReads, HttpResponse, StringContextOps}

import java.net.URL
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ReferenceDataConnector @Inject() (config: FrontendAppConfig, http: HttpClientV2) extends Logging {

  private def get[T](url: URL)(implicit ec: ExecutionContext, hc: HeaderCarrier, reads: HttpReads[Responses[T]]): Future[Responses[T]] =
    http
      .get(url)
      .setHeader(HeaderNames.Accept -> {
        val version = if (config.phase6Enabled) "2.0" else "1.0"
        s"application/vnd.hmrc.$version+json"
      })
      .execute[Responses[T]]

  private def getOne[T](url: URL)(implicit ec: ExecutionContext, hc: HeaderCarrier, reads: HttpReads[Responses[T]]): Future[Response[T]] =
    get[T](url).map(_.map(_.head))

  def getCountryCodeCommunityCountry(countryId: String)(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[Response[Country]] = {
    val queryParameters                = Country.queryParameters(countryId)(config)
    val url                            = url"${config.customsReferenceDataUrl}/lists/CountryCodesCommunity?$queryParameters"
    implicit val reads: Reads[Country] = Country.reads(config)
    getOne[Country](url)
  }

  def getCustomsOfficesOfDepartureForCountry(
    countryCodes: String*
  )(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[Responses[CustomsOffice]] = {
    val queryParameters = countryCodes.map("data.countryId" -> _) :+ ("data.roles.role" -> "DEP")
    val url             = url"${config.customsReferenceDataUrl}/lists/CustomsOffices?$queryParameters"
    get[CustomsOffice](url)
  }

  def getCountryCodesCTCCountry(countryId: String)(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[Response[Country]] = {
    val queryParameters                = Country.queryParameters(countryId)(config)
    val url                            = url"${config.customsReferenceDataUrl}/lists/CountryCodesCTC?$queryParameters"
    implicit val reads: Reads[Country] = Country.reads(config)
    getOne[Country](url)
  }

  def getCountryCustomsSecurityAgreementAreaCountry(countryId: String)(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[Response[Country]] = {
    val queryParameters                = Country.queryParameters(countryId)(config)
    val url                            = url"${config.customsReferenceDataUrl}/lists/CountryCustomsSecurityAgreementArea?$queryParameters"
    implicit val reads: Reads[Country] = Country.reads(config)
    getOne[Country](url)
  }

  def getSecurityTypes()(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[Responses[SecurityType]] = {
    val url                                 = url"${config.customsReferenceDataUrl}/lists/DeclarationTypeSecurity"
    implicit val reads: Reads[SecurityType] = SecurityType.reads(config)
    get[SecurityType](url)
  }

  def getDeclarationTypes()(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[Responses[DeclarationType]] = {
    val url                                    = url"${config.customsReferenceDataUrl}/lists/DeclarationType"
    implicit val reads: Reads[DeclarationType] = DeclarationType.reads(config)
    get[DeclarationType](url)
  }

  def getAdditionalDeclarationTypes()(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[Responses[AdditionalDeclarationType]] = {
    val url                                              = url"${config.customsReferenceDataUrl}/lists/DeclarationTypeAdditional"
    implicit val reads: Reads[AdditionalDeclarationType] = AdditionalDeclarationType.reads(config)
    get[AdditionalDeclarationType](url)
  }

  implicit def responseHandlerGeneric[A](implicit reads: Reads[List[A]], order: Order[A]): HttpReads[Responses[A]] =
    (_: String, url: String, response: HttpResponse) =>
      response.status match {
        case OK =>
          val json = if (config.phase6Enabled) response.json else response.json \ "data"
          json.validate[List[A]] match {
            case JsSuccess(Nil, _) =>
              Left(NoReferenceDataFoundException(url))
            case JsSuccess(head :: tail, _) =>
              Right(NonEmptySet.of(head, tail*))
            case JsError(errors) =>
              Left(JsResultException(errors))
          }
        case e =>
          logger.warn(s"[ReferenceDataConnector][responseHandlerGeneric] Reference data call returned $e")
          Left(Exception(s"[ReferenceDataConnector][responseHandlerGeneric] $e - ${response.body}"))
      }
}

object ReferenceDataConnector {

  type Responses[T] = Either[Exception, NonEmptySet[T]]
  type Response[T]  = Either[Exception, T]

  class NoReferenceDataFoundException(url: String) extends Exception(s"The reference data call was successful but the response body is empty: $url")
}
