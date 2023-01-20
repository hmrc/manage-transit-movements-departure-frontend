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

import models.domain.UserAnswersReader
import models.journeyDomain.guaranteeDetails.GuaranteeDetailsDomain
import models.journeyDomain.routeDetails.RouteDetailsDomain
import models.journeyDomain.traderDetails.TraderDetailsDomain
import models.journeyDomain.transport.TransportDomain

case class DepartureDomain(
  preTaskList: PreTaskListDomain,
  traderDetails: TraderDetailsDomain,
  routeDetails: RouteDetailsDomain,
  guaranteeDetails: GuaranteeDetailsDomain,
  transportDetails: TransportDomain
)

object DepartureDomain {

  def userAnswersReader(ctcCountryCode: Seq[String], customsSecurityAgreement: Seq[String]): UserAnswersReader[DepartureDomain] =
    for {
      preTaskListDomain   <- UserAnswersReader[PreTaskListDomain]
      routeDetailsDomain  <- RouteDetailsDomain.userAnswersReader(ctcCountryCode, customsSecurityAgreement)
      traderDetailsDomain <- UserAnswersReader[TraderDetailsDomain]
      guaranteeDomain     <- UserAnswersReader[GuaranteeDetailsDomain]
      transportDomain     <- UserAnswersReader[TransportDomain]
    } yield DepartureDomain(preTaskListDomain, traderDetailsDomain, routeDetailsDomain, guaranteeDomain, transportDomain)
}
