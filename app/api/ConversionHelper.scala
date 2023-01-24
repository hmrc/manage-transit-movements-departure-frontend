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
import models.journeyDomain.traderDetails.consignment.{ConsignmentConsigneeDomain, ConsignmentConsignorDomain}
import models.journeyDomain.traderDetails.holderOfTransit.AdditionalContactDomain
import models.journeyDomain.transport.carrierDetails.CarrierDetailsDomain
import models.journeyDomain.transport.supplyChainActors.SupplyChainActorsDomain
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

  protected def locationOfGoods(): Option[LocationOfGoodsType05] = ???

  protected def departureTransportMeans(): Seq[DepartureTransportMeansType03] = ???

  protected def countryOfRoutingOfConsignment(): Seq[CountryOfRoutingOfConsignmentType01] = ???

  protected def activeBorderTransportMeans(): Seq[ActiveBorderTransportMeansType02] = ???

  protected def placeOfLoading(): Option[PlaceOfLoadingType03] = ???

  protected def placeOfUnloading(): Option[PlaceOfUnloadingType01] = ???

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
