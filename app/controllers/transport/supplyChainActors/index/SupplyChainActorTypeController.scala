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

package controllers.transport.supplyChainActors.index

import controllers.actions._
import controllers.{NavigatorOps, SettableOps, SettableOpsRunner}
import forms.EnumerableFormProvider
import models.transport.supplyChainActors.SupplyChainActorType
import models.{Index, LocalReferenceNumber, Mode}
import navigation.UserAnswersNavigator
import navigation.transport.SupplyChainActorNavigatorProvider
import pages.transport.supplyChainActors.index.SupplyChainActorTypePage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.transport.supplyChainActors.index.SupplyChainActorTypeView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class SupplyChainActorTypeController @Inject() (
  override val messagesApi: MessagesApi,
  implicit val sessionRepository: SessionRepository,
  navigatorProvider: SupplyChainActorNavigatorProvider,
  actions: Actions,
  formProvider: EnumerableFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: SupplyChainActorTypeView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  private val form = formProvider[SupplyChainActorType]("transport.supplyChainActors.index.supplyChainActorType")

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode, actorIndex: Index): Action[AnyContent] = actions.requireData(lrn) {
    implicit request =>
      val preparedForm = request.userAnswers.get(SupplyChainActorTypePage(actorIndex)) match {
        case None        => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, lrn, SupplyChainActorType.radioItems, mode, actorIndex))
  }

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode, actorIndex: Index): Action[AnyContent] = actions.requireData(lrn).async {
    implicit request =>
      form
        .bindFromRequest()
        .fold(
          formWithErrors => Future.successful(BadRequest(view(formWithErrors, lrn, SupplyChainActorType.radioItems, mode, actorIndex))),
          value => {
            implicit val navigator: UserAnswersNavigator = navigatorProvider(mode, actorIndex)
            SupplyChainActorTypePage(actorIndex).writeToUserAnswers(value).writeToSession().navigate()
          }
        )
  }
}
