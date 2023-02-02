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
import models.DeclarationType.Option4
import models.GuaranteeType._
import models.domain._
import models.journeyDomain.Stage.{AccessingJourney, CompletingJourney}
import models.journeyDomain.{JourneyDomainModel, Stage}
import models.reference.CurrencyCode
import models.{CheckMode, GuaranteeType, Index, Mode, UserAnswers}
import pages.guaranteeDetails.guarantee._
import pages.preTaskList.DeclarationTypePage
import play.api.mvc.Call

sealed trait GuaranteeDomain extends JourneyDomainModel {
  val index: Index

  val `type`: GuaranteeType

  override def routeIfCompleted(userAnswers: UserAnswers, mode: Mode, stage: Stage): Option[Call] =
    Some(controllers.guaranteeDetails.guarantee.routes.CheckYourAnswersController.onPageLoad(userAnswers.lrn, index))
}

object GuaranteeDomain {

  // scalastyle:off cyclomatic.complexity
  implicit def userAnswersReader(index: Index): UserAnswersReader[GuaranteeDomain] =
    DeclarationTypePage.reader.flatMap {
      case Option4 =>
        GuaranteeTypePage(index).mandatoryReader(_ == TIRGuarantee).map(GuaranteeOfTypesAB(_)(index))
      case _ =>
        GuaranteeTypePage(index).reader.flatMap {
          guaranteeType =>
            guaranteeType match {
              case GuaranteeWaiverByAgreement =>
                GuaranteeOfTypesAB.userAnswersReader(index, guaranteeType)
              case GuaranteeWaiver | ComprehensiveGuarantee | IndividualGuarantee | FlatRateVoucher | IndividualGuaranteeMultiple =>
                GuaranteeOfTypes01249.userAnswersReader(index, guaranteeType)
              case GuaranteeWaiverSecured =>
                GuaranteeOfType5.userAnswersReader(index, guaranteeType)
              case GuaranteeNotRequiredExemptPublicBody =>
                GuaranteeOfType8.userAnswersReader(index, guaranteeType)
              case CashDepositGuarantee =>
                GuaranteeOfType3.userAnswersReader(index, guaranteeType)
              case TIRGuarantee =>
                UserAnswersReader.fail[GuaranteeDomain](GuaranteeTypePage(index))
            }
        }
    }
  // scalastyle:on cyclomatic.complexity

  case class GuaranteeOfTypesAB(
    `type`: GuaranteeType
  )(override val index: Index)
      extends GuaranteeDomain {

    override def routeIfCompleted(userAnswers: UserAnswers, mode: Mode, stage: Stage): Option[Call] = Some {
      stage match {
        case AccessingJourney =>
          controllers.guaranteeDetails.guarantee.routes.GuaranteeTypeController.onPageLoad(userAnswers.lrn, CheckMode, index)
        case CompletingJourney =>
          controllers.guaranteeDetails.routes.AddAnotherGuaranteeController.onPageLoad(userAnswers.lrn)
      }
    }
  }

  object GuaranteeOfTypesAB {

    def userAnswersReader(index: Index, guaranteeType: GuaranteeType): UserAnswersReader[GuaranteeDomain] =
      UserAnswersReader(GuaranteeOfTypesAB(guaranteeType)(index))
  }

  case class GuaranteeOfTypes01249(
    `type`: GuaranteeType,
    grn: String,
    currency: CurrencyCode,
    liabilityAmount: BigDecimal,
    accessCode: String
  )(override val index: Index)
      extends GuaranteeDomain

  object GuaranteeOfTypes01249 {

    def userAnswersReader(index: Index, guaranteeType: GuaranteeType): UserAnswersReader[GuaranteeDomain] =
      (
        UserAnswersReader(guaranteeType),
        ReferenceNumberPage(index).reader,
        CurrencyPage(index).reader,
        LiabilityAmountPage(index).reader,
        AccessCodePage(index).reader
      ).mapN {
        (`type`, grn, currency, liabilityAmount, accessCode) => GuaranteeOfTypes01249(`type`, grn, currency, liabilityAmount, accessCode)(index)
      }
  }

  case class GuaranteeOfType5(
    `type`: GuaranteeType,
    currency: CurrencyCode,
    liabilityAmount: BigDecimal,
  )(override val index: Index)
      extends GuaranteeDomain

  object GuaranteeOfType5 {

    def userAnswersReader(index: Index, guaranteeType: GuaranteeType): UserAnswersReader[GuaranteeDomain] =
      (
        UserAnswersReader(guaranteeType),
        CurrencyPage(index).reader,
        LiabilityAmountPage(index).reader,
      ).mapN {
        (`type`, currency, liabilityAmount) => GuaranteeOfType5(`type`, currency, liabilityAmount)(index)
      }
  }

  case class GuaranteeOfType8(
    `type`: GuaranteeType,
    type8And3Guarantee: Type8And3GuaranteeDomain,
  )(override val index: Index)
      extends GuaranteeDomain

  object GuaranteeOfType8 {

    def userAnswersReader(index: Index, guaranteeType: GuaranteeType): UserAnswersReader[GuaranteeDomain] =
      (
        UserAnswersReader(guaranteeType),
        UserAnswersReader[Type8And3GuaranteeDomain](Type8And3GuaranteeDomain.userAnswersReader(index))
      ).mapN {
        (`type`, type8And3Guarantee) => GuaranteeOfType8(`type`, type8And3Guarantee)(index)
      }
  }

  case class GuaranteeOfType3(
    `type`: GuaranteeType,
    type8And3Guarantee: Option[Type8And3GuaranteeDomain]
  )(override val index: Index)
      extends GuaranteeDomain

  object GuaranteeOfType3 {

    def userAnswersReader(index: Index, guaranteeType: GuaranteeType): UserAnswersReader[GuaranteeDomain] =
      (
        UserAnswersReader(guaranteeType),
        OtherReferenceYesNoPage(index)
          .filterOptionalDependent(identity)(UserAnswersReader[Type8And3GuaranteeDomain](Type8And3GuaranteeDomain.userAnswersReader(index))
      )).mapN {
        (`type`, type8And3Guarantee) => GuaranteeOfType3(`type`, type8And3Guarantee)(index)
      }
  }
}
