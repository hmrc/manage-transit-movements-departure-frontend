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

package pages.transport.supplyChainActors.index

import controllers.transport.supplyChainActors.index.routes
import models.transport.supplyChainActors.SupplyChainActorType
import models.{Index, Mode, UserAnswers}
import pages.QuestionPage
import pages.sections.transport.SupplyChainActorSection
import play.api.libs.json.JsPath
import play.api.mvc.Call

import scala.util.Try

case class SupplyChainActorTypePage(actorIndex: Index) extends QuestionPage[SupplyChainActorType] {

  override def path: JsPath = SupplyChainActorSection(actorIndex).path \ toString

  override def toString: String = "supplyChainActorType"

  override def route(userAnswers: UserAnswers, mode: Mode): Option[Call] =
    Some(routes.SupplyChainActorTypeController.onPageLoad(userAnswers.lrn, mode, actorIndex))

  override def cleanup(value: Option[SupplyChainActorType], userAnswers: UserAnswers): Try[UserAnswers] =
    value match {
      case Some(_) => userAnswers.remove(IdentificationNumberPage(actorIndex))
      case None    => super.cleanup(value, userAnswers)
    }
}
