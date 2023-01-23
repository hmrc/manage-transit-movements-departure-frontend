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

import api.Conversions
import api.Conversions.scope
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

  // TODO - Build out submission model from domain and replace createSubmission
  def createPayload(userAnswers: UserAnswers)(implicit hc: HeaderCarrier): Future[Either[ReaderError, CC015CType]] =
    for {
      countries <- countriesService.getCountryCodesCTC()
      security  <- countriesService.getCustomsSecurityAgreementAreaCountries()
      domain                  = UserAnswersReader[DepartureDomain](userAnswersReader(countries.countryCodes, security.countryCodes)).run(userAnswers)
      reducedDatasetIndicator = userAnswers.get(ApprovedOperatorPage).getOrElse(false) // TODO - can we get this from the domain models?
    } yield domain.map {
      case DepartureDomain(preTaskList, traderDetails, routeDetails, guaranteeDetails, transportDetails) =>
        val message: MESSAGE_FROM_TRADERSequence = Conversions.message
        val messageType: MessageType015          = Conversions.messageType
        val correlationIdentifier                = Conversions.correlationIdentifier

        val transitOperation: TransitOperationType06 = Conversions.transitOperation(
          userAnswers.lrn.value,
          preTaskList,
          reducedDatasetIndicator,
          routeDetails.routing
        )

        val authorisations = Conversions.authorisations(
          transportDetails.authorisationsAndLimit.map(
            x => x.authorisationsDomain
          )
        )

        val customsOfficeOfDeparture: CustomsOfficeOfDepartureType03 =
          Conversions.customsOfficeOfDeparture(preTaskList.officeOfDeparture)

        val customsOfficeOfDestinationDeclared: CustomsOfficeOfDestinationDeclaredType01 =
          Conversions.customsOfficeOfDestination(routeDetails.routing.officeOfDestination)

        val customsOfficeOfTransitDeclared: Seq[CustomsOfficeOfTransitDeclaredType03] =
          Conversions.customsOfficeOfTransit(routeDetails.transit)

        val customsOfficeOfExitForTransitDeclared: Seq[CustomsOfficeOfExitForTransitDeclaredType02] =
          Conversions.customsOfficeOfExit(routeDetails.exit)

        val holderOfTheTransitProcedure: HolderOfTheTransitProcedureType14 = Conversions.holderOfTheTransitProcedureType(traderDetails)

        val representative: Option[RepresentativeType05] = Conversions.representative(traderDetails)

        val guarantee: Seq[GuaranteeType02] = Conversions.guaranteeType(guaranteeDetails)

        CC015CType(
          messagE_FROM_TRADERSequence1 = message,
          messageType = messageType,
          correlatioN_IDENTIFIERSequence3 = correlationIdentifier,
          TransitOperation = transitOperation,
          Authorisation = authorisations,
          CustomsOfficeOfDeparture = customsOfficeOfDeparture,
          CustomsOfficeOfDestinationDeclared = customsOfficeOfDestinationDeclared,
          CustomsOfficeOfTransitDeclared = customsOfficeOfTransitDeclared,
          CustomsOfficeOfExitForTransitDeclared = customsOfficeOfExitForTransitDeclared,
          HolderOfTheTransitProcedure = holderOfTheTransitProcedure,
          Representative = representative,
          Guarantee = guarantee,
          Consignment = ???,
          attributes = Map("@PhaseID" -> DataRecord(PhaseIDtype.fromString("NCTS5.0", scope)))
        )
    }

  def submitDeclaration(userAnswers: UserAnswers)(implicit hc: HeaderCarrier): Future[HttpResponse] = {

    val declarationUrl = s"${appConfig.apiUrl}/movements/departures"

    createPayload(userAnswers) flatMap {
      case Left(msg) => throw new BadRequestException(s"${msg.page.toString}-${msg.message.getOrElse("Something went wrong")}")
      case Right(submissionModel) =>
        val payload: String = toXML[CC015CType](submissionModel, "ncts:CC015C", scope).toString
        httpClient.POSTString(declarationUrl, payload, requestHeaders)
    }

  }

}
