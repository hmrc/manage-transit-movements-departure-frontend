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

package models.journeyDomain.transport

import cats.implicits._
import models.Index
import models.SecurityDetailsType.NoSecurityDetails
import models.domain.{GettableAsFilterForNextReaderOps, GettableAsReaderOps, UserAnswersReader}
import models.journeyDomain.JourneyDomainModel
import models.reference.{CustomsOffice, Nationality}
import models.transport.transportMeans.BorderModeOfTransport
import models.transport.transportMeans.active.Identification
import models.transport.transportMeans.BorderModeOfTransport.Air
import pages.preTaskList.SecurityDetailsTypePage
import pages.transport.transportMeans.BorderModeOfTransportPage
import pages.transport.transportMeans.active._

sealed trait TransportMeansActiveDomain extends JourneyDomainModel {
  val borderMode: BorderModeOfTransport
  val identification: Identification
}

object TransportMeansActiveDomain {

  implicit val userAnswersReader: UserAnswersReader[TransportMeansActiveDomain] =
    BorderModeOfTransportPage.reader.flatMap {
      case BorderModeOfTransport.Rail => UserAnswersReader[TransportMeansDomainWithRailBorderMode].widen[TransportMeansActiveDomain]
      case BorderModeOfTransport.Road => UserAnswersReader[TransportMeansDomainWithRoadBorderMode].widen[TransportMeansActiveDomain]
      case _                          => UserAnswersReader[TransportMeansDomainWithAnyOtherBorderMode]
    }
}

case class TransportMeansDomainWithRailBorderMode(
  identificationNumber: String,
  nationality: Nationality
) extends TransportMeansActiveDomain {
  override val borderMode: BorderModeOfTransport = BorderModeOfTransport.Rail
  override val identification: Identification    = Identification.TrainNumber
}

object TransportMeansDomainWithRailBorderMode {

  def conveyanceReads(index: Index): UserAnswersReader[Option[String]] = {

    val details = for {
      securityDetails <- SecurityDetailsTypePage.reader
      borderMode      <- BorderModeOfTransportPage.reader
    } yield (securityDetails, borderMode)

    details.flatMap {
      case (NoSecurityDetails, x: BorderModeOfTransport) if x != Air =>
        ConveyanceReferenceNumberYesNoPage(index).filterOptionalDependent(identity)(ConveyanceReferenceNumberPage(index).reader)
      case _ => ConveyanceReferenceNumberPage(index).reader.map(Some(_))
    }
  }

  implicit def userAnswersReader(index: Index): UserAnswersReader[TransportMeansDomainWithRailBorderMode] =
    (
      IdentificationNumberPage(index).reader,
      AddNationalityYesNoPage(index).filterOptionalDependent(identity)(NationalityPage(index).reader),
      CustomsOfficeActiveBorderPage(index).reader,
      conveyanceReads(index)
    ).tupled.map((TransportMeansDomainWithRailBorderMode.apply _).tupled)
}

case class TransportMeansDomainWithRoadBorderMode(
  identificationNumber: String,
  nationality: Nationality
) extends TransportMeansActiveDomain {
  override val borderMode: BorderModeOfTransport = BorderModeOfTransport.Road
  override val identification: Identification    = Identification.RegNumberRoadVehicle
}

object TransportMeansDomainWithRoadBorderMode {

  def conveyanceReads(index: Index): UserAnswersReader[Option[String]] = {

    val details = for {
      securityDetails <- SecurityDetailsTypePage.reader
      borderMode      <- BorderModeOfTransportPage.reader
    } yield (securityDetails, borderMode)

    details.flatMap {
      case (NoSecurityDetails, x: BorderModeOfTransport) if x != Air =>
        ConveyanceReferenceNumberYesNoPage(index).filterOptionalDependent(identity)(ConveyanceReferenceNumberPage(index).reader)
      case _ => ConveyanceReferenceNumberPage(index).reader.map(Some(_))
    }
  }

  implicit def userAnswersReader(index: Index): UserAnswersReader[TransportMeansDomainWithRoadBorderMode] =
    (
      IdentificationNumberPage(index).reader,
      AddNationalityYesNoPage(index).filterOptionalDependent(identity)(NationalityPage(index).reader),
      CustomsOfficeActiveBorderPage(index).reader,
      conveyanceReads(index)
    ).tupled.map((TransportMeansDomainWithRoadBorderMode.apply _).tupled)
}

case class TransportMeansDomainWithAnyOtherBorderMode(
  identification: Identification,
  identificationNumber: String,
  nationality: Option[Nationality],
  customsOffice: CustomsOffice,
  conveyanceReferenceNumber: Option[String]
)

object TransportMeansDomainWithAnyOtherBorderMode {

  def conveyanceReads(index: Index): UserAnswersReader[Option[String]] = {

    val details = for {
      securityDetails <- SecurityDetailsTypePage.reader
      borderMode      <- BorderModeOfTransportPage.reader
    } yield (securityDetails, borderMode)

    details.flatMap {
      case (NoSecurityDetails, x: BorderModeOfTransport) if x != Air =>
        ConveyanceReferenceNumberYesNoPage(index).filterOptionalDependent(identity)(ConveyanceReferenceNumberPage(index).reader)
      case _ => ConveyanceReferenceNumberPage(index).reader.map(Some(_))
    }
  }

  def identificationReads(index: Index): UserAnswersReader[Identification] =
    BorderModeOfTransportPage.reader.flatMap {
      _ => IdentificationPage(index).reader
    }

  def userAnswersReader(index: Index): UserAnswersReader[TransportMeansActiveDomain] =
    (
      identificationReads(index),
      IdentificationNumberPage(index).reader,
      AddNationalityYesNoPage(index).filterOptionalDependent(identity)(NationalityPage(index).reader),
      CustomsOfficeActiveBorderPage(index).reader,
      conveyanceReads(index)
    ).tupled.map((TransportMeansDomainWithAnyOtherBorderMode.apply _).tupled)
}
