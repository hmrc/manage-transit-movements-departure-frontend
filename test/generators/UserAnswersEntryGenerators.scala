/*
 * Copyright 2022 HM Revenue & Customs
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
import models.traderDetails.representative.RepresentativeCapacity
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import play.api.libs.json._
import queries.Gettable

trait UserAnswersEntryGenerators {
  self: Generators =>

  def generateAnswer: PartialFunction[Gettable[_], Gen[JsValue]] =
    generatePreTaskListAnswer orElse
      generateTraderDetailsAnswer orElse
      generateGuaranteeDetailsAnswer orElse
      generateRouteDetailsAnswer

  private def generatePreTaskListAnswer: PartialFunction[Gettable[_], Gen[JsValue]] = {
    import pages.preTaskList._
    {
      case OfficeOfDeparturePage   => arbitrary[CustomsOffice](arbitraryOfficeOfDeparture).map(Json.toJson(_))
      case ProcedureTypePage       => arbitrary[ProcedureType].map(Json.toJson(_))
      case DeclarationTypePage     => arbitrary[DeclarationType].map(Json.toJson(_))
      case TIRCarnetReferencePage  => Gen.alphaNumStr.map(JsString)
      case SecurityDetailsTypePage => arbitrary[SecurityDetailsType].map(Json.toJson(_))
      case DetailsConfirmedPage    => Gen.const(true).map(JsBoolean)
    }
  }

  private def generateTraderDetailsAnswer: PartialFunction[Gettable[_], Gen[JsValue]] = {
    import pages.traderDetails._
    {
      generateHolderOfTransitAnswer orElse
        generateRepresentativeAnswer orElse
        generateConsignmentAnswer orElse {
          case ActingAsRepresentativePage => arbitrary[Boolean].map(JsBoolean)
        }
    }
  }

  private def generateHolderOfTransitAnswer: PartialFunction[Gettable[_], Gen[JsValue]] = {
    import pages.traderDetails.holderOfTransit._
    {
      case EoriYesNoPage               => arbitrary[Boolean].map(JsBoolean)
      case EoriPage                    => Gen.alphaNumStr.map(JsString)
      case TirIdentificationYesNoPage  => arbitrary[Boolean].map(JsBoolean)
      case TirIdentificationPage       => Gen.alphaNumStr.map(JsString)
      case NamePage                    => Gen.alphaNumStr.map(JsString)
      case AddressPage                 => arbitrary[Address].map(Json.toJson(_))
      case AddContactPage              => arbitrary[Boolean].map(JsBoolean)
      case contact.NamePage            => Gen.alphaNumStr.map(JsString)
      case contact.TelephoneNumberPage => Gen.alphaNumStr.map(JsString)
    }
  }

  private def generateRepresentativeAnswer: PartialFunction[Gettable[_], Gen[JsValue]] = {
    import pages.traderDetails.representative._
    {
      case EoriPage            => Gen.alphaNumStr.map(JsString)
      case NamePage            => Gen.alphaNumStr.map(JsString)
      case CapacityPage        => arbitrary[RepresentativeCapacity].map(Json.toJson(_))
      case TelephoneNumberPage => Gen.alphaNumStr.map(JsString)
    }
  }

  private def generateConsignmentAnswer: PartialFunction[Gettable[_], Gen[JsValue]] = {
    import pages.traderDetails.consignment._
    {
      generateConsignorAnswer orElse
        generateConsigneeAnswer orElse {
          case ApprovedOperatorPage     => arbitrary[Boolean].map(JsBoolean)
          case MoreThanOneConsigneePage => arbitrary[Boolean].map(JsBoolean)
        }
    }
  }

  private def generateConsignorAnswer: PartialFunction[Gettable[_], Gen[JsValue]] = {
    import pages.traderDetails.consignment.consignor._
    {
      case EoriYesNoPage               => arbitrary[Boolean].map(JsBoolean)
      case EoriPage                    => Gen.alphaNumStr.map(JsString)
      case NamePage                    => Gen.alphaNumStr.map(JsString)
      case AddressPage                 => arbitrary[Address].map(Json.toJson(_))
      case AddContactPage              => arbitrary[Boolean].map(JsBoolean)
      case contact.NamePage            => Gen.alphaNumStr.map(JsString)
      case contact.TelephoneNumberPage => Gen.alphaNumStr.map(JsString)
    }
  }

  private def generateConsigneeAnswer: PartialFunction[Gettable[_], Gen[JsValue]] = {
    import pages.traderDetails.consignment.consignee._
    {
      case EoriYesNoPage  => arbitrary[Boolean].map(JsBoolean)
      case EoriNumberPage => Gen.alphaNumStr.map(JsString)
      case NamePage       => Gen.alphaNumStr.map(JsString)
      case AddressPage    => arbitrary[Address].map(Json.toJson(_))
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
    }
  }

  private def generateRouteDetailsAnswer: PartialFunction[Gettable[_], Gen[JsValue]] =
    generateRoutingAnswer orElse
      generateTransitAnswer orElse
      generateExitAnswer orElse
      generateLocationOfGoodsAnswer orElse
      generateLoadingAnswer orElse
      generateUnloadingAnswer

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
      case AddressPage                 => arbitrary[Address].map(Json.toJson(_))
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

  private def generateLoadingAnswer: PartialFunction[Gettable[_], Gen[JsValue]] = {
    import pages.routeDetails.loadingAndUnloading.loading._
    {
      case PlaceOfLoadingAddUnLocodeYesNoPage         => arbitrary[Boolean].map(JsBoolean)
      case PlaceOfLoadingUnLocodePage                 => arbitrary[UnLocode].map(Json.toJson(_))
      case PlaceOfLoadingAddExtraInformationYesNoPage => arbitrary[Boolean].map(JsBoolean)
      case PlaceOfLoadingCountryPage                  => arbitrary[Country].map(Json.toJson(_))
      case PlaceOfLoadingLocationPage                 => Gen.alphaNumStr.map(JsString)
    }
  }

  private def generateUnloadingAnswer: PartialFunction[Gettable[_], Gen[JsValue]] = {
    import pages.routeDetails.loadingAndUnloading.unloading._
    {
      case AddPlaceOfUnloadingPage           => arbitrary[Boolean].map(JsBoolean)
      case PlaceOfUnloadingUnLocodeYesNoPage => arbitrary[Boolean].map(JsBoolean)
      case AddExtraInformationYesNoPage      => arbitrary[Boolean].map(JsBoolean)
      case CountryPage                       => arbitrary[Country].map(Json.toJson(_))
    }
  }

}
