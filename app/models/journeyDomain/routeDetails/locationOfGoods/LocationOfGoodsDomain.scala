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
import models.domain.{GettableAsReaderOps, UserAnswersReader}
import models.journeyDomain.{JourneyDomainModel, Stage}
import models.reference.CustomsOffice
import models.{Coordinates, LocationOfGoodsIdentification, LocationType, UserAnswers}
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
          case CoordinatesIdentifier   => LocationOfGoodsW.userAnswersReader(typeOfLocation)
          case Unlocode                => ???
          case Address                 => ???
          case PostalCode              => ???
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
    identificationNumber: String
  ) extends LocationOfGoodsDomain {

    override val qualifierOfIdentification: LocationOfGoodsIdentification = EoriNumber
  }

  object LocationOfGoodsX {

    def userAnswersReader(typeOfLocation: LocationType): UserAnswersReader[LocationOfGoodsDomain] =
      (
        UserAnswersReader(typeOfLocation),
        LocationOfGoodsEoriPage.reader
      ).mapN {
        (typeOfLocation, identificationNumber) =>
          LocationOfGoodsX(typeOfLocation, identificationNumber)
      }
  }

  case class LocationOfGoodsY(
    typeOfLocation: LocationType,
    authorisationNumber: String
  ) extends LocationOfGoodsDomain {

    override val qualifierOfIdentification: LocationOfGoodsIdentification = AuthorisationNumber
  }

  object LocationOfGoodsY {

    def userAnswersReader(typeOfLocation: LocationType): UserAnswersReader[LocationOfGoodsDomain] =
      (
        UserAnswersReader(typeOfLocation),
        LocationOfGoodsAuthorisationNumberPage.reader
      ).mapN {
        (typeOfLocation, authorisationNumber) =>
          LocationOfGoodsY(typeOfLocation, authorisationNumber)
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
}
