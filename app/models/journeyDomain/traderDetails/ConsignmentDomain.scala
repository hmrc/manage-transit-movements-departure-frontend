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

package models.journeyDomain.traderDetails

import cats.implicits._
import models.DeclarationType.Option4
import models.SecurityDetailsType.NoSecurityDetails
import models.domain.{GettableAsFilterForNextReaderOps, GettableAsReaderOps, UserAnswersReader}
import pages.preTaskList.{DeclarationTypePage, SecurityDetailsTypePage}
import pages.traderDetails.consignment._

case class ConsignmentDomain(
  consignor: Option[ConsignmentConsignorDomain],
  consignee: Option[ConsignmentConsigneeDomain]
)

object ConsignmentDomain {

  implicit val userAnswersReader: UserAnswersReader[ConsignmentDomain] =
    for {
      consignor <- readConsignorDomain
      consignee <- readConsigneeDomain
    } yield ConsignmentDomain(consignor, consignee)

  private def readConsignorDomain: UserAnswersReader[Option[ConsignmentConsignorDomain]] = {
    lazy val consignorReader: UserAnswersReader[Option[ConsignmentConsignorDomain]] =
      UserAnswersReader[ConsignmentConsignorDomain].map(Some(_))

    DeclarationTypePage.reader.flatMap {
      case Option4 => consignorReader
      case _ =>
        ApprovedOperatorPage.reader.flatMap {
          case true =>
            SecurityDetailsTypePage.reader.flatMap {
              case NoSecurityDetails => none[ConsignmentConsignorDomain].pure[UserAnswersReader]
              case _                 => consignorReader
            }
          case false => consignorReader
        }
    }
  }

  private def readConsigneeDomain: UserAnswersReader[Option[ConsignmentConsigneeDomain]] =
    consignee.MoreThanOneConsigneePage.filterOptionalDependent(!_)(UserAnswersReader[ConsignmentConsigneeDomain])
}
