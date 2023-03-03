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

package generators

import models._
import models.reference._
import models.transport.authorisations.AuthorisationType
import models.transport.equipment.PaymentMethod
import models.transport.supplyChainActors.SupplyChainActorType
import models.transport.transportMeans.BorderModeOfTransport
import models.transport.transportMeans.departure.{Identification, InlandMode}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import pages.traderDetails.consignment.ApprovedOperatorPage
import play.api.libs.json._
import queries.Gettable

import java.time.LocalDate

// scalastyle:off number.of.methods
trait UserAnswersEntryGenerators {
  self: Generators =>

  def generateAnswer: PartialFunction[Gettable[_], Gen[JsValue]] =
    generatePreTaskListAnswer orElse
      generateGuaranteeDetailsAnswer orElse
      generateRouteDetailsAnswer orElse
      generateTransportAnswer

  private def generatePreTaskListAnswer: PartialFunction[Gettable[_], Gen[JsValue]] = {
    import pages.preTaskList._
    {
      case OfficeOfDeparturePage   => arbitrary[CustomsOffice](arbitraryOfficeOfDeparture).map(Json.toJson(_))
      case ProcedureTypePage       => arbitrary[ProcedureType].map(Json.toJson(_))
      case DeclarationTypePage     => arbitrary[DeclarationType].map(Json.toJson(_))
      case TIRCarnetReferencePage  => Gen.alphaNumStr.map(JsString)
      case SecurityDetailsTypePage => arbitrary[SecurityDetailsType].map(Json.toJson(_))
      case DetailsConfirmedPage    => Gen.const(true).map(JsBoolean)
      case ApprovedOperatorPage    => arbitrary[Boolean].map(JsBoolean)
    }
  }

  private def generateGuaranteeDetailsAnswer: PartialFunction[Gettable[_], Gen[JsValue]] = {
    import pages.guaranteeDetails.guarantee._
    {
      case GuaranteeTypePage(_)       => arbitrary[GuaranteeType].map(Json.toJson(_))
      case ReferenceNumberPage(_)     => Gen.alphaNumStr.map(JsString)
      case OtherReferenceYesNoPage(_) => arbitrary[Boolean].map(JsBoolean)
      case OtherReferencePage(_)      => Gen.alphaNumStr.map(JsString)
      case AccessCodePage(_)          => Gen.alphaNumStr.map(JsString)
      case LiabilityAmountPage(_)     => Gen.choose(BigDecimal("0"), BigDecimal("9999999999999999.99")).map(Json.toJson(_))
      case CurrencyPage(_)            => arbitrary[CurrencyCode].map(Json.toJson(_))
    }
  }

  private def generateRouteDetailsAnswer: PartialFunction[Gettable[_], Gen[JsValue]] =
    generateRoutingAnswer orElse
      generateTransitAnswer orElse
      generateExitAnswer orElse
      generateLocationOfGoodsAnswer orElse
      generateLoadingAndUnloadingAnswer

  private def generateRoutingAnswer: PartialFunction[Gettable[_], Gen[JsValue]] = {
    import pages.routeDetails.routing._
    import pages.routeDetails.routing.index._
    {
      case CountryOfDestinationPage     => arbitrary[Country].map(Json.toJson(_))
      case OfficeOfDestinationPage      => arbitrary[CustomsOffice].map(Json.toJson(_))
      case BindingItineraryPage         => arbitrary[Boolean].map(JsBoolean)
      case AddCountryOfRoutingYesNoPage => arbitrary[Boolean].map(JsBoolean)
      case CountryOfRoutingPage(_)      => arbitrary[Country].map(Json.toJson(_))
    }
  }

