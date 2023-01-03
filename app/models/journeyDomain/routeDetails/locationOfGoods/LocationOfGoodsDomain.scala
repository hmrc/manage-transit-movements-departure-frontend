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

package models.journeyDomain.routeDetails.locationOfGoods

import cats.implicits._
import models.LocationOfGoodsIdentification._
import models.domain.{GettableAsFilterForNextReaderOps, GettableAsReaderOps, UserAnswersReader}
import models.journeyDomain.{JourneyDomainModel, Stage}
import models.reference.{Country, CustomsOffice, UnLocode}
import models.{Coordinates, DynamicAddress, LocationOfGoodsIdentification, LocationType, Mode, PostalCodeAddress, UserAnswers}
import pages.routeDetails.locationOfGoods._
import play.api.mvc.Call

sealed trait LocationOfGoodsDomain extends JourneyDomainModel {

  val typeOfLocation: LocationType

  val qualifierOfIdentification: LocationOfGoodsIdentification

  override def routeIfCompleted(userAnswers: UserAnswers, mode: Mode, stage: Stage): Option[Call] =
    Some(controllers.routeDetails.locationOfGoods.routes.CheckYourAnswersController.onPageLoad(userAnswers.lrn, mode))
}

object LocationOfGoodsDomain {

  implicit val userAnswersReader: UserAnswersReader[LocationOfGoodsDomain] =
    LocationTypePage.reader.flatMap {
      typeOfLocation =>
        IdentificationPage.reader.flatMap {
          case CustomsOfficeIdentifier => LocationOfGoodsV.userAnswersReader(typeOfLocation)
          case EoriNumber              => LocationOfGoodsX.userAnswersReader(typeOfLocation)
          case AuthorisationNumber     => LocationOfGoodsY.userAnswersReader(typeOfLocation)
          case UnlocodeIdentifier      => LocationOfGoodsU.userAnswersReader(typeOfLocation)
          case CoordinatesIdentifier   => LocationOfGoodsW.userAnswersReader(typeOfLocation)
          case AddressIdentifier       => LocationOfGoodsZ.userAnswersReader(typeOfLocation)
          case PostalCode              => LocationOfGoodsT.userAnswersReader(typeOfLocation)
        }
    }

  case class LocationOfGoodsV(
    typeOfLocation: LocationType,
    customsOffice: CustomsOffice
  ) extends LocationOfGoodsDomain {

    override val qualifierOfIdentification: LocationOfGoodsIdentification = CustomsOfficeIdentifier
  }

  object LocationOfGoodsV {

    def userAnswersReader(typeOfLocation: LocationType): UserAnswersReader[LocationOfGoodsDomain] =
      (
        UserAnswersReader(typeOfLocation),
        CustomsOfficeIdentifierPage.reader
      ).tupled.map((LocationOfGoodsV.apply _).tupled)
  }

  case class LocationOfGoodsX(
    typeOfLocation: LocationType,
    identificationNumber: String,
    additionalIdentifier: Option[String],
    additionalContact: Option[AdditionalContactDomain]
  ) extends LocationOfGoodsDomain {

    override val qualifierOfIdentification: LocationOfGoodsIdentification = EoriNumber
  }

  object LocationOfGoodsX {

    def userAnswersReader(typeOfLocation: LocationType): UserAnswersReader[LocationOfGoodsDomain] =
      (
        UserAnswersReader(typeOfLocation),
        EoriPage.reader,
        AddIdentifierYesNoPage.filterOptionalDependent(identity)(AdditionalIdentifierPage.reader),
        AddContactYesNoPage.filterOptionalDependent(identity)(UserAnswersReader[AdditionalContactDomain])
      ).tupled.map((LocationOfGoodsX.apply _).tupled)
  }

