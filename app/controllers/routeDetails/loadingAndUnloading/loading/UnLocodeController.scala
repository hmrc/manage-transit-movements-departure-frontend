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

package controllers.routeDetails.loadingAndUnloading.loading

import controllers.actions._
import controllers.{NavigatorOps, SettableOps, SettableOpsRunner}
import forms.UnLocodeFormProvider
import models.{LocalReferenceNumber, Mode}
import navigation.routeDetails.LoadingAndUnloadingNavigatorProvider
import pages.routeDetails.loadingAndUnloading.loading.UnLocodePage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import services.UnLocodesService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.routeDetails.loadingAndUnloading.loading.UnLocodeView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class UnLocodeController @Inject() (
  override val messagesApi: MessagesApi,
  implicit val sessionRepository: SessionRepository,
  actions: Actions,
  formProvider: UnLocodeFormProvider,
  service: UnLocodesService,
  navigatorProvider: LoadingAndUnloadingNavigatorProvider,
  val controllerComponents: MessagesControllerComponents,
  view: UnLocodeView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = actions.requireData(lrn).async {
    implicit request =>
      service.getUnLocodes.map {
        unLocodeList =>
          val form = formProvider("routeDetails.loadingAndUnloading.loading.unLocode", unLocodeList)
          val preparedForm = request.userAnswers.get(UnLocodePage) match {
            case None        => form
            case Some(value) => form.fill(value)
          }

          Ok(view(preparedForm, lrn, unLocodeList.unLocodes, mode))
      }
  }

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = actions.requireData(lrn).async {
    implicit request =>
      service.getUnLocodes.flatMap {
        unLocodeList =>
          val form = formProvider("routeDetails.loadingAndUnloading.loading.unLocode", unLocodeList)
          form
            .bindFromRequest()
            .fold(
              formWithErrors => Future.successful(BadRequest(view(formWithErrors, lrn, unLocodeList.unLocodes, mode))),
              value =>
                navigatorProvider(mode).flatMap {
                  implicit navigator =>
                    UnLocodePage.writeToUserAnswers(value).writeToSession().navigate()
                }
            )
      }
  }
}