  private def generateTransitAnswer: PartialFunction[Gettable[_], Gen[JsValue]] = {
    import pages.routeDetails.transit._
    import pages.routeDetails.transit.index._
    {
      case T2DeclarationTypeYesNoPage        => arbitrary[Boolean].map(JsBoolean)
      case AddOfficeOfTransitYesNoPage       => arbitrary[Boolean].map(JsBoolean)
      case OfficeOfTransitCountryPage(_)     => arbitrary[Country].map(Json.toJson(_))
      case OfficeOfTransitPage(_)            => arbitrary[CustomsOffice].map(Json.toJson(_))
      case AddOfficeOfTransitETAYesNoPage(_) => arbitrary[Boolean].map(JsBoolean)
      case OfficeOfTransitETAPage(_)         => arbitrary[DateTime].map(Json.toJson(_))
    }
  }

  private def generateExitAnswer: PartialFunction[Gettable[_], Gen[JsValue]] = {
    import pages.routeDetails.exit.index._
    {
      case OfficeOfExitCountryPage(_) => arbitrary[Country].map(Json.toJson(_))
      case OfficeOfExitPage(_)        => arbitrary[CustomsOffice].map(Json.toJson(_))
    }
  }

  private def generateLocationOfGoodsAnswer: PartialFunction[Gettable[_], Gen[JsValue]] = {
    import pages.routeDetails.locationOfGoods._
    {
      val pf: PartialFunction[Gettable[_], Gen[JsValue]] = {
        case AddLocationOfGoodsPage => arbitrary[Boolean].map(JsBoolean)
        case LocationTypePage       => arbitrary[LocationType].map(Json.toJson(_))
        case IdentificationPage     => arbitrary[LocationOfGoodsIdentification].map(Json.toJson(_))
        case AddContactYesNoPage    => arbitrary[Boolean].map(JsBoolean)
      }

      pf orElse
        generateLocationOfGoodsIdentifierAnswer orElse
        generateLocationOfGoodsContactAnswer
    }
  }

  private def generateLocationOfGoodsIdentifierAnswer: PartialFunction[Gettable[_], Gen[JsValue]] = {
    import pages.routeDetails.locationOfGoods._
    {
      case CustomsOfficeIdentifierPage => arbitrary[CustomsOffice].map(Json.toJson(_))
      case EoriPage                    => Gen.alphaNumStr.map(JsString)
      case AuthorisationNumberPage     => Gen.alphaNumStr.map(JsString)
      case AddIdentifierYesNoPage      => arbitrary[Boolean].map(JsBoolean)
      case AdditionalIdentifierPage    => Gen.alphaNumStr.map(JsString)
      case CoordinatesPage             => arbitrary[Coordinates].map(Json.toJson(_))
      case UnLocodePage                => arbitrary[UnLocode].map(Json.toJson(_))
      case CountryPage                 => arbitrary[Country].map(Json.toJson(_))
      case AddressPage                 => arbitrary[DynamicAddress].map(Json.toJson(_))
      case PostalCodePage              => arbitrary[PostalCodeAddress].map(Json.toJson(_))
    }
  }

  private def generateLocationOfGoodsContactAnswer: PartialFunction[Gettable[_], Gen[JsValue]] = {
    import pages.routeDetails.locationOfGoods.contact._
    {
      case NamePage            => Gen.alphaNumStr.map(JsString)
      case TelephoneNumberPage => Gen.alphaNumStr.map(JsString)
    }
  }

  private def generateLoadingAndUnloadingAnswer: PartialFunction[Gettable[_], Gen[JsValue]] = {
    import pages.routeDetails.loadingAndUnloading._
    {
      val pf: PartialFunction[Gettable[_], Gen[JsValue]] = {
        case AddPlaceOfUnloadingPage => arbitrary[Boolean].map(JsBoolean)
      }

      generateLoadingAnswer orElse
        pf orElse
        generateUnloadingAnswer
    }
  }

  private def generateLoadingAnswer: PartialFunction[Gettable[_], Gen[JsValue]] = {
    import pages.routeDetails.loadingAndUnloading.loading._
    {
      case AddUnLocodeYesNoPage         => arbitrary[Boolean].map(JsBoolean)
      case UnLocodePage                 => arbitrary[UnLocode].map(Json.toJson(_))
      case AddExtraInformationYesNoPage => arbitrary[Boolean].map(JsBoolean)
      case CountryPage                  => arbitrary[Country].map(Json.toJson(_))
      case LocationPage                 => Gen.alphaNumStr.map(JsString)
    }
  }

