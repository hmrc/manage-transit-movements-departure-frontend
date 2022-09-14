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

package models.journeyDomain.routeDetails.locationOfGoods

import cats.implicits._
import models.LocationOfGoodsIdentification._
import models.domain.{GettableAsFilterForNextReaderOps, GettableAsReaderOps, UserAnswersReader}
import models.journeyDomain.{JourneyDomainModel, Stage}
import models.reference.{CustomsOffice, UnLocode}
import models.{Address, Coordinates, LocationOfGoodsIdentification, LocationType, PostalCodeAddress, UserAnswers}
import pages.routeDetails.locationOfGoods.{AddContactYesNoPage, _}
import play.api.mvc.Call

sealed trait LocationOfGoodsDomain extends JourneyDomainModel {

  val typeOfLocation: LocationType

  val qualifierOfIdentification: LocationOfGoodsIdentification

  override def routeIfCompleted(userAnswers: UserAnswers, stage: Stage): Option[Call] =
    Some(controllers.routeDetails.locationOfGoods.routes.CheckYourAnswersController.onPageLoad(userAnswers.lrn))
}

object LocationOfGoodsDomain {

  implicit val userAnswersReader: UserAnswersReader[LocationOfGoodsDomain] =
    LocationOfGoodsTypePage.reader.flatMap {
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
      ).mapN {
        (typeOfLocation, customsOffice) =>
          LocationOfGoodsV(typeOfLocation, customsOffice)
      }
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
      ).mapN {
        (typeOfLocation, identificationNumber, additionalIdentifier, additionalContact) =>
          LocationOfGoodsX(typeOfLocation, identificationNumber, additionalIdentifier, additionalContact)
      }
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
      ).mapN {
        (typeOfLocation, authorisationNumber, additionalIdentifier, additionalContact) =>
          LocationOfGoodsY(typeOfLocation, authorisationNumber, additionalIdentifier, additionalContact)
      }
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
      ).mapN {
        (typeOfLocation, coordinates, additionalContact) =>
          LocationOfGoodsW(typeOfLocation, coordinates, additionalContact)
      }
  }

  case class LocationOfGoodsZ(
    typeOfLocation: LocationType,
    address: Address,
    additionalContact: Option[AdditionalContactDomain]
  ) extends LocationOfGoodsDomain {

    override val qualifierOfIdentification: LocationOfGoodsIdentification = AddressIdentifier
  }

  object LocationOfGoodsZ {

    def userAnswersReader(typeOfLocation: LocationType): UserAnswersReader[LocationOfGoodsDomain] =
      (
        UserAnswersReader(typeOfLocation),
        AddressPage.reader,
        AddContactYesNoPage.filterOptionalDependent(identity)(UserAnswersReader[AdditionalContactDomain])
      ).mapN {
        (typeOfLocation, address, additionalContact) =>
          LocationOfGoodsZ(typeOfLocation, address, additionalContact)
      }
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
        LocationOfGoodsUnLocodePage.reader,
        AddContactYesNoPage.filterOptionalDependent(identity)(UserAnswersReader[AdditionalContactDomain])
      ).mapN {
        (typeOfLocation, unLocode, additionalContact) =>
          LocationOfGoodsU(typeOfLocation, unLocode, additionalContact)
      }
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
        LocationOfGoodsPostalCodePage.reader,
        AddContactYesNoPage.filterOptionalDependent(identity)(UserAnswersReader[AdditionalContactDomain])
      ).mapN {
        (typeOfLocation, postalCodeAddress, additionalContact) =>
          LocationOfGoodsT(typeOfLocation, postalCodeAddress, additionalContact)
      }
  }

}
