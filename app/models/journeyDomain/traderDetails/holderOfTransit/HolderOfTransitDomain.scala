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

package models.journeyDomain.traderDetails.holderOfTransit

import cats.implicits._
import models.DeclarationType.Option4
import models.domain._
import models.journeyDomain.{JourneyDomainModel, Stage}
import models.{Address, EoriNumber, Mode, NormalMode, UserAnswers}
import pages.preTaskList.DeclarationTypePage
import pages.traderDetails.holderOfTransit._
import play.api.mvc.Call

trait HolderOfTransitDomain extends JourneyDomainModel {
  val name: String
  val address: Address
  val additionalContact: Option[AdditionalContactDomain]

  override def routeIfCompleted(userAnswers: UserAnswers, stage: Stage): Option[Call] =
    Some(controllers.traderDetails.routes.ActingAsRepresentativeController.onPageLoad(userAnswers.lrn, NormalMode))
}

object HolderOfTransitDomain {

  implicit val userAnswersReader: UserAnswersReader[HolderOfTransitDomain] =
    DeclarationTypePage.reader.flatMap {
      case Option4 => UserAnswersReader[HolderOfTransitTIR].widen[HolderOfTransitDomain]
      case _       => UserAnswersReader[HolderOfTransitEori].widen[HolderOfTransitDomain]
    }
}

case class HolderOfTransitEori(
  eori: Option[EoriNumber],
  name: String,
  address: Address,
  additionalContact: Option[AdditionalContactDomain]
) extends HolderOfTransitDomain

object HolderOfTransitEori {

  implicit val userAnswersReader: UserAnswersReader[HolderOfTransitEori] =
    (
      EoriYesNoPage.filterOptionalDependent(identity)(EoriPage.reader.map(EoriNumber(_))),
      NamePage.reader,
      AddressPage.reader,
      AddContactPage.filterOptionalDependent(identity)(UserAnswersReader[AdditionalContactDomain])
    ).tupled.map((HolderOfTransitEori.apply _).tupled)

}

case class HolderOfTransitTIR(
  tir: Option[String],
  name: String,
  address: Address,
  additionalContact: Option[AdditionalContactDomain]
) extends HolderOfTransitDomain

object HolderOfTransitTIR {

  implicit val userAnswersReader: UserAnswersReader[HolderOfTransitTIR] =
    (
      TirIdentificationYesNoPage.filterOptionalDependent(identity)(TirIdentificationPage.reader),
      NamePage.reader,
      AddressPage.reader,
      AddContactPage.filterOptionalDependent(identity)(UserAnswersReader[AdditionalContactDomain])
    ).tupled.map((HolderOfTransitTIR.apply _).tupled)
}
