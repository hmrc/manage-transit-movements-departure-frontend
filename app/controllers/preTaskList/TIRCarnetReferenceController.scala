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

package controllers.preTaskList

import controllers.actions._
import controllers.{NavigatorOps, SettableOps, SettableOpsRunner}
import forms.preTaskList.TIRCarnetReferenceFormProvider
import models.DeclarationType.Option4
import models.ProcedureType.Normal
import models.journeyDomain.PreTaskListDomain
import models.{LocalReferenceNumber, Mode}
import navigation.Navigator
import navigation.annotations.PreTaskListDetails
import pages.preTaskList.{DeclarationTypePage, ProcedureTypePage, TIRCarnetReferencePage}
import play.api.Logging
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.preTaskList.TirCarnetReferenceView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class TIRCarnetReferenceController @Inject() (
  override val messagesApi: MessagesApi,
  implicit val sessionRepository: SessionRepository,
  @PreTaskListDetails implicit val navigator: Navigator,
  actions: Actions,
  checkIfTaskAlreadyCompleted: CheckTaskAlreadyCompletedActionProvider,
  getMandatoryPage: SpecificDataRequiredActionProvider,
  formProvider: TIRCarnetReferenceFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: TirCarnetReferenceView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with Logging {

  private val form = formProvider()

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] =
    actions
      .requireData(lrn)
      .andThen(checkIfTaskAlreadyCompleted[PreTaskListDomain])
      .andThen(getMandatoryPage.getFirst(ProcedureTypePage))
      .andThen(getMandatoryPage.getSecond(DeclarationTypePage)) {
        implicit request =>
          request.arg match {
            case (Normal, Option4) =>
              val preparedForm = request.userAnswers.get(TIRCarnetReferencePage) match {
                case None        => form
                case Some(value) => form.fill(value)
              }

              Ok(view(preparedForm, lrn, mode))
            case _ =>
              logger.warn(s"[TIRCarnetReferenceController][onPageLoad] Cannot create TIR carnet reference")
              Redirect(routes.LocalReferenceNumberController.onPageLoad())
          }
      }

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = actions
    .requireData(lrn)
    .andThen(checkIfTaskAlreadyCompleted[PreTaskListDomain])
    .async {
      implicit request =>
        form
          .bindFromRequest()
          .fold(
            formWithErrors => Future.successful(BadRequest(view(formWithErrors, lrn, mode))),
            value => TIRCarnetReferencePage.writeToUserAnswers(value).writeToSession().navigateWith(mode)
          )
    }
}
