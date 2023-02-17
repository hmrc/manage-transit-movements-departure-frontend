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

package models.journeyDomain.transport.equipment

import cats.implicits._
import controllers.transport.equipment.index.routes
import models.domain.{GettableAsFilterForNextReaderOps, GettableAsReaderOps, JsArrayGettableAsReaderOps, UserAnswersReader}
import models.journeyDomain.transport.equipment.index.itemNumber.ItemNumbersDomain
import models.journeyDomain.transport.equipment.seal.SealsDomain
import models.journeyDomain.{JourneyDomainModel, Stage}
import models.transport.authorisations.AuthorisationType
import models.{Index, Mode, ProcedureType, UserAnswers}
import pages.preTaskList.ProcedureTypePage
import pages.sections.transport.authorisationsAndLimit.AuthorisationsSection
import pages.transport.authorisationsAndLimit.authorisations.index.AuthorisationTypePage
import pages.transport.equipment.index._
import pages.transport.preRequisites.ContainerIndicatorPage
import play.api.i18n.Messages
import play.api.mvc.Call

case class EquipmentDomain(
  containerId: Option[String],
  seals: Option[SealsDomain],
  goodsItemNumbers: Option[ItemNumbersDomain]
)(index: Index)
    extends JourneyDomainModel {

  def asString(implicit messages: Messages): String =
    EquipmentDomain.asString(index, containerId)

  override def routeIfCompleted(userAnswers: UserAnswers, mode: Mode, stage: Stage): Option[Call] =
    Some(routes.EquipmentAnswersController.onPageLoad(userAnswers.lrn, mode, index))
}

object EquipmentDomain {

  def asString(index: Index, containerId: Option[String])(implicit messages: Messages): String =
    containerId.fold(
      messages("transport.equipment.value.withoutContainer", index.display)
    )(
      messages("transport.equipment.value.withContainer", index.display, _)
    )

  implicit def userAnswersReader(equipmentIndex: Index): UserAnswersReader[EquipmentDomain] =
    (
      containerIdReads(equipmentIndex),
      sealsReads(equipmentIndex),
      goodsItemNumbersReads(equipmentIndex)
    ).tupled.map((EquipmentDomain.apply _).tupled).map(_(equipmentIndex))

  def containerIdReads(equipmentIndex: Index): UserAnswersReader[Option[String]] =
    ContainerIndicatorPage.reader.flatMap {
      case true if equipmentIndex.isFirst =>
        ContainerIdentificationNumberPage(equipmentIndex).reader.map(Option(_))
      case true =>
        AddContainerIdentificationNumberYesNoPage(equipmentIndex).filterOptionalDependent(identity) {
          ContainerIdentificationNumberPage(equipmentIndex).reader
        }
      case false =>
        none[String].pure[UserAnswersReader]
    }

  def sealsReads(equipmentIndex: Index): UserAnswersReader[Option[SealsDomain]] = for {
    procedureType      <- ProcedureTypePage.reader
    authorisationTypes <- AuthorisationsSection.fieldReader(AuthorisationTypePage)
    hasSSEAuthorisation = authorisationTypes.contains(AuthorisationType.SSE)
    reader <- (procedureType, hasSSEAuthorisation) match {
      case (ProcedureType.Simplified, true) =>
        UserAnswersReader[SealsDomain](SealsDomain.userAnswersReader(equipmentIndex)).map(Option(_))
      case _ =>
        AddSealYesNoPage(equipmentIndex).filterOptionalDependent(identity) {
          UserAnswersReader[SealsDomain](SealsDomain.userAnswersReader(equipmentIndex))
        }
    }
  } yield reader

  def goodsItemNumbersReads(equipmentIndex: Index): UserAnswersReader[Option[ItemNumbersDomain]] =
    ContainerIdentificationNumberPage(equipmentIndex).optionalReader.flatMap {
      case Some(_) if equipmentIndex.isFirst =>
        AddGoodsItemNumberYesNoPage(equipmentIndex).filterOptionalDependent(identity) {
          UserAnswersReader[ItemNumbersDomain](ItemNumbersDomain.userAnswersReader(equipmentIndex))
        }
      case _ =>
        UserAnswersReader[ItemNumbersDomain](ItemNumbersDomain.userAnswersReader(equipmentIndex)).map(Option(_))
    }
}
