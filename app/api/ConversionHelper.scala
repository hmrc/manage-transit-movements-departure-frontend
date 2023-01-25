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
import models.DynamicAddress
import models.journeyDomain.routeDetails.loadingAndUnloading.LoadingAndUnloadingDomain
import models.journeyDomain.routeDetails.locationOfGoods.LocationOfGoodsDomain
import models.journeyDomain.routeDetails.routing.RoutingDomain
import models.journeyDomain.traderDetails.consignment.{ConsignmentConsigneeDomain, ConsignmentConsignorDomain}
import models.journeyDomain.traderDetails.holderOfTransit.AdditionalContactDomain
import models.journeyDomain.transport.carrierDetails.CarrierDetailsDomain
import models.journeyDomain.transport.supplyChainActors.SupplyChainActorsDomain
import models.journeyDomain.transport.transportMeans.{
  TransportMeansDepartureDomainWithOtherInlandMode,
  TransportMeansDomain,
  TransportMeansDomainWithOtherInlandMode
}
import models.reference.Country
import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}

import scala.xml.NamespaceBinding

trait ConversionHelper {

  val scope: NamespaceBinding              = scalaxb.toScope(Some("ncts") -> "http://ncts.dgtaxud.ec")
  val formatterNoMillis: DateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss")

  protected def carrierType(domain: CarrierDetailsDomain): Option[CarrierType04] =
    Some(
      CarrierType04(domain.identificationNumber,
                    domain.contactPerson.map(
                      x => ContactPersonType05(x.name, x.telephoneNumber, None)
                    )
      )
    )

  protected def consignor(domain: Option[ConsignmentConsignorDomain]): Option[ConsignorType07] =
    domain.map(
      consignor =>
        ConsignorType07(
          consignor.eori.map(
            x => x.toString
          ),
          Some(consignor.name),
          Some(AddressType17(consignor.address.numberAndStreet, consignor.address.postalCode, consignor.address.city, consignor.country.code.code))
        )
    )

  protected def consignee(domain: Option[ConsignmentConsigneeDomain]): Option[ConsigneeType05] =
    domain.map(
      consignee =>
        ConsigneeType05(
          consignee.eori.map(
            x => x.toString
          ),
          Some(consignee.name),
          Some(AddressType17(consignee.address.numberAndStreet, consignee.address.postalCode, consignee.address.city, consignee.country.code.code))
        )
    )

  protected def additionalSupplyChainActor(domain: Option[SupplyChainActorsDomain]): Seq[AdditionalSupplyChainActorType] =
    domain
      .map(
        supplyChainActorsDomain =>
          supplyChainActorsDomain.SupplyChainActorsDomain.map(
            supplyChainActor =>
              AdditionalSupplyChainActorType(
                supplyChainActorsDomain.SupplyChainActorsDomain.indexOf(supplyChainActor).toString,
                supplyChainActor.role.toString,
                supplyChainActor.identification
              )
          )
      )
      .getOrElse(Seq.empty)

  protected def transportEquipment(): Seq[TransportEquipmentType06] = ???

  protected def locationOfGoods(domain: Option[LocationOfGoodsDomain]): Option[LocationOfGoodsType05] =
    domain.map(
      locationOfGoodsDomain =>
        LocationOfGoodsType05(
          typeOfLocation = locationOfGoodsDomain.typeOfLocation.code,
          qualifierOfIdentification = locationOfGoodsDomain.qualifierOfIdentification.code,
          authorisationNumber = locationOfGoodsDomain match {
            case LocationOfGoodsDomain.LocationOfGoodsY(_, authorisationNumber, _, _) => Some(authorisationNumber)
            case _                                                                    => None
          },
          additionalIdentifier = locationOfGoodsDomain match {
            case LocationOfGoodsDomain.LocationOfGoodsY(_, _, additionalIdentifier, _) => additionalIdentifier
            case _                                                                     => None
          },
          UNLocode = locationOfGoodsDomain match {
            case LocationOfGoodsDomain.LocationOfGoodsU(_, unLocode, _) => Some(unLocode.unLocodeExtendedCode)
            case _                                                      => None
          },
          CustomsOffice = locationOfGoodsDomain match {
            case LocationOfGoodsDomain.LocationOfGoodsV(_, customsOffice) => Some(CustomsOfficeType02(customsOffice.id))
            case _                                                        => None
          },
          GNSS = locationOfGoodsDomain match {
            case LocationOfGoodsDomain.LocationOfGoodsW(_, coordinates, _) => Some(GNSSType(coordinates.latitude, coordinates.longitude))
            case _                                                         => None
          },
          EconomicOperator = locationOfGoodsDomain match {
            case LocationOfGoodsDomain.LocationOfGoodsX(_, identificationNumber, _, _) => Some(EconomicOperatorType03(identificationNumber))
            case _                                                                     => None
          },
          Address = locationOfGoodsDomain match {
            case LocationOfGoodsDomain.LocationOfGoodsZ(_, country, address, _) =>
              Some(AddressType14(address.numberAndStreet, address.postalCode, address.city, country.code.code))
            case _ => None
          },
          PostcodeAddress = locationOfGoodsDomain match {
            case LocationOfGoodsDomain.LocationOfGoodsT(_, postalCodeAddress, _) =>
              Some(PostcodeAddressType02(Some(postalCodeAddress.streetNumber), postalCodeAddress.postalCode, postalCodeAddress.country.code.code))
            case _ => None
          },
          ContactPerson = locationOfGoodsDomain.contactPerson.map(
            p => ContactPersonType06(p.name, p.telephoneNumber)
          )
        )
    )

