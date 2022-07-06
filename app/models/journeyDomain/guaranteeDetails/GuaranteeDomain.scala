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

package models.journeyDomain.guaranteeDetails

import cats.implicits._
import models.DeclarationType.Option4
import models.domain._
import models.guaranteeDetails.GuaranteeType
import models.guaranteeDetails.GuaranteeType._
import models.journeyDomain.JourneyDomainModel
import models.{Index, UserAnswers}
import pages.guaranteeDetails._
import pages.preTaskList.DeclarationTypePage
import play.api.mvc.Call

sealed trait GuaranteeDomain extends JourneyDomainModel {
  val index: Index

  override def routeIfCompleted(userAnswers: UserAnswers): Option[Call] =
    None // TODO - update to check your answers when built
}

object GuaranteeDomain {

  implicit def userAnswersReader(index: Index): UserAnswersReader[GuaranteeDomain] =
    UserAnswersReader[GuaranteeTypeOnly](GuaranteeTypeOnly.userAnswersReader(index)).widen[GuaranteeDomain] orElse
      UserAnswersReader[FullGuarantee](FullGuarantee.userAnswersReader(index)).widen[GuaranteeDomain] orElse
      UserAnswersReader[GuaranteeWithOtherReference](GuaranteeWithOtherReference.userAnswersReader(index)).widen[GuaranteeDomain] orElse
      UserAnswersReader[GuaranteeWithOptionalOtherReference](GuaranteeWithOptionalOtherReference.userAnswersReader(index)).widen[GuaranteeDomain]

  case class GuaranteeTypeOnly(
    `type`: GuaranteeType
  )(override val index: Index)
      extends GuaranteeDomain

  object GuaranteeTypeOnly {

    implicit def userAnswersReader(index: Index): UserAnswersReader[GuaranteeTypeOnly] =
      DeclarationTypePage.reader
        .map {
          case Option4 =>
            Seq(
              TIRGuarantee // B
            )
          case _ =>
            // TODO - what is J?
            Seq(
              GuaranteeWaiverByAgreement, // A
              GuaranteeNotRequired // R
            )
        }
        .flatMap {
          `types`: Seq[GuaranteeType] =>
            GuaranteeTypePage(index).mandatoryReader(`types`.contains(_)).map {
              `type` => GuaranteeTypeOnly(`type`)(index)
            }
        }
  }

  // TODO - add access code and liability amount once built
  case class FullGuarantee(
    `type`: GuaranteeType,
    grn: String
  )(override val index: Index)
      extends GuaranteeDomain

  object FullGuarantee {

    implicit def userAnswersReader(index: Index): UserAnswersReader[FullGuarantee] = {
      val `types` = Seq(
        GuaranteeWaiver, // 0
        ComprehensiveGuarantee, // 1
        IndividualGuarantee, // 2
        FlatRateVoucher, // 4
        GuaranteeWaiverSecured, // 5
        IndividualGuaranteeMultiple // 9
      )
      (
        GuaranteeTypePage(index).mandatoryReader(`types`.contains(_)),
        ReferenceNumberPage(index).reader
      ).mapN {
        (`type`, grn) => FullGuarantee(`type`, grn)(index)
      }
    }
  }

  case class GuaranteeWithOtherReference(
    `type`: GuaranteeType,
    otherReference: String
  )(override val index: Index)
      extends GuaranteeDomain

  object GuaranteeWithOtherReference {

    implicit def userAnswersReader(index: Index): UserAnswersReader[GuaranteeWithOtherReference] = {
      val `types` = Seq(
        GuaranteeNotRequiredExemptPublicBody // 8
      )
      (
        GuaranteeTypePage(index).mandatoryReader(`types`.contains(_))
      ).map {
        `type` => GuaranteeWithOtherReference(`type`, "")(index) // TODO - read other ref. page once built
      }
    }
  }

  case class GuaranteeWithOptionalOtherReference(
    `type`: GuaranteeType,
    otherReference: Option[String]
  )(override val index: Index)
      extends GuaranteeDomain

  object GuaranteeWithOptionalOtherReference {

    implicit def userAnswersReader(index: Index): UserAnswersReader[GuaranteeWithOptionalOtherReference] = {
      val `types` = Seq(
        CashDepositGuarantee // 3
      )
      (
        GuaranteeTypePage(index).mandatoryReader(`types`.contains(_)),
        none[String].pure[UserAnswersReader] // TODO - read other ref. pages once built
      ).mapN {
        (`type`, otherReference) => GuaranteeWithOptionalOtherReference(`type`, otherReference)(index)
      }
    }
  }
}
