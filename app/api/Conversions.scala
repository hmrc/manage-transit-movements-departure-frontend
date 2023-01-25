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

package api

import generated._
import models.journeyDomain.PreTaskListDomain
import models.journeyDomain.guaranteeDetails.{GuaranteeDetailsDomain, GuaranteeDomain}
import models.journeyDomain.routeDetails.RouteDetailsDomain
import models.journeyDomain.routeDetails.exit.ExitDomain
import models.journeyDomain.routeDetails.routing.RoutingDomain
import models.journeyDomain.routeDetails.transit.TransitDomain
import models.journeyDomain.traderDetails.TraderDetailsDomain
import models.journeyDomain.traderDetails.holderOfTransit.HolderOfTransitDomain
import models.journeyDomain.transport.TransportDomain
import models.journeyDomain.transport.authorisationsAndLimit.authorisations.AuthorisationsDomain
import models.reference.CustomsOffice
import org.joda.time.DateTime

object Conversions extends ConversionHelper {

  def message: MESSAGE_FROM_TRADERSequence =
    MESSAGE_FROM_TRADERSequence(
      None,
      MESSAGE_1Sequence(
        "NCTS",
        ApiXmlHelpers.toDate(DateTime.now().toString(formatterNoMillis)),
        "CC015C" // TODO - check this with API team? What should this be set to?
      )
    )

  def messageType: MessageType015 = MessageType015.fromString("CC015C", scope)

  // TODO - What should this be?
  def correlationIdentifier = CORRELATION_IDENTIFIERSequence(None)

  def transitOperation(lrn: String,
                       preTaskListDomain: PreTaskListDomain,
                       reducedDatasetIndicator: Boolean,
                       routingDomain: RoutingDomain
  ): TransitOperationType06 =
    TransitOperationType06(
      LRN = lrn,
      declarationType = preTaskListDomain.declarationType.toString,
      additionalDeclarationType = "A",
      TIRCarnetNumber = preTaskListDomain.tirCarnetReference,
      presentationOfTheGoodsDateAndTime = None, // TODO - what is this? Needed?
      security = preTaskListDomain.securityDetailsType.securityContentType.toString,
      reducedDatasetIndicator = ApiXmlHelpers.boolToFlag(reducedDatasetIndicator),
      specificCircumstanceIndicator = None, // TODO - what is this? Needed?
      communicationLanguageAtDeparture = None, // TODO - what is this? Needed?
      bindingItinerary = ApiXmlHelpers.boolToFlag(routingDomain.bindingItinerary),
      limitDate = None // TODO - what is this? Needed?
    )

  def authorisations(domain: Option[AuthorisationsDomain]): Seq[AuthorisationType03] =
    domain
      .map(
        authorisation =>
          authorisation.authorisations.map(
            a =>
              AuthorisationType03(
                authorisation.authorisations.indexOf(a).toString,
                a.authorisationType.toString,
                a.referenceNumber
              )
          )
      )
      .getOrElse(Seq.empty)

  def customsOfficeOfDeparture(customsOffice: CustomsOffice): CustomsOfficeOfDepartureType03 =
    CustomsOfficeOfDepartureType03(customsOffice.id)

  def customsOfficeOfDestination(customsOffice: CustomsOffice): CustomsOfficeOfDestinationDeclaredType01 =
    CustomsOfficeOfDestinationDeclaredType01(customsOffice.id)

  def customsOfficeOfTransit(domain: Option[TransitDomain]): Seq[CustomsOfficeOfTransitDeclaredType03] =
    domain
      .map(
        transitDomain =>
          transitDomain.officesOfTransit
            .map(
              officeOfTransitDomain =>
                CustomsOfficeOfTransitDeclaredType03(
                  transitDomain.officesOfTransit.indexOf(officeOfTransitDomain.customsOffice).toString,
                  officeOfTransitDomain.customsOffice.id
                )
            )
      )
      .getOrElse(Seq.empty)

  def customsOfficeOfExit(domain: Option[ExitDomain]): Seq[CustomsOfficeOfExitForTransitDeclaredType02] =
    domain
      .map(
        transitDomain =>
          transitDomain.officesOfExit
            .map(
              officeOfExitDomain =>
                CustomsOfficeOfExitForTransitDeclaredType02(
                  transitDomain.officesOfExit.indexOf(officeOfExitDomain).toString,
                  officeOfExitDomain.customsOffice.id
                )
            )
      )
      .getOrElse(Seq.empty)