  private def generateUnloadingAnswer: PartialFunction[Gettable[_], Gen[JsValue]] = {
    import pages.routeDetails.loadingAndUnloading.AddPlaceOfUnloadingPage
    import pages.routeDetails.loadingAndUnloading.unloading._
    {
      case AddPlaceOfUnloadingPage      => arbitrary[Boolean].map(JsBoolean)
      case UnLocodeYesNoPage            => arbitrary[Boolean].map(JsBoolean)
      case UnLocodePage                 => arbitrary[UnLocode].map(Json.toJson(_))
      case AddExtraInformationYesNoPage => arbitrary[Boolean].map(JsBoolean)
      case CountryPage                  => arbitrary[Country].map(Json.toJson(_))
      case LocationPage                 => Gen.alphaNumStr.map(JsString)
    }
  }

  private def generateTransportAnswer: PartialFunction[Gettable[_], Gen[JsValue]] =
    generatePreRequisitesAnswer orElse
      generateTransportMeansAnswer orElse
      generateSupplyChainActorsAnswers orElse
      generateAuthorisationAnswers orElse
      generateLimitAnswers orElse
      generateCarrierDetailsAnswers orElse
      generateEquipmentsAndChargesAnswers

  private def generatePreRequisitesAnswer: PartialFunction[Gettable[_], Gen[JsValue]] = {
    import pages.transport.preRequisites._
    {
      case SameUcrYesNoPage                  => arbitrary[Boolean].map(JsBoolean)
      case UniqueConsignmentReferencePage    => Gen.alphaNumStr.map(JsString)
      case CountryOfDispatchPage             => arbitrary[Country].map(Json.toJson(_))
      case TransportedToSameCountryYesNoPage => arbitrary[Boolean].map(JsBoolean)
      case ItemsDestinationCountryPage       => arbitrary[Country].map(Json.toJson(_))
      case ContainerIndicatorPage            => arbitrary[Boolean].map(JsBoolean)
    }
  }

  private def generateTransportMeansAnswer: PartialFunction[Gettable[_], Gen[JsValue]] = {
    import pages.transport.transportMeans._
    generateTransportMeansDepartureAnswer orElse
      generateTransportMeansActiveAnswer orElse {
        case AnotherVehicleCrossingYesNoPage => arbitrary[Boolean].map(JsBoolean)
        case BorderModeOfTransportPage       => arbitrary[BorderModeOfTransport].map(Json.toJson(_))
      }
  }

  private def generateTransportMeansDepartureAnswer: PartialFunction[Gettable[_], Gen[JsValue]] = {
    import pages.transport.transportMeans.departure._
    {
      case InlandModePage                => arbitrary[InlandMode].map(Json.toJson(_))
      case IdentificationPage            => arbitrary[Identification].map(Json.toJson(_))
      case MeansIdentificationNumberPage => Gen.alphaNumStr.map(JsString)
      case VehicleCountryPage            => arbitrary[Nationality].map(Json.toJson(_))
    }
  }

  private def generateTransportMeansActiveAnswer: PartialFunction[Gettable[_], Gen[JsValue]] = {
    import pages.transport.transportMeans.active._
    {
      case IdentificationPage(_)                 => arbitrary[Identification].map(Json.toJson(_))
      case IdentificationNumberPage(_)           => Gen.alphaNumStr.map(JsString)
      case AddNationalityYesNoPage(_)            => arbitrary[Boolean].map(JsBoolean)
      case NationalityPage(_)                    => arbitrary[Nationality].map(Json.toJson(_))
      case CustomsOfficeActiveBorderPage(_)      => arbitrary[CustomsOffice].map(Json.toJson(_))
      case ConveyanceReferenceNumberYesNoPage(_) => arbitrary[Boolean].map(JsBoolean)
      case ConveyanceReferenceNumberPage(_)      => Gen.alphaNumStr.map(JsString)
    }
  }

