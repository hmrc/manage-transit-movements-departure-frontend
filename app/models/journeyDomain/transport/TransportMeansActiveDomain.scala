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
import models.{Index, Mode, UserAnswers}
import models.SecurityDetailsType.NoSecurityDetails
import models.domain.{GettableAsFilterForNextReaderOps, GettableAsReaderOps, UserAnswersReader}
import models.journeyDomain.Stage.{AccessingJourney, CompletingJourney}
import models.journeyDomain.{JourneyDomainModel, Stage}
import models.reference.{CustomsOffice, Nationality}
import models.transport.transportMeans.active.Identification
import models.transport.transportMeans.departure.InlandMode
import models.transport.transportMeans.departure.InlandMode.Air
import pages.preTaskList.SecurityDetailsTypePage
import pages.transport.transportMeans.active._
import pages.transport.transportMeans.departure.InlandModePage
import play.api.i18n.Messages
import play.api.mvc.Call

case class TransportMeansActiveDomain(
  identification: Identification,
  identificationNumber: String,
  nationality: Option[Nationality],
  customsOffice: CustomsOffice,
  conveyanceReferenceNumber: Option[String]
) extends JourneyDomainModel {

  def asString(implicit messages: Messages): String =
    TransportMeansActiveDomain.asString(identification, identificationNumber)

  override def routeIfCompleted(userAnswers: UserAnswers, mode: Mode, stage: Stage): Option[Call] = Some {
    stage match {
      case AccessingJourney =>
        controllers.transport.transportMeans.routes.AnotherVehicleCrossingYesNoController.onPageLoad(userAnswers.lrn, mode)
      case CompletingJourney =>
        controllers.transport.transportMeans.active.routes.AddAnotherBorderTransportController.onPageLoad(userAnswers.lrn, mode)
    }
  }
}

object TransportMeansActiveDomain {

  def asString(identification: Identification, identificationNumber: String)(implicit messages: Messages): String =
    messages(s"transport.transportMeans.active.identification.${identification.toString}").concat(s" - $identificationNumber")

  def conveyanceReads(index: Index): UserAnswersReader[Option[String]] = {

    val details = for {
      securityDetails <- SecurityDetailsTypePage.reader
      inlandMode      <- InlandModePage.reader
    } yield (securityDetails, inlandMode)

    details.flatMap {
      case (NoSecurityDetails, inlandMode: InlandMode) if inlandMode != Air =>
        ConveyanceReferenceNumberYesNoPage(index).filterOptionalDependent(identity)(ConveyanceReferenceNumberPage(index).reader)
      case _ => ConveyanceReferenceNumberPage(index).reader.map(Some(_))
    }
  }

  def userAnswersReader(index: Index): UserAnswersReader[TransportMeansActiveDomain] =
    (
      IdentificationPage(index).reader,
      IdentificationNumberPage(index).reader,
      AddNationalityYesNoPage(index).filterOptionalDependent(identity)(NationalityPage(index).reader),
      CustomsOfficeActiveBorderPage(index).reader,
      conveyanceReads(index)
    ).tupled.map((TransportMeansActiveDomain.apply _).tupled)
}
