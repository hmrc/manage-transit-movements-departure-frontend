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

package models.guaranteeDetails

import config.Constants.XI
import models.{RadioModelU, UserAnswers, WithName}
import pages.preTaskList.OfficeOfDeparturePage

sealed trait GuaranteeType

object GuaranteeType extends RadioModelU[GuaranteeType] {

  override val messageKeyPrefix = "guaranteeDetails.guaranteeType"

  case object GuaranteeWaiver extends WithName("0") with GuaranteeType

  case object ComprehensiveGuarantee extends WithName("1") with GuaranteeType

  case object IndividualGuarantee extends WithName("2") with GuaranteeType

  case object CashDepositGuarantee extends WithName("3") with GuaranteeType

  case object FlatRateVoucher extends WithName("4") with GuaranteeType

  case object GuaranteeWaiverSecured extends WithName("5") with GuaranteeType

  case object GuaranteeNotRequiredExemptPublicBody extends WithName("8") with GuaranteeType

  case object IndividualGuaranteeMultiple extends WithName("9") with GuaranteeType

  case object GuaranteeWaiverByAgreement extends WithName("A") with GuaranteeType

  case object TIRGuarantee extends WithName("B") with GuaranteeType

  case object GuaranteeNotRequired extends WithName("R") with GuaranteeType

  val values: Seq[GuaranteeType] = Seq(
    GuaranteeWaiver,
    ComprehensiveGuarantee,
    IndividualGuarantee,
    CashDepositGuarantee,
    FlatRateVoucher,
    GuaranteeWaiverSecured,
    GuaranteeNotRequiredExemptPublicBody,
    IndividualGuaranteeMultiple,
    GuaranteeWaiverByAgreement,
    TIRGuarantee,
    GuaranteeNotRequired
  )

  override def valuesU(userAnswers: UserAnswers): Seq[GuaranteeType] = {
    val valuesExcludingTIRGuarantee = values.filterNot(_ == TIRGuarantee)
    userAnswers.get(OfficeOfDeparturePage).map(_.countryId.code) match {
      case Some(XI) => valuesExcludingTIRGuarantee.filterNot(_ == IndividualGuaranteeMultiple)
      case _        => valuesExcludingTIRGuarantee
    }
  }
}
