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

package api.submission

import api.ApiXmlHelper
import base.SpecBase
import commonTestUtils.UserAnswersSpecHelper
import generated._
import generators.Generators
import models.domain.UserAnswersReader
import models.journeyDomain.DepartureDomain
import models.journeyDomain.DepartureDomain.userAnswersReader
import models.journeyDomain.routeDetails.locationOfGoods.LocationOfGoodsDomain
import models.journeyDomain.transport.transportMeans.{TransportMeansDepartureDomainWithIdentification, TransportMeansDomainWithOtherInlandMode}

class ConsignmentSpec extends SpecBase with UserAnswersSpecHelper with Generators {

  "Consignment" - {

    "transform is called" - {

      "will convert to API format" in {

        arbitraryDepartureAnswers(emptyUserAnswers).map(
          arbitraryDepartureUserAnswers =>
            UserAnswersReader[DepartureDomain](userAnswersReader(ctcCountryCodes, customsSecurityAgreementAreaCountryCodes))
              .run(arbitraryDepartureUserAnswers)
              .map {
                case DepartureDomain(_, traderDetailsDomain, routeDetailsDomain, _, transportDomain) =>
                  val expected = ConsignmentType20(
                    countryOfDispatch = transportDomain.preRequisites.countryOfDispatch.map(
                      x => x.code.code
                    ),
                    countryOfDestination = transportDomain.preRequisites.itemsDestinationCountry.map(
                      x => x.code.code
                    ),
                    containerIndicator = Some(ApiXmlHelper.boolToFlag(transportDomain.preRequisites.containerIndicator)),
                    inlandModeOfTransport = Some(transportDomain.transportMeans.inlandMode.inlandModeType.toString),
                    modeOfTransportAtTheBorder = None,
                    grossMass = 1d,
                    referenceNumberUCR = transportDomain.preRequisites.ucr,
                    Carrier = Some(
                      CarrierType04(
                        transportDomain.carrierDetails.identificationNumber,
                        transportDomain.carrierDetails.contactPerson.map(
                          x => ContactPersonType05(x.name, x.telephoneNumber, None)
                        )
                      )
                    ),
                    Consignor = traderDetailsDomain.consignment.consignor.map(
                      consignor =>
                        ConsignorType07(
                          consignor.eori.map(
                            x => x.value
                          ),
                          Some(consignor.name),
                          Some(
                            AddressType17(consignor.address.numberAndStreet, consignor.address.postalCode, consignor.address.city, consignor.country.code.code)
                          )
                        )
                    ),
                    Consignee = traderDetailsDomain.consignment.consignee.map(
                      consignee =>
                        ConsigneeType05(
                          consignee.eori.map(
                            x => x.value
                          ),
                          Some(consignee.name),
                          Some(
                            AddressType17(consignee.address.numberAndStreet, consignee.address.postalCode, consignee.address.city, consignee.country.code.code)
                          )
                        )
                    ),
                    AdditionalSupplyChainActor = transportDomain.supplyChainActors
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
                      .getOrElse(Seq.empty),
                    TransportEquipment = Seq.empty,
                    LocationOfGoods = routeDetailsDomain.locationOfGoods.map(
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
                              Some(
                                PostcodeAddressType02(Some(postalCodeAddress.streetNumber), postalCodeAddress.postalCode, postalCodeAddress.country.code.code)
                              )
                            case _ => None
                          },
                          ContactPerson = locationOfGoodsDomain.contactPerson.map(
                            p => ContactPersonType06(p.name, p.telephoneNumber)
                          )
                        )
                    ),
                    DepartureTransportMeans = transportDomain.transportMeans match {
                      case TransportMeansDomainWithOtherInlandMode(_, means: TransportMeansDepartureDomainWithIdentification, _) =>
                        Seq(
                          DepartureTransportMeansType03("0",
                                                        Some(means.identification.identificationType.toString),
                                                        Some(means.identificationNumber),
                                                        Some(means.nationality.code)
                          )
                        )
                      case _ => Seq.empty
                    },
                    CountryOfRoutingOfConsignment = routeDetailsDomain.routing.countriesOfRouting.map(
                      c =>
                        CountryOfRoutingOfConsignmentType01(
                          routeDetailsDomain.routing.countriesOfRouting.indexOf(c).toString,
                          c.country.code.code
                        )
                    ),
                    ActiveBorderTransportMeans = transportDomain.transportMeans match {
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
                    },
                    PlaceOfLoading = routeDetailsDomain.loadingAndUnloading.loading.map(
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
                    ),
                    PlaceOfUnloading = routeDetailsDomain.loadingAndUnloading.unloading.map(
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
                    ),
                    PreviousDocument = Seq.empty, // TODO - When the journey is built
                    SupportingDocument = Seq.empty, // TODO - When the journey is built
                    TransportDocument = Seq.empty, // TODO - When the journey is built
                    AdditionalReference = Seq.empty, // TODO - When the journey is built
                    AdditionalInformation = Seq.empty, // TODO - When the journey is built
                    TransportCharges = None, // TODO - When the journey is built
                    // TODO - Build house consignment from generated user answers when that journey is built
                    HouseConsignment = Seq(
                      HouseConsignmentType10(
                        sequenceNumber = "1",
                        countryOfDispatch = None,
                        grossMass = 1d,
                        referenceNumberUCR = None,
                        Consignor = None,
                        Consignee = None,
                        AdditionalSupplyChainActor = Seq.empty,
                        DepartureTransportMeans = Seq.empty,
                        PreviousDocument = Seq.empty,
                        SupportingDocument = Seq.empty,
                        TransportDocument = Seq.empty,
                        AdditionalReference = Seq.empty,
                        AdditionalInformation = Seq.empty,
                        TransportCharges = None,
                        ConsignmentItem = Seq(
                          ConsignmentItemType09(
                            goodsItemNumber = "1",
                            declarationGoodsItemNumber = 1,
                            declarationType = None,
                            countryOfDispatch = None,
                            countryOfDestination = None,
                            referenceNumberUCR = None,
                            Consignee = None,
                            AdditionalSupplyChainActor = Seq.empty,
                            Commodity = CommodityType06(
                              descriptionOfGoods = "test",
                              cusCode = None,
                              CommodityCode = None,
                              DangerousGoods = Seq.empty,
                              GoodsMeasure = None
                            ),
                            Packaging = Seq(
                              PackagingType03(
                                sequenceNumber = "1",
                                typeOfPackages = "Nu",
                                numberOfPackages = None,
                                shippingMarks = None
                              )
                            )
                          )
                        )
                      )
                    )
                  )

                  val converted = Consignment.transform(transportDomain, traderDetailsDomain, routeDetailsDomain)

                  converted mustBe expected

              }
        )

      }

    }

  }

}
