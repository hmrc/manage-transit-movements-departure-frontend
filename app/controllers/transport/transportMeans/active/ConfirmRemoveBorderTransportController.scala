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

package controllers.transport.transportMeans.active

import controllers.actions._
import controllers.{NavigatorOps, SettableOps, SettableOpsRunner}
import forms.YesNoFormProvider
import models.{Index, LocalReferenceNumber, Mode}
import pages.sections.transport.TransportMeansActiveSection
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.transport.transportMeans.active.ConfirmRemoveBorderTransportView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ConfirmRemoveBorderTransportController @Inject() (
  override val messagesApi: MessagesApi,
  implicit val sessionRepository: SessionRepository,
  actions: Actions,
  formProvider: YesNoFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: ConfirmRemoveBorderTransportView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode, activeIndex: Index): Action[AnyContent] = actions
    .requireData(lrn) {
      implicit request =>
        val form = formProvider("transport.transportMeans.active.confirmRemoveBorderTransport", activeIndex.display)

        Ok(view(form, lrn, mode, activeIndex))
    }

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode, activeIndex: Index): Action[AnyContent] = actions.requireData(lrn).async {
    implicit request =>
      val form = formProvider("transport.transportMeans.active.confirmRemoveBorderTransport", activeIndex.display)
      form
        .bindFromRequest()
        .fold(
          formWithErrors => Future.successful(BadRequest(view(formWithErrors, lrn, mode, activeIndex))),
          {
            case true =>
              TransportMeansActiveSection(activeIndex)
                .removeFromUserAnswers()
                .writeToSession()
                .navigateTo(routes.AddAnotherBorderTransportController.onPageLoad(lrn, mode))
            case false =>
              Future.successful(Redirect(routes.AddAnotherBorderTransportController.onPageLoad(lrn, mode)))
          }
        )
  }
}
