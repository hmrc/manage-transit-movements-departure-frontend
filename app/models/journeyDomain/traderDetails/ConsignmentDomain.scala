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
import models.SecurityDetailsType.NoSecurityDetails
import models.domain.{GettableAsFilterForNextReaderOps, UserAnswersReader}
import pages.preTaskList.SecurityDetailsTypePage
import pages.traderDetails.consignment._

case class ConsignmentDomain(
  consignor: Option[ConsignmentConsignorDomain],
  consignee: Option[ConsignmentConsigneeDomain]
)

object ConsignmentDomain {

  implicit val userAnswersReader: UserAnswersReader[ConsignmentDomain] = {
    lazy val consignorDomain = UserAnswersReader[ConsignmentConsignorDomain]
    for {
      consignorApprovedDomain <- ApprovedOperatorPage.filterOptionalDependent(!_)(consignorDomain)
      consignorSecurityDomain <- SecurityDetailsTypePage.filterOptionalDependent(_ != NoSecurityDetails)(consignorDomain)
      consigneeDomain         <- consignee.MoreThanOneConsigneePage.filterOptionalDependent(!_)(UserAnswersReader[ConsignmentConsigneeDomain])
    } yield ConsignmentDomain(consignorApprovedDomain orElse consignorSecurityDomain, consigneeDomain)

  }
}
