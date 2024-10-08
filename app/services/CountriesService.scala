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

package services

import connectors.ReferenceDataConnector
import connectors.ReferenceDataConnector.NoReferenceDataFoundException
import models.reference._
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class CountriesService @Inject() (referenceDataConnector: ReferenceDataConnector)(implicit ec: ExecutionContext) {

  def isInCL112(customsOffice: CustomsOffice)(implicit hc: HeaderCarrier): Future[Boolean] =
    referenceDataConnector
      .getCountryCodesCTCCountry(customsOffice.countryId)
      .map {
        _ => true
      }
      .recover {
        case _: NoReferenceDataFoundException => false
      }

  def isInCL147(customsOffice: CustomsOffice)(implicit hc: HeaderCarrier): Future[Boolean] =
    referenceDataConnector
      .getCountryCustomsSecurityAgreementAreaCountry(customsOffice.countryId)
      .map {
        _ => true
      }
      .recover {
        case _: NoReferenceDataFoundException => false
      }

  def isInCL010(customsOffice: CustomsOffice)(implicit hc: HeaderCarrier): Future[Boolean] =
    referenceDataConnector
      .getCountryCodeCommunityCountry(customsOffice.countryId)
      .map {
        _ => true
      }
      .recover {
        case _: NoReferenceDataFoundException => false
      }

}
