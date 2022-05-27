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

package models.journeyDomain

import cats.data.ReaderT
import cats.implicits._
import models.DeclarationType.Option4
import models.ProcedureType.Normal
import models.domain._
import models.reference.CustomsOffice
import models.{DeclarationType, LocalReferenceNumber, ProcedureType, SecurityDetailsType, UserAnswers}
import pages.preTaskList._

case class PreTaskListDomain(
  localReferenceNumber: LocalReferenceNumber,
  officeOfDeparture: CustomsOffice,
  procedureType: ProcedureType,
  declarationType: DeclarationType,
  tirCarnetReference: Option[String],
  securityDetailsType: SecurityDetailsType,
  detailsConfirmed: Boolean
)

object PreTaskListDomain {

  private val localReferenceNumber: UserAnswersReader[LocalReferenceNumber] =
    ReaderT[EitherType, UserAnswers, LocalReferenceNumber](
      ua => Right(ua.lrn)
    )

  private val tirCarnetReference: UserAnswersReader[Option[String]] =
    ProcedureTypePage
      .filterOptionalDependent(_ == Normal) {
        DeclarationTypePage.filterOptionalDependent(_ == Option4) {
          TIRCarnetReferencePage.reader
        }
      }
      .map(_.flatten)

  implicit val reader: UserAnswersReader[PreTaskListDomain] =
    (
      localReferenceNumber,
      OfficeOfDeparturePage.reader,
      ProcedureTypePage.reader,
      DeclarationTypePage.reader,
      tirCarnetReference,
      SecurityDetailsTypePage.reader,
      DetailsConfirmedPage.mandatoryReader(identity)
    ).tupled.map((PreTaskListDomain.apply _).tupled)

}
