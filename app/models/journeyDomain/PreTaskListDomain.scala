/*
 * Copyright 2024 HM Revenue & Customs
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

import config.Constants.CountryCode.*
import models.ProcedureType.Normal
import models.reference.{CustomsOffice, DeclarationType, SecurityType}
import models.{LocalReferenceNumber, ProcedureType, UserAnswers}
import pages.preTaskList.*
import pages.sections.{PreTaskListSection, Section}

case class PreTaskListDomain(
  localReferenceNumber: LocalReferenceNumber,
  additionalDeclarationType: String,
  officeOfDeparture: CustomsOffice,
  procedureType: ProcedureType,
  declarationType: DeclarationType,
  tirCarnetReference: Option[String],
  securityDetailsType: SecurityType
) extends JourneyDomainModel {

  override def page: Option[Section[?]] = Some(PreTaskListSection)
}

object PreTaskListDomain {

  private val localReferenceNumberReader: Read[LocalReferenceNumber] =
    UserAnswersReader.success {
      (ua: UserAnswers) => ua.lrn
    }

  private val tirCarnetReferenceReader: Read[Option[String]] =
    OfficeOfDeparturePage.reader
      .to {
        _.countryId match {
          case XI =>
            ProcedureTypePage
              .filterOptionalDependent(_ == Normal) {
                DeclarationTypePage.filterOptionalDependent(_.isTIR) {
                  TIRCarnetReferencePage.reader
                }
              }
              .flatten
          case _ =>
            DeclarationTypePage.filterMandatoryDependent(!_.isTIR) {
              UserAnswersReader.none
            }
        }
      }

  private def standardDeclarationReader(preLodgeFlag: Boolean): Read[String] = if (preLodgeFlag) {
    AdditionalDeclarationTypePage.reader.to(
      x => UserAnswersReader.success(x.code)
    )
  } else {
    StandardDeclarationPage.reader

  }

  implicit def reader(preLodgeFlag: Boolean): UserAnswersReader[PreTaskListDomain] =
    (
      localReferenceNumberReader,
      standardDeclarationReader(preLodgeFlag),
      OfficeOfDeparturePage.reader,
      ProcedureTypePage.reader,
      DeclarationTypePage.reader,
      tirCarnetReferenceReader,
      SecurityDetailsTypePage.reader
    ).map(PreTaskListDomain.apply).apply(Nil)

}