  private def generateSupplyChainActorsAnswers: PartialFunction[Gettable[_], Gen[JsValue]] = {
    import pages.transport.supplyChainActors._

    val pf: PartialFunction[Gettable[_], Gen[JsValue]] = {
      case SupplyChainActorYesNoPage => arbitrary[Boolean].map(JsBoolean)
    }

    pf orElse
      generateSupplyChainActorAnswers
  }

  private def generateSupplyChainActorAnswers: PartialFunction[Gettable[_], Gen[JsValue]] = {
    import pages.transport.supplyChainActors.index._
    {
      case SupplyChainActorTypePage(_) => arbitrary[SupplyChainActorType].map(Json.toJson(_))
      case IdentificationNumberPage(_) => Gen.alphaNumStr.map(JsString)
    }
  }

  private def generateAuthorisationAnswers: PartialFunction[Gettable[_], Gen[JsValue]] = {
    import pages.transport.authorisationsAndLimit.authorisations._
    import pages.transport.authorisationsAndLimit.authorisations.index._
    {
      case AddAuthorisationsYesNoPage          => arbitrary[Boolean].map(JsBoolean)
      case AuthorisationTypePage(_)            => arbitrary[AuthorisationType].map(Json.toJson(_))
      case AuthorisationReferenceNumberPage(_) => Gen.alphaNumStr.map(JsString)
    }
  }

  private def generateLimitAnswers: PartialFunction[Gettable[_], Gen[JsValue]] = {
    import pages.transport.authorisationsAndLimit.limit.LimitDatePage
    {
      case LimitDatePage => arbitrary[LocalDate].map(Json.toJson(_))
    }
  }

  private def generateCarrierDetailsAnswers: PartialFunction[Gettable[_], Gen[JsValue]] = {
    import pages.transport.carrierDetails._
    import pages.transport.carrierDetails.contact._
    {
      case IdentificationNumberPage => Gen.alphaNumStr.map(JsString)
      case AddContactYesNoPage      => arbitrary[Boolean].map(JsBoolean)
      case NamePage                 => Gen.alphaNumStr.map(JsString)
      case TelephoneNumberPage      => Gen.alphaNumStr.map(JsString)
    }
  }

  private def generateEquipmentsAndChargesAnswers: PartialFunction[Gettable[_], Gen[JsValue]] = {
    import pages.transport.equipment._
    val pf: PartialFunction[Gettable[_], Gen[JsValue]] = {
      case AddTransportEquipmentYesNoPage => arbitrary[Boolean].map(JsBoolean)
      case AddPaymentMethodYesNoPage      => arbitrary[Boolean].map(JsBoolean)
      case PaymentMethodPage              => arbitrary[PaymentMethod].map(Json.toJson(_))
    }

    pf orElse
      generateEquipmentAnswers
  }

  private def generateEquipmentAnswers: PartialFunction[Gettable[_], Gen[JsValue]] = {
    import pages.transport.equipment.index._

    val pf: PartialFunction[Gettable[_], Gen[JsValue]] = {
      case AddContainerIdentificationNumberYesNoPage(_) => arbitrary[Boolean].map(JsBoolean)
      case ContainerIdentificationNumberPage(_)         => Gen.alphaNumStr.map(JsString)
      case AddSealYesNoPage(_)                          => arbitrary[Boolean].map(JsBoolean)
      case AddGoodsItemNumberYesNoPage(_)               => arbitrary[Boolean].map(JsBoolean)
    }

    pf orElse
      generateSealAnswers orElse
      generateItemNumberAnswers
  }

  private def generateSealAnswers: PartialFunction[Gettable[_], Gen[JsValue]] = {
    import pages.transport.equipment.index.seals._
    {
      case IdentificationNumberPage(_, _) => Gen.alphaNumStr.map(JsString)
    }
  }

  private def generateItemNumberAnswers: PartialFunction[Gettable[_], Gen[JsValue]] = {
    import pages.transport.equipment.index.itemNumber._
    {
      case ItemNumberPage(_, _) => Gen.alphaNumStr.map(JsString)
    }
  }
}
// scalastyle:on number.of.methods
