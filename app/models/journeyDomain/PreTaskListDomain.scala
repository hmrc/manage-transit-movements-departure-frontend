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

import cats.implicits._
import config.Constants.XI
import models.DeclarationType.Option4
import models.ProcedureType.Normal
import models.domain._
import models.reference.CustomsOffice
import models.{DeclarationType, LocalReferenceNumber, Mode, ProcedureType, SecurityDetailsType, UserAnswers}
import pages.preTaskList._
import play.api.mvc.Call

case class PreTaskListDomain(
  localReferenceNumber: LocalReferenceNumber,
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
            DeclarationTypePage.filterOptionalDependent(_ == Option4) {
              TIRCarnetReferencePage.reader
            }
          }
          .map(_.flatten)
      case _ =>
        DeclarationTypePage.filterMandatoryDependent(_ != Option4) {
          none[String].pure[UserAnswersReader]
        }
    }

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

  import play.api.libs.json._
  import play.api.libs.functional.syntax._

  val ignore = OWrites[Any](
    _ => Json.obj()
  )

  // New writes for transformation
  implicit val preTaskWrites: Writes[PreTaskListDomain] = (
    (JsPath \ "TransitOperation" \ "LRN").write[LocalReferenceNumber] and
      (JsPath \ "CustomsOfficeOfDeparture" \ "referenceNumber")
        .write[String]
        .contramap(
          (x: CustomsOffice) => x.id
        ) and
      ignore and
      (JsPath \ "TransitOperation" \ "declarationType").write[DeclarationType] and
      (JsPath \ "TransitOperation" \ "TIRCarnet").writeNullable[String] and
      (JsPath \ "TransitOperation" \ "security").write[SecurityDetailsType] and
      ignore
  )(unlift(PreTaskListDomain.unapply))

  // There is more than one way to skin a cat...

//  implicit val preTaskWrites2: Writes[PreTaskListDomain] = (
//    (__ \ "TransitOperation" \ "LRN").write[LocalReferenceNumber] and
//      (__ \ "CustomsOfficeOfDeparture" \ "referenceNumber").write[String] and
//      (__ \ "TransitOperation" \ "declarationType").write[DeclarationType] and
//      (__ \ "TransitOperation" \ "security").write[SecurityDetailsType]
//    ).apply {
//    x: PreTaskListDomain =>
//      (
//        x.localReferenceNumber,
//        x.officeOfDeparture.id,
//        x.declarationType,
//        x.securityDetailsType
//      )
//  }

//  implicit val preTaskWrites3: Writes[PreTaskListDomain] = Writes {
//    x: PreTaskListDomain =>
//      Json.parse(s"""
//                    |{
//                    |  "TransitOperation": {
//                    |    "LRN": ${Json.toJson(x.localReferenceNumber)},
//                    |    "declarationType": ${Json.toJson(x.declarationType)},
//                    |    "security": ${Json.toJson(x.securityDetailsType)}
//                    |  },
//                    |  "CustomsOfficeOfDeparture": {
//                    |    "referenceNumber": ${JsString(x.officeOfDeparture.id)}
//                    |  }
//                    |}
//                    |""".stripMargin)
//  }

}
