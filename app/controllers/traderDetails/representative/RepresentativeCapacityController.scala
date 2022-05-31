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

package controllers.traderDetails.representative

import controllers.actions._
import forms.traderDetails.representative.RepresentativeCapacityFormProvider

import javax.inject.Inject
import models.{LocalReferenceNumber, Mode}
import models.traderDetails.representative.RepresentativeCapacity
import navigation.Navigator
import navigation.annotations.Representative
import pages.traderDetails.representative.RepresentativeCapacityPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.traderDetails.representative.RepresentativeCapacityView

import scala.concurrent.{ExecutionContext, Future}

class RepresentativeCapacityController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  @Representative navigator: Navigator,
  actions: Actions,
  formProvider: RepresentativeCapacityFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: RepresentativeCapacityView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  private val form = formProvider()

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = actions.requireData(lrn) {
    implicit request =>
      val preparedForm = request.userAnswers.get(RepresentativeCapacityPage) match {
        case None        => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, lrn, RepresentativeCapacity.radioItems, mode))
  }

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = actions.requireData(lrn).async {
    implicit request =>
      form
        .bindFromRequest()
        .fold(
          formWithErrors => Future.successful(BadRequest(view(formWithErrors, lrn, RepresentativeCapacity.radioItems, mode))),
          value =>
            for {
              updatedAnswers <- Future.fromTry(request.userAnswers.set(RepresentativeCapacityPage, value))
              _              <- sessionRepository.set(updatedAnswers)
            } yield Redirect(navigator.nextPage(RepresentativeCapacityPage, mode, updatedAnswers))
        )
  }
}
