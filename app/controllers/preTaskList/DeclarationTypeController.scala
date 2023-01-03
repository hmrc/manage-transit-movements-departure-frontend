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

package controllers.preTaskList

import controllers.actions._
import controllers.{NavigatorOps, SettableOps, SettableOpsRunner}
import forms.EnumerableFormProvider
import models.journeyDomain.PreTaskListDomain
import models.requests.DataRequest
import models.{DeclarationType, LocalReferenceNumber, Mode}
import navigation.{PreTaskListNavigatorProvider, UserAnswersNavigator}
import pages.preTaskList.DeclarationTypePage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.preTaskList.DeclarationTypeView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class DeclarationTypeController @Inject() (
  override val messagesApi: MessagesApi,
  implicit val sessionRepository: SessionRepository,
  navigatorProvider: PreTaskListNavigatorProvider,
  actions: Actions,
  checkIfTaskAlreadyCompleted: CheckTaskAlreadyCompletedActionProvider,
  formProvider: EnumerableFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: DeclarationTypeView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  private val form = formProvider[DeclarationType]("declarationType")

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = actions
    .requireData(lrn)
    .andThen(checkIfTaskAlreadyCompleted[PreTaskListDomain]) {
      implicit request =>
        val preparedForm = request.userAnswers.get(DeclarationTypePage) match {
          case None        => form
          case Some(value) => form.fill(value)
        }

        Ok(view(preparedForm, DeclarationType.radioItemsU(request.userAnswers), lrn, mode))
    }

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = actions
    .requireData(lrn)
    .andThen(checkIfTaskAlreadyCompleted[PreTaskListDomain])
    .async {
      implicit request: DataRequest[AnyContent] =>
        form
          .bindFromRequest()
          .fold(
            formWithErrors => Future.successful(BadRequest(view(formWithErrors, DeclarationType.radioItemsU(request.userAnswers), lrn, mode))),
            value => {
              implicit val navigator: UserAnswersNavigator = navigatorProvider(mode)
              DeclarationTypePage.writeToUserAnswers(value).writeToSession().navigate()
            }
          )
    }
}
