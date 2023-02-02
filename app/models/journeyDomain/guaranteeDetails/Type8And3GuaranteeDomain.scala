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

package models.journeyDomain.guaranteeDetails

import cats.implicits._
import models.Index
import models.domain.{GettableAsReaderOps, UserAnswersReader}
import models.journeyDomain.JourneyDomainModel
import models.reference.CurrencyCode
import pages.guaranteeDetails.guarantee.{CurrencyPage, LiabilityAmountPage, OtherReferencePage}

case class Type8And3GuaranteeDomain(otherReference: String, currency: CurrencyCode, liabilityAmount: BigDecimal) extends JourneyDomainModel

object Type8And3GuaranteeDomain {

  def userAnswersReader(index: Index): UserAnswersReader[Type8And3GuaranteeDomain] =
    (
      OtherReferencePage(index).reader,
      CurrencyPage(index).reader,
      LiabilityAmountPage(index).reader
    ).tupled.map((Type8And3GuaranteeDomain.apply _).tupled)
}
