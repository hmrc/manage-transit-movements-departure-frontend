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

package controllers.routeDetails.transit

import controllers.actions._
import controllers.{NavigatorOps, SettableOps, SettableOpsRunner}
import forms.AddAnotherFormProvider
import models.{LocalReferenceNumber, Mode}
import navigation.Navigator
import pages.routeDetails.transit.AddAnotherOfficeOfTransitPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.routeDetails.transit.AddAnotherOfficeOfTransitView
import javax.inject.Inject
import navigation.annotations.routeDetails.Transit
import play.api.data.Form

import scala.concurrent.{ExecutionContext, Future}

class AddAnotherOfficeOfTransitController @Inject() (
  override val messagesApi: MessagesApi,
  implicit val sessionRepository: SessionRepository,
  @Transit implicit val navigator: Navigator,
  actions: Actions,
  formProvider: AddAnotherFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: AddAnotherOfficeOfTransitView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  private def form(allowMoreOfficesOfTransit: Boolean): Form[Boolean] =
    formProvider("guaranteeDetails.addAnotherOfficeOfTransit", allowMoreOfficesOfTransit)

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = actions.requireData(lrn) {
    implicit request =>
      val preparedForm = request.userAnswers.get(AddAnotherOfficeOfTransitPage) match {
        case None        => form(true)
        case Some(value) => form(true).fill(value)
      }

      Ok(view(preparedForm, lrn, mode))
  }

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = actions.requireData(lrn).async {
    implicit request =>
      form(true)
        .bindFromRequest()
        .fold(
          formWithErrors => Future.successful(BadRequest(view(formWithErrors, lrn, mode))),
          value => AddAnotherOfficeOfTransitPage.writeToUserAnswers(value).writeToSession().navigateWith(mode)
        )
  }
}
