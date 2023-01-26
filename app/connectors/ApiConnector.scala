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

import api.submission.Header.scope
import api.submission._
import config.FrontendAppConfig
import generated._
import models.UserAnswers
import models.domain.UserAnswersReader
import models.journeyDomain.{DepartureDomain, ReaderError}
import play.api.Logging
import play.api.http.HeaderNames
import scalaxb.`package`.toXML
import uk.gov.hmrc.http.{BadRequestException, HeaderCarrier, HttpClient, HttpErrorFunctions, HttpResponse}
import models.journeyDomain.DepartureDomain.userAnswersReader
import pages.traderDetails.consignment.ApprovedOperatorPage
import scalaxb.DataRecord
import services.CountriesService

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ApiConnector @Inject() (httpClient: HttpClient, appConfig: FrontendAppConfig, countriesService: CountriesService)(implicit ec: ExecutionContext)
    extends HttpErrorFunctions
    with Logging {

  private val requestHeaders = Seq(
    HeaderNames.ACCEPT       -> "application/vnd.hmrc.2.0+json",
    HeaderNames.CONTENT_TYPE -> "application/xml"
  )

  def createPayload(userAnswers: UserAnswers)(implicit hc: HeaderCarrier): Future[Either[ReaderError, CC015CType]] =
    for {
      countries <- countriesService.getCountryCodesCTC()
      security  <- countriesService.getCustomsSecurityAgreementAreaCountries()
      domain                  = UserAnswersReader[DepartureDomain](userAnswersReader(countries.countryCodes, security.countryCodes)).run(userAnswers)
      reducedDatasetIndicator = userAnswers.get(ApprovedOperatorPage).getOrElse(false) // TODO - can we get this from the domain models?
    } yield domain.map {
      case DepartureDomain(preTaskList, traderDetails, routeDetails, guaranteeDetails, transportDetails) =>
        CC015CType(
          messagE_FROM_TRADERSequence1 = Header.message,
          messageType = Header.messageType,
          correlatioN_IDENTIFIERSequence3 = Header.correlationIdentifier,
          TransitOperation = TransitOperation.transform(userAnswers.lrn.value, preTaskList, reducedDatasetIndicator, routeDetails.routing, transportDetails),
          Authorisation = Authorisations.transform(
            transportDetails.authorisationsAndLimit.map(
              x => x.authorisationsDomain
            )
          ),
          CustomsOfficeOfDeparture = CustomsOffices.transformOfficeOfDeparture(preTaskList.officeOfDeparture),
          CustomsOfficeOfDestinationDeclared = CustomsOffices.transformOfficeOfDestination(routeDetails.routing.officeOfDestination),
          CustomsOfficeOfTransitDeclared = CustomsOffices.transformOfficeOfTransit(routeDetails.transit),
          CustomsOfficeOfExitForTransitDeclared = CustomsOffices.transformOfficeOfExit(routeDetails.exit),
          HolderOfTheTransitProcedure = HolderOfTheTransitProcedure.transform(traderDetails),
          Representative = Representative.transform(traderDetails),
          Guarantee = Guarantee.transform(guaranteeDetails),
          Consignment = Consignment.transform(transportDetails, traderDetails, routeDetails),
          attributes = Map("@PhaseID" -> DataRecord(PhaseIDtype.fromString("NCTS5.0", scope)))
        )
    }

  def submitDeclaration(userAnswers: UserAnswers)(implicit hc: HeaderCarrier): Future[HttpResponse] = {

    val declarationUrl = s"${appConfig.apiUrl}/movements/departures"

    createPayload(userAnswers) flatMap {
      case Left(msg) => throw new BadRequestException(s"${msg.page.toString} at path ${msg.page.path}: ${msg.message.getOrElse("Something went wrong")}")
      case Right(submissionModel) =>
        val payload: String = toXML[CC015CType](submissionModel, "ncts:CC015C", scope).toString
        httpClient.POSTString(declarationUrl, payload, requestHeaders)
    }

  }

}
