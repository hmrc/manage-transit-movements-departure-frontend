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
import pages.routeDetails.locationOfGoods._
import play.api.mvc.Call

sealed trait LocationOfGoodsDomain extends JourneyDomainModel {

  val typeOfLocation: LocationType

  val qualifierOfIdentification: LocationOfGoodsIdentification

  override def routeIfCompleted(userAnswers: UserAnswers, stage: Stage): Option[Call] =
    None // TODO - update to CYA once built
}

object LocationOfGoodsDomain {

  implicit val userAnswersReader: UserAnswersReader[LocationOfGoodsDomain] =
    LocationOfGoodsTypePage.reader.flatMap {
      typeOfLocation =>
        LocationOfGoodsIdentificationPage.reader.flatMap {
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
        LocationOfGoodsCustomsOfficeIdentifierPage.reader
      ).mapN {
        (typeOfLocation, customsOffice) =>
          LocationOfGoodsV(typeOfLocation, customsOffice)
      }
  }

  case class LocationOfGoodsX(
    typeOfLocation: LocationType,
    identificationNumber: String,
    additionalIdentifier: Option[String]
  ) extends LocationOfGoodsDomain {

    override val qualifierOfIdentification: LocationOfGoodsIdentification = EoriNumber
  }

  object LocationOfGoodsX {

    def userAnswersReader(typeOfLocation: LocationType): UserAnswersReader[LocationOfGoodsDomain] =
      (
        UserAnswersReader(typeOfLocation),
        LocationOfGoodsEoriPage.reader,
        LocationOfGoodsAddIdentifierPage.filterOptionalDependent(identity)(UserAnswersReader(""))
      ).mapN {
        (typeOfLocation, identificationNumber, additionalIdentifier) =>
          LocationOfGoodsX(typeOfLocation, identificationNumber, additionalIdentifier)
      }
  }

  case class LocationOfGoodsY(
    typeOfLocation: LocationType,
    authorisationNumber: String,
    additionalIdentifier: Option[String]
  ) extends LocationOfGoodsDomain {

    override val qualifierOfIdentification: LocationOfGoodsIdentification = AuthorisationNumber
  }

  object LocationOfGoodsY {

    def userAnswersReader(typeOfLocation: LocationType): UserAnswersReader[LocationOfGoodsDomain] =
      (
        UserAnswersReader(typeOfLocation),
        LocationOfGoodsAuthorisationNumberPage.reader,
        LocationOfGoodsAddIdentifierPage.filterOptionalDependent(identity)(UserAnswersReader(""))
      ).mapN {
        (typeOfLocation, authorisationNumber, additionalIdentifier) =>
          LocationOfGoodsY(typeOfLocation, authorisationNumber, additionalIdentifier)
      }
  }

  case class LocationOfGoodsW(
    typeOfLocation: LocationType,
    coordinates: Coordinates
  ) extends LocationOfGoodsDomain {

    override val qualifierOfIdentification: LocationOfGoodsIdentification = CoordinatesIdentifier
  }

  object LocationOfGoodsW {

    def userAnswersReader(typeOfLocation: LocationType): UserAnswersReader[LocationOfGoodsDomain] =
      (
        UserAnswersReader(typeOfLocation),
        LocationOfGoodsCoordinatesPage.reader
      ).mapN {
        (typeOfLocation, coordinates) =>
          LocationOfGoodsW(typeOfLocation, coordinates)
      }
  }

  case class LocationOfGoodsZ(
    typeOfLocation: LocationType,
    address: Address
  ) extends LocationOfGoodsDomain {

    override val qualifierOfIdentification: LocationOfGoodsIdentification = AddressIdentifier
  }

  object LocationOfGoodsZ {

    def userAnswersReader(typeOfLocation: LocationType): UserAnswersReader[LocationOfGoodsDomain] =
      (
        UserAnswersReader(typeOfLocation),
        LocationOfGoodsAddressPage.reader
      ).mapN {
        (typeOfLocation, address) =>
          LocationOfGoodsZ(typeOfLocation, address)
      }
  }

  case class LocationOfGoodsU(
    typeOfLocation: LocationType,
    unLocode: UnLocode
  ) extends LocationOfGoodsDomain {

    override val qualifierOfIdentification: LocationOfGoodsIdentification = UnlocodeIdentifier
  }

  object LocationOfGoodsU {

    def userAnswersReader(typeOfLocation: LocationType): UserAnswersReader[LocationOfGoodsDomain] =
      (
        UserAnswersReader(typeOfLocation),
        LocationOfGoodsUnLocodePage.reader
      ).mapN {
        (typeOfLocation, unLocode) =>
          LocationOfGoodsU(typeOfLocation, unLocode)
      }
  }

  case class LocationOfGoodsT(
    typeOfLocation: LocationType,
    postalCodeAddress: PostalCodeAddress
  ) extends LocationOfGoodsDomain {

    override val qualifierOfIdentification: LocationOfGoodsIdentification = PostalCode
  }

  object LocationOfGoodsT {

    def userAnswersReader(typeOfLocation: LocationType): UserAnswersReader[LocationOfGoodsDomain] =
      (
        UserAnswersReader(typeOfLocation),
        LocationOfGoodsPostalCodePage.reader
      ).mapN {
        (typeOfLocation, postalCodeAddress) =>
          LocationOfGoodsT(typeOfLocation, postalCodeAddress)
      }
  }

}
