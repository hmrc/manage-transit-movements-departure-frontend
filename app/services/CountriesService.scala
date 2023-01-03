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

package services

import connectors.ReferenceDataConnector
import models.reference.{Country, CountryCode}
import models.{CountryList, DeclarationType, UserAnswers}
import pages.preTaskList.DeclarationTypePage
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class CountriesService @Inject() (referenceDataConnector: ReferenceDataConnector)(implicit ec: ExecutionContext) {

  def getDestinationCountries(userAnswers: UserAnswers)(implicit hc: HeaderCarrier): Future[CountryList] =
    userAnswers.get(DeclarationTypePage) match {
      case Some(DeclarationType.Option4) => getCommunityCountries()
      case _                             => getTransitCountries()
    }

  def getCountries()(implicit hc: HeaderCarrier): Future[CountryList] =
    getCountries(Nil)

  def getTransitCountries()(implicit hc: HeaderCarrier): Future[CountryList] = {
    val queryParameters = Seq("membership" -> "ctc")
    getCountries(queryParameters)
  }

  def getNonEuTransitCountries()(implicit hc: HeaderCarrier): Future[CountryList] = {
    val queryParameters = Seq("membership" -> "non_eu")
    getCountries(queryParameters)
  }

  def getAddressPostcodeBasedCountries()(implicit hc: HeaderCarrier): Future[CountryList] =
    referenceDataConnector
      .getAddressPostcodeBasedCountries()
      .map(sort)

  def getCommunityCountries()(implicit hc: HeaderCarrier): Future[CountryList] = {
    val queryParameters = Seq("membership" -> "eu")
    getCountries(queryParameters)
  }

  def getCustomsSecurityAgreementAreaCountries()(implicit hc: HeaderCarrier): Future[CountryList] =
    referenceDataConnector
      .getCustomsSecurityAgreementAreaCountries()
      .map(sort)

  def getCountryCodesCTC()(implicit hc: HeaderCarrier): Future[CountryList] =
    referenceDataConnector
      .getCountryCodesCTC()
      .map(sort)

  def getCountriesWithoutZip()(implicit hc: HeaderCarrier): Future[Seq[CountryCode]] =
    referenceDataConnector
      .getCountriesWithoutZip()

  private def getCountries(queryParameters: Seq[(String, String)])(implicit hc: HeaderCarrier): Future[CountryList] =
    referenceDataConnector
      .getCountries(queryParameters)
      .map(sort)

  def doesCountryRequireZip(country: Country)(implicit hc: HeaderCarrier): Future[Boolean] =
    getCountriesWithoutZip().map(!_.contains(country.code))

  private def sort(countries: Seq[Country]): CountryList =
    CountryList(countries.sortBy(_.description.toLowerCase))
}
