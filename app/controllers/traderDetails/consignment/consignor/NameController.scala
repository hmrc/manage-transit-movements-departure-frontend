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

package controllers.traderDetails.consignment.consignor

import controllers.actions._
import forms.NameFormProvider
import javax.inject.Inject
import models.{LocalReferenceNumber, Mode}
import navigation.Navigator
import navigation.annotations.TraderDetailsConsignment
import pages.traderDetails.consignment.consignor.NamePage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.traderDetails.consignment.consignor.NameView

import scala.concurrent.{ExecutionContext, Future}

class NameController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  @TraderDetailsConsignment navigator: Navigator,
  formProvider: NameFormProvider,
  actions: Actions,
  val controllerComponents: MessagesControllerComponents,
  view: NameView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  private val form = formProvider("traderDetails.consignment.consignor.name")

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = actions.requireData(lrn) {
    implicit request =>
      val preparedForm = request.userAnswers.get(NamePage) match {
        case None        => form
        case Some(value) => form.fill(value)
      }
      Ok(view(preparedForm, lrn, mode))
  }

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = actions.requireData(lrn).async {
    implicit request =>
      form
        .bindFromRequest()
        .fold(
          formWithErrors => Future.successful(BadRequest(view(formWithErrors, lrn, mode))),
          value =>
            for {
              updatedAnswers <- Future.fromTry(request.userAnswers.set(NamePage, value))
              _              <- sessionRepository.set(updatedAnswers)
            } yield Redirect(navigator.nextPage(NamePage, mode, updatedAnswers))
        )
  }
}
