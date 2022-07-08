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
import models.reference.CustomsOffice
import models.traderDetails.representative.RepresentativeCapacity
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import pages.QuestionPage
import pages.preTaskList._
import pages.traderDetails._
import play.api.libs.json.{JsBoolean, JsString, JsValue, Json}
import queries.Gettable

trait UserAnswersEntryGenerators {
  self: Generators =>

  def generateAnswer: PartialFunction[Gettable[_], Gen[(QuestionPage[_], JsValue)]] =
    generatePreTaskListAnswer orElse
      generateTraderDetailsAnswer orElse
      generateGuaranteeDetailsAnswer

  private def generatePreTaskListAnswer: PartialFunction[Gettable[_], Gen[(QuestionPage[_], JsValue)]] = {
    case OfficeOfDeparturePage   => arbitrary[CustomsOffice](arbitraryOfficeOfDeparture).map(Json.toJson(_)).map((OfficeOfDeparturePage, _))
    case ProcedureTypePage       => arbitrary[ProcedureType].map(Json.toJson(_)).map((ProcedureTypePage, _))
    case DeclarationTypePage     => arbitrary[DeclarationType].map(Json.toJson(_)).map((DeclarationTypePage, _))
    case TIRCarnetReferencePage  => Gen.alphaNumStr.map(JsString).map((TIRCarnetReferencePage, _))
    case SecurityDetailsTypePage => arbitrary[SecurityDetailsType].map(Json.toJson(_)).map((SecurityDetailsTypePage, _))
    case DetailsConfirmedPage    => Gen.const(true).map(JsBoolean).map((DetailsConfirmedPage, _))
  }

  private def generateTraderDetailsAnswer: PartialFunction[Gettable[_], Gen[(QuestionPage[_], JsValue)]] =
    generateHolderOfTransitAnswer orElse
      generateRepresentativeAnswer orElse
      generateConsignmentAnswer orElse {
        case ActingAsRepresentativePage => arbitrary[Boolean].map(JsBoolean).map((ActingAsRepresentativePage, _))
      }

  private def generateHolderOfTransitAnswer: PartialFunction[Gettable[_], Gen[(QuestionPage[_], JsValue)]] = {
    import pages.traderDetails.holderOfTransit._
    {
      case EoriYesNoPage               => arbitrary[Boolean].map(JsBoolean).map((EoriYesNoPage, _))
      case EoriPage                    => Gen.alphaNumStr.map(JsString).map((EoriPage, _))
      case TirIdentificationYesNoPage  => arbitrary[Boolean].map(JsBoolean).map((TirIdentificationYesNoPage, _))
      case TirIdentificationPage       => Gen.alphaNumStr.map(JsString).map((TirIdentificationPage, _))
      case NamePage                    => Gen.alphaNumStr.map(JsString).map((NamePage, _))
      case AddressPage                 => arbitrary[Address].map(Json.toJson(_)).map((AddressPage, _))
      case AddContactPage              => arbitrary[Boolean].map(JsBoolean).map((AddContactPage, _))
      case contact.NamePage            => Gen.alphaNumStr.map(JsString).map((contact.NamePage, _))
      case contact.TelephoneNumberPage => Gen.alphaNumStr.map(JsString).map((contact.TelephoneNumberPage, _))
    }
  }

  private def generateRepresentativeAnswer: PartialFunction[Gettable[_], Gen[(QuestionPage[_], JsValue)]] = {
    import pages.traderDetails.representative._
    {
      case EoriPage            => Gen.alphaNumStr.map(JsString).map((EoriPage, _))
      case NamePage            => Gen.alphaNumStr.map(JsString).map((NamePage, _))
      case CapacityPage        => arbitrary[RepresentativeCapacity].map(Json.toJson(_)).map((CapacityPage, _))
      case TelephoneNumberPage => Gen.alphaNumStr.map(JsString).map((TelephoneNumberPage, _))
    }
  }

  private def generateConsignmentAnswer: PartialFunction[Gettable[_], Gen[(QuestionPage[_], JsValue)]] = {
    import pages.traderDetails.consignment._
    {
      generateConsignorAnswer orElse
        generateConsigneeAnswer orElse {
          case ApprovedOperatorPage     => arbitrary[Boolean].map(JsBoolean).map((ApprovedOperatorPage, _))
          case MoreThanOneConsigneePage => arbitrary[Boolean].map(JsBoolean).map((MoreThanOneConsigneePage, _))
        }
    }
  }

  private def generateConsignorAnswer: PartialFunction[Gettable[_], Gen[(QuestionPage[_], JsValue)]] = {
    import pages.traderDetails.consignment.consignor._
    {
      case EoriYesNoPage               => arbitrary[Boolean].map(JsBoolean).map((EoriYesNoPage, _))
      case EoriPage                    => Gen.alphaNumStr.map(JsString).map((EoriPage, _))
      case NamePage                    => Gen.alphaNumStr.map(JsString).map((NamePage, _))
      case AddressPage                 => arbitrary[Address].map(Json.toJson(_)).map((AddressPage, _))
      case AddContactPage              => arbitrary[Boolean].map(JsBoolean).map((AddContactPage, _))
      case contact.NamePage            => Gen.alphaNumStr.map(JsString).map((contact.NamePage, _))
      case contact.TelephoneNumberPage => Gen.alphaNumStr.map(JsString).map((contact.TelephoneNumberPage, _))
    }
  }

  private def generateConsigneeAnswer: PartialFunction[Gettable[_], Gen[(QuestionPage[_], JsValue)]] = {
    import pages.traderDetails.consignment.consignee._
    {
      case EoriYesNoPage  => arbitrary[Boolean].map(JsBoolean).map((EoriYesNoPage, _))
      case EoriNumberPage => Gen.alphaNumStr.map(JsString).map((EoriNumberPage, _))
      case NamePage       => Gen.alphaNumStr.map(JsString).map((NamePage, _))
      case AddressPage    => arbitrary[Address].map(Json.toJson(_)).map((AddressPage, _))
    }
  }

  private def generateGuaranteeDetailsAnswer: PartialFunction[Gettable[_], Gen[(QuestionPage[_], JsValue)]] = {
    import pages.guaranteeDetails._
    {
      case GuaranteeTypePage(index)   => arbitrary[GuaranteeType].map(Json.toJson(_)).map((GuaranteeTypePage(index), _))
      case ReferenceNumberPage(index) => Gen.alphaNumStr.map(JsString).map((ReferenceNumberPage(index), _))
      case OtherReferencePage(index)  => Gen.alphaNumStr.map(JsString).map((OtherReferencePage(index), _))
    }
  }

}
