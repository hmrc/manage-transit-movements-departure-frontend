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

package models.journeyDomain

import cats.implicits._
import config.Constants.{TIR, XI}
import models.ProcedureType.Normal
import models.domain._
import models.reference.CustomsOffice
import models.{AdditionalDeclarationType, DeclarationType, LocalReferenceNumber, Mode, ProcedureType, SecurityDetailsType, UserAnswers}
import pages.preTaskList._
import play.api.libs.json.{Json, OFormat}
import play.api.mvc.Call

case class PreTaskListDomain(
  localReferenceNumber: LocalReferenceNumber,
  additionalDeclarationType: AdditionalDeclarationType,
  officeOfDeparture: CustomsOffice,
  procedureType: ProcedureType,
  declarationType: DeclarationType,
  tirCarnetReference: Option[String],
  securityDetailsType: SecurityDetailsType,
  detailsConfirmed: Boolean
) extends JourneyDomainModel {

  override def routeIfCompleted(userAnswers: UserAnswers, mode: Mode, stage: Stage): Option[Call] =
    Some(controllers.preTaskList.routes.CheckYourAnswersController.onPageLoad(userAnswers.lrn))
}

object PreTaskListDomain {

  private val localReferenceNumber: UserAnswersReader[LocalReferenceNumber] = {
    val fn: UserAnswers => EitherType[LocalReferenceNumber] = ua => Right(ua.lrn)
    UserAnswersReader(fn)
  }

  private val tirCarnetReference: UserAnswersReader[Option[String]] =
    OfficeOfDeparturePage.reader.map(_.countryCode).flatMap {
      case XI =>
        ProcedureTypePage
          .filterOptionalDependent(_ == Normal) {
            DeclarationTypePage.filterOptionalDependent(_.code == TIR) {
              TIRCarnetReferencePage.reader
            }
          }
          .map(_.flatten)
      case _ =>
        DeclarationTypePage.filterMandatoryDependent(_.code != TIR) {
          none[String].pure[UserAnswersReader]
        }
    }

  implicit val reader: UserAnswersReader[PreTaskListDomain] =
    (
      localReferenceNumber,
      AdditionalDeclarationTypePage.reader,
      OfficeOfDeparturePage.reader,
      ProcedureTypePage.reader,
      DeclarationTypePage.reader,
      tirCarnetReference,
      SecurityDetailsTypePage.reader,
      DetailsConfirmedPage.mandatoryReader(identity)
    ).tupled.map((PreTaskListDomain.apply _).tupled)

  implicit val format: OFormat[PreTaskListDomain] = Json.format[PreTaskListDomain]

}