  def holderOfTheTransitProcedure(domain: TraderDetailsDomain): HolderOfTheTransitProcedureType14 =
    domain.holderOfTransit match {
      case HolderOfTransitDomain.HolderOfTransitEori(eori, name, country, address, additionalContact) =>
        holderOfTheTransitProcedure(eori.map(
                                      x => x.value
                                    ),
                                    Some(name),
                                    country,
                                    address,
                                    additionalContact
        )
      case HolderOfTransitDomain.HolderOfTransitTIR(tir, name, country, address, additionalContact) =>
        holderOfTheTransitProcedure(tir, Some(name), country, address, additionalContact)
    }

  def representative(domain: TraderDetailsDomain): Option[RepresentativeType05] =
    domain.representative.map {
      r =>
        RepresentativeType05(
          r.eori.value,
          r.capacity.toString,
          Some(ContactPersonType05(r.name, r.phone, None))
        )
    }

  def guarantee(domain: GuaranteeDetailsDomain): Seq[GuaranteeType02] =
    domain.guarantees.map {
      case guaranteeDomain @ GuaranteeDomain.GuaranteeOfTypesABR(guaranteeType) =>
        GuaranteeType02(
          guaranteeDomain.index.position.toString,
          guaranteeType.toString,
          None
        )
      case guaranteeDomain @ GuaranteeDomain.GuaranteeOfTypes01249(guaranteeType, grn, accessCode, liabilityAmount) =>
        GuaranteeType02(
          guaranteeDomain.index.position.toString,
          guaranteeType.toString,
          None,
          Seq(GuaranteeReferenceType03(guaranteeDomain.index.position.toString, Some(grn), Some(accessCode), Some(liabilityAmount)))
        )
      case guaranteeDomain @ GuaranteeDomain.GuaranteeOfType5(guaranteeType, grn) =>
        GuaranteeType02(
          guaranteeDomain.index.position.toString,
          guaranteeType.toString,
          None,
          Seq(GuaranteeReferenceType03(guaranteeDomain.index.position.toString, Some(grn), None, None))
        )
      case guaranteeDomain @ GuaranteeDomain.GuaranteeOfType8(guaranteeType, otherReference) =>
        GuaranteeType02(
          guaranteeDomain.index.position.toString,
          guaranteeType.toString,
          Some(otherReference)
        )
      case guaranteeDomain @ GuaranteeDomain.GuaranteeOfType3(guaranteeType, otherReference) =>
        GuaranteeType02(
          guaranteeDomain.index.position.toString,
          guaranteeType.toString,
          otherReference
        )
    }

  def consignment(transportDomain: TransportDomain, traderDetailsDomain: TraderDetailsDomain, routeDetailsDomain: RouteDetailsDomain): ConsignmentType20 =
    ConsignmentType20(
      countryOfDispatch = transportDomain.preRequisites.countryOfDispatch.map(
        x => x.code.code
      ),
      countryOfDestination = transportDomain.preRequisites.itemsDestinationCountry.map(
        x => x.code.code
      ),
      containerIndicator = Some(ApiXmlHelpers.boolToFlag(transportDomain.preRequisites.containerIndicator)),
      inlandModeOfTransport = Some(transportDomain.transportMeans.inlandMode.inlandModeType.toString),
      modeOfTransportAtTheBorder = ???, // TODO - come back to this
      grossMass = ???, // TODO - Need items for this
      referenceNumberUCR = transportDomain.preRequisites.ucr,
      Carrier = carrierType(transportDomain.carrierDetails),
      Consignor = consignor(traderDetailsDomain.consignment.consignor),
      Consignee = consignee(traderDetailsDomain.consignment.consignee),
      AdditionalSupplyChainActor = additionalSupplyChainActor(transportDomain.supplyChainActors),
      TransportEquipment = transportEquipment(), // TODO - when the journey is built
      LocationOfGoods = locationOfGoods(routeDetailsDomain.locationOfGoods),
      DepartureTransportMeans = departureTransportMeans(transportDomain.transportMeans),
      CountryOfRoutingOfConsignment = countryOfRoutingOfConsignment(routeDetailsDomain.routing),
      ActiveBorderTransportMeans = activeBorderTransportMeans(transportDomain.transportMeans),
      PlaceOfLoading = placeOfLoading(routeDetailsDomain.loadingAndUnloading),
      PlaceOfUnloading = placeOfUnloading(routeDetailsDomain.loadingAndUnloading),
      PreviousDocument = previousDocument(), // TODO - when the journey is built
      SupportingDocument = supportingDocument(), // TODO - when the journey is built
      TransportDocument = transportDocument(), // TODO - when the journey is built
      AdditionalReference = additionalReference(), // TODO - what is this?
      AdditionalInformation = additionalInformation(), // TODO - what is this?
      TransportCharges = transportCharges(), // TODO - what is this?
      HouseConsignment = houseConsignment() // TODO - whats is this if we already have a consignment?
    )
}