  protected def departureTransportMeans(domain: TransportMeansDomain): Seq[DepartureTransportMeansType03] =
    domain match {
      case TransportMeansDomainWithOtherInlandMode(_, transportMeansDepartureDomain, _) =>
        val means: TransportMeansDepartureDomainWithOtherInlandMode =
          transportMeansDepartureDomain.asInstanceOf[TransportMeansDepartureDomainWithOtherInlandMode]

        Seq(
          DepartureTransportMeansType03("0",
                                        Some(means.identification.identificationType.toString),
                                        Some(means.identificationNumber),
                                        Some(means.nationality.code)
          )
        )
      case _ => Seq.empty
    }

  protected def activeBorderTransportMeans(domain: TransportMeansDomain): Seq[ActiveBorderTransportMeansType02] =
    domain match {
      case TransportMeansDomainWithOtherInlandMode(_, _, transportMeansActiveList) =>
        transportMeansActiveList
          .map(
            activeList =>
              activeList.transportMeansActiveListDomain.map(
                active =>
                  ActiveBorderTransportMeansType02(
                    sequenceNumber = activeList.transportMeansActiveListDomain.indexOf(active).toString,
                    customsOfficeAtBorderReferenceNumber = Some(active.customsOffice.id),
                    typeOfIdentification = Some(active.identification.borderModeType.toString),
                    identificationNumber = Some(active.identificationNumber),
                    nationality = active.nationality.map(
                      n => n.code
                    ),
                    conveyanceReferenceNumber = active.conveyanceReferenceNumber
                  )
              )
          )
          .getOrElse(Seq.empty)
      case _ => Seq.empty
    }

  protected def countryOfRoutingOfConsignment(domain: RoutingDomain): Seq[CountryOfRoutingOfConsignmentType01] =
    domain.countriesOfRouting.map(
      c =>
        CountryOfRoutingOfConsignmentType01(
          domain.countriesOfRouting.indexOf(c).toString,
          c.country.code.code
        )
    )

  protected def placeOfLoading(domain: LoadingAndUnloadingDomain): Option[PlaceOfLoadingType03] =
    domain.loading.map(
      x =>
        PlaceOfLoadingType03(
          x.unLocode.map(
            locode => locode.unLocodeExtendedCode
          ),
          x.additionalInformation.map(
            info => info.country.code.code
          ),
          x.additionalInformation.map(
            info => info.location
          )
        )
    )

  protected def placeOfUnloading(domain: LoadingAndUnloadingDomain): Option[PlaceOfUnloadingType01] =
    domain.unloading.map(
      x =>
        PlaceOfUnloadingType01(
          x.unLocode.map(
            locode => locode.unLocodeExtendedCode
          ),
          x.additionalInformation.map(
            info => info.country.code.code
          ),
          x.additionalInformation.map(
            info => info.location
          )
        )
    )

  protected def previousDocument(): Seq[PreviousDocumentType09] = ???

  protected def supportingDocument(): Seq[SupportingDocumentType05] = ???

  protected def transportDocument(): Seq[TransportDocumentType04] = ???

  protected def additionalReference(): Seq[AdditionalReferenceType06] = ???

  protected def additionalInformation(): Seq[AdditionalInformationType03] = ???

  protected def transportCharges(): Option[TransportChargesType] = ???

  protected def houseConsignment(): Seq[HouseConsignmentType10] = ???

  protected def holderOfTheTransitProcedure(id: Option[String],
                                            name: Option[String],
                                            country: Country,
                                            address: DynamicAddress,
                                            additionalContact: Option[AdditionalContactDomain]
  ) =
    HolderOfTheTransitProcedureType14(
      identificationNumber = id,
      TIRHolderIdentificationNumber = None,
      name = name,
      Address = Some(AddressType17(address.numberAndStreet, address.postalCode, address.city, country.code.toString)),
      ContactPerson = additionalContact.map(
        x => ContactPersonType05(x.name, x.telephoneNumber, None)
      )
    )
}
