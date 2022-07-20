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
import models.guaranteeDetails.GuaranteeType
import models.reference.{Country, CustomsOffice}
import models.traderDetails.representative.RepresentativeCapacity
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import pages.preTaskList._
import pages.traderDetails._
import play.api.libs.json.{JsBoolean, JsString, JsValue, Json}
import queries.Gettable

trait UserAnswersEntryGenerators {
  self: Generators =>

  def generateAnswer: PartialFunction[Gettable[_], Gen[JsValue]] =
    generatePreTaskListAnswer orElse
      generateTraderDetailsAnswer orElse
      generateGuaranteeDetailsAnswer orElse
      generateRouteDetailsAnswer

  private def generatePreTaskListAnswer: PartialFunction[Gettable[_], Gen[JsValue]] = {
    case OfficeOfDeparturePage   => arbitrary[CustomsOffice](arbitraryOfficeOfDeparture).map(Json.toJson(_))
    case ProcedureTypePage       => arbitrary[ProcedureType].map(Json.toJson(_))
    case DeclarationTypePage     => arbitrary[DeclarationType].map(Json.toJson(_))
    case TIRCarnetReferencePage  => Gen.alphaNumStr.map(JsString)
    case SecurityDetailsTypePage => arbitrary[SecurityDetailsType].map(Json.toJson(_))
    case DetailsConfirmedPage    => Gen.const(true).map(JsBoolean)
  }

  private def generateTraderDetailsAnswer: PartialFunction[Gettable[_], Gen[JsValue]] =
    generateHolderOfTransitAnswer orElse
      generateRepresentativeAnswer orElse
      generateConsignmentAnswer orElse {
        case ActingAsRepresentativePage => arbitrary[Boolean].map(JsBoolean)
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
    generateRoutingAnswer

  private def generateRoutingAnswer: PartialFunction[Gettable[_], Gen[JsValue]] = {
    import pages.routeDetails.routing._
    {
      case BindingItineraryPage         => arbitrary[Boolean].map(JsBoolean)
      case AddCountryOfRoutingYesNoPage => arbitrary[Boolean].map(JsBoolean)
      case CountryOfRoutingPage(_)      => arbitrary[Country].map(Json.toJson(_))
    }
  }

}
