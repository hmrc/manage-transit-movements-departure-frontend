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

  private val `0,1,2,4,9` = Seq(
    GuaranteeWaiver,
    ComprehensiveGuarantee,
    IndividualGuarantee,
    FlatRateVoucher,
    IndividualGuaranteeMultiple
  )
  private val `3` = Seq(CashDepositGuarantee)
  private val `5` = Seq(GuaranteeWaiverSecured)
  private val `8` = Seq(GuaranteeNotRequiredExemptPublicBody)
  private val `A,R` = Seq(
    GuaranteeWaiverByAgreement,
    GuaranteeNotRequired
  )
  private val `B` = TIRGuarantee

  // scalastyle:off cyclomatic.complexity
  implicit def userAnswersReader(index: Index): UserAnswersReader[GuaranteeDomain] =
    DeclarationTypePage.reader.flatMap {
      case Option4 =>
        GuaranteeTypePage(index)
          .mandatoryReader(_ == `B`)
          .map(GuaranteeOfTypesABR(_)(index))
      case _ =>
        GuaranteeTypePage(index).reader.flatMap {
          case guaranteeType if `A,R`.contains(guaranteeType) =>
            GuaranteeOfTypesABR.userAnswersReader(index, guaranteeType)
          case guaranteeType if `0,1,2,4,9`.contains(guaranteeType) =>
            GuaranteeOfTypes01249.userAnswersReader(index, guaranteeType)
          case guaranteeType if `5`.contains(guaranteeType) =>
            GuaranteeOfType5.userAnswersReader(index, guaranteeType)
          case guaranteeType if `8`.contains(guaranteeType) =>
            GuaranteeOfType8.userAnswersReader(index, guaranteeType)
          case guaranteeType if `3`.contains(guaranteeType) =>
            GuaranteeOfType3.userAnswersReader(index, guaranteeType)
          case `B` =>
            UserAnswersReader.fail[GuaranteeDomain](GuaranteeTypePage(index))
        }
    }
  // scalastyle:on cyclomatic.complexity

  case class GuaranteeOfTypesABR(
    `type`: GuaranteeType
  )(override val index: Index)
      extends GuaranteeDomain

  object GuaranteeOfTypesABR {

    def userAnswersReader(index: Index, guaranteeType: GuaranteeType): UserAnswersReader[GuaranteeDomain] =
      UserAnswersReader(GuaranteeOfTypesABR(guaranteeType)(index))
  }

  case class GuaranteeOfTypes01249(
    `type`: GuaranteeType,
    grn: String,
    accessCode: String,
    liabilityAmount: String
  )(override val index: Index)
      extends GuaranteeDomain

  object GuaranteeOfTypes01249 {

    def userAnswersReader(index: Index, guaranteeType: GuaranteeType): UserAnswersReader[GuaranteeDomain] =
      (
        UserAnswersReader(guaranteeType),
        ReferenceNumberPage(index).reader
      ).mapN {
        (`type`, grn) => GuaranteeOfTypes01249(`type`, grn, "", "")(index) // TODO - read access code and liability amount once built
      }
  }

  case class GuaranteeOfType5(
    `type`: GuaranteeType,
    grn: String
  )(override val index: Index)
      extends GuaranteeDomain

  object GuaranteeOfType5 {

    def userAnswersReader(index: Index, guaranteeType: GuaranteeType): UserAnswersReader[GuaranteeDomain] =
      (
        UserAnswersReader(guaranteeType),
        ReferenceNumberPage(index).reader
      ).mapN {
        (`type`, grn) => GuaranteeOfType5(`type`, grn)(index)
      }
  }

  case class GuaranteeOfType8(
    `type`: GuaranteeType,
    otherReference: String
  )(override val index: Index)
      extends GuaranteeDomain

  object GuaranteeOfType8 {

    def userAnswersReader(index: Index, guaranteeType: GuaranteeType): UserAnswersReader[GuaranteeDomain] =
      UserAnswersReader(guaranteeType).map {
        `type` => GuaranteeOfType8(`type`, "")(index) // TODO - read other ref. page once built
      }
  }

  case class GuaranteeOfType3(
    `type`: GuaranteeType,
    otherReference: Option[String]
  )(override val index: Index)
      extends GuaranteeDomain

  object GuaranteeOfType3 {

    def userAnswersReader(index: Index, guaranteeType: GuaranteeType): UserAnswersReader[GuaranteeDomain] =
      UserAnswersReader(guaranteeType).map {
        `type` => GuaranteeOfType3(`type`, None)(index) // TODO - read other ref. pages once built
      }
  }
}