  case class LocationOfGoodsY(
    typeOfLocation: LocationType,
    authorisationNumber: String,
    additionalIdentifier: Option[String],
    additionalContact: Option[AdditionalContactDomain]
  ) extends LocationOfGoodsDomain {

    override val qualifierOfIdentification: LocationOfGoodsIdentification = AuthorisationNumber
  }

  object LocationOfGoodsY {

    def userAnswersReader(typeOfLocation: LocationType): UserAnswersReader[LocationOfGoodsDomain] =
      (
        UserAnswersReader(typeOfLocation),
        AuthorisationNumberPage.reader,
        AddIdentifierYesNoPage.filterOptionalDependent(identity)(AdditionalIdentifierPage.reader),
        AddContactYesNoPage.filterOptionalDependent(identity)(UserAnswersReader[AdditionalContactDomain])
      ).tupled.map((LocationOfGoodsY.apply _).tupled)
  }

  case class LocationOfGoodsW(
    typeOfLocation: LocationType,
    coordinates: Coordinates,
    additionalContact: Option[AdditionalContactDomain]
  ) extends LocationOfGoodsDomain {

    override val qualifierOfIdentification: LocationOfGoodsIdentification = CoordinatesIdentifier
  }

  object LocationOfGoodsW {

    def userAnswersReader(typeOfLocation: LocationType): UserAnswersReader[LocationOfGoodsDomain] =
      (
        UserAnswersReader(typeOfLocation),
        CoordinatesPage.reader,
        AddContactYesNoPage.filterOptionalDependent(identity)(UserAnswersReader[AdditionalContactDomain])
      ).tupled.map((LocationOfGoodsW.apply _).tupled)
  }

  case class LocationOfGoodsZ(
    typeOfLocation: LocationType,
    country: Country,
    address: DynamicAddress,
    additionalContact: Option[AdditionalContactDomain]
  ) extends LocationOfGoodsDomain {

    override val qualifierOfIdentification: LocationOfGoodsIdentification = AddressIdentifier
  }

  object LocationOfGoodsZ {

    def userAnswersReader(typeOfLocation: LocationType): UserAnswersReader[LocationOfGoodsDomain] =
      (
        UserAnswersReader(typeOfLocation),
        CountryPage.reader,
        AddressPage.reader,
        AddContactYesNoPage.filterOptionalDependent(identity)(UserAnswersReader[AdditionalContactDomain])
      ).tupled.map((LocationOfGoodsZ.apply _).tupled)
  }

  case class LocationOfGoodsU(
    typeOfLocation: LocationType,
    unLocode: UnLocode,
    additionalContact: Option[AdditionalContactDomain]
  ) extends LocationOfGoodsDomain {

    override val qualifierOfIdentification: LocationOfGoodsIdentification = UnlocodeIdentifier
  }

  object LocationOfGoodsU {

    def userAnswersReader(typeOfLocation: LocationType): UserAnswersReader[LocationOfGoodsDomain] =
      (
        UserAnswersReader(typeOfLocation),
        UnLocodePage.reader,
        AddContactYesNoPage.filterOptionalDependent(identity)(UserAnswersReader[AdditionalContactDomain])
      ).tupled.map((LocationOfGoodsU.apply _).tupled)
  }

  case class LocationOfGoodsT(
    typeOfLocation: LocationType,
    postalCodeAddress: PostalCodeAddress,
    additionalContact: Option[AdditionalContactDomain]
  ) extends LocationOfGoodsDomain {

    override val qualifierOfIdentification: LocationOfGoodsIdentification = PostalCode
  }

  object LocationOfGoodsT {

    def userAnswersReader(typeOfLocation: LocationType): UserAnswersReader[LocationOfGoodsDomain] =
      (
        UserAnswersReader(typeOfLocation),
        PostalCodePage.reader,
        AddContactYesNoPage.filterOptionalDependent(identity)(UserAnswersReader[AdditionalContactDomain])
      ).tupled.map((LocationOfGoodsT.apply _).tupled)
  }

}
