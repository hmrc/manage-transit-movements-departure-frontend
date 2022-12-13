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
import controllers.transport.transportMeans.routes
import models.SecurityDetailsType.NoSecurityDetails
import models.domain.{GettableAsFilterForNextReaderOps, GettableAsReaderOps, JsArrayGettableAsReaderOps, UserAnswersReader}
import models.journeyDomain.{JourneyDomainModel, Stage}
import models.transport.transportMeans.departure.InlandMode
import models.{Index, Mode, RichJsArray, UserAnswers}
import pages.preTaskList.SecurityDetailsTypePage
import pages.sections.transport.TransportMeansActiveListSection
import pages.transport.transportMeans.AnotherVehicleCrossingYesNoPage
import pages.transport.transportMeans.departure.InlandModePage
import play.api.mvc.Call

sealed trait TransportMeansDomain extends JourneyDomainModel {
  val inlandMode: InlandMode

  override def routeIfCompleted(userAnswers: UserAnswers, mode: Mode, stage: Stage): Option[Call] =
    Option(routes.TransportMeansCheckYourAnswersController.onPageLoad(userAnswers.lrn, mode))
}

object TransportMeansDomain {

  implicit val userAnswersReader: UserAnswersReader[TransportMeansDomain] =
    InlandModePage.reader.flatMap {
      case InlandMode.Mail =>
        UserAnswersReader(TransportMeansDomainWithMailInlandMode).widen[TransportMeansDomain]
      case x =>
        UserAnswersReader[TransportMeansDomainWithOtherInlandMode](
          TransportMeansDomainWithOtherInlandMode.userAnswersReader(x)
        ).widen[TransportMeansDomain]
    }
}

case object TransportMeansDomainWithMailInlandMode extends TransportMeansDomain {
  override val inlandMode: InlandMode = InlandMode.Mail
}

case class TransportMeansDomainWithOtherInlandMode(
  override val inlandMode: InlandMode,
  transportMeansDeparture: TransportMeansDepartureDomain,
  transportMeansActive: Option[Seq[TransportMeansActiveDomain]]
) extends TransportMeansDomain

object TransportMeansDomainWithOtherInlandMode {

  implicit def userAnswersReader(inlandMode: InlandMode): UserAnswersReader[TransportMeansDomainWithOtherInlandMode] = {

    val arrayReader: UserAnswersReader[Seq[TransportMeansActiveDomain]] = TransportMeansActiveListSection.arrayReader.flatMap {
      case x if x.isEmpty =>
        UserAnswersReader[TransportMeansActiveDomain](TransportMeansActiveDomain.userAnswersReader(Index(0))).map(Seq(_))
      case x =>
        x.traverse[TransportMeansActiveDomain](TransportMeansActiveDomain.userAnswersReader)
    }

    implicit val transportMeansActiveReader: UserAnswersReader[Option[Seq[TransportMeansActiveDomain]]] =
      SecurityDetailsTypePage.reader.flatMap {
        case NoSecurityDetails => AnotherVehicleCrossingYesNoPage.filterOptionalDependent(identity)(arrayReader)
        case _                 => arrayReader.map(Some(_))
      }

    (
      UserAnswersReader(inlandMode),
      UserAnswersReader[TransportMeansDepartureDomain],
      UserAnswersReader[Option[Seq[TransportMeansActiveDomain]]]
    ).tupled.map((TransportMeansDomainWithOtherInlandMode.apply _).tupled)
  }
}
