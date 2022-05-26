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

package controllers.traderDetails.holderOfTransit

import controllers.actions._
import forms.TelephoneNumberFormProvider
import models.{LocalReferenceNumber, Mode}
import navigation.Navigator
import navigation.annotations.TraderDetails
import pages.traderDetails.holderOfTransit.{ContactNamePage, ContactTelephoneNumberPage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.traderDetails.holderOfTransit.ContactTelephoneNumberView
import javax.inject.Inject

import scala.concurrent.{ExecutionContext, Future}

class ContactTelephoneNumberController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  @TraderDetails navigator: Navigator,
  formProvider: TelephoneNumberFormProvider,
  actions: Actions,
  val controllerComponents: MessagesControllerComponents,
  view: ContactTelephoneNumberView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = actions.requireData(lrn) {
    implicit request =>
      request.userAnswers.get(ContactNamePage) match {
        case Some(name) =>
          val form = formProvider("traderDetails.holderOfTransit.contactTelephoneNumber", name)
          val preparedForm = request.userAnswers.get(ContactTelephoneNumberPage) match {
            case None        => form
            case Some(value) => form.fill(value)
          }
          Ok(view(preparedForm, lrn, mode, name))

        case _ => Redirect(controllers.routes.SessionExpiredController.onPageLoad())
      }
  }

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = actions.requireData(lrn).async {
    implicit request =>
      request.userAnswers.get(ContactNamePage) match {
        case Some(name) =>
          val form = formProvider("traderDetails.holderOfTransit.contactTelephoneNumber", name)
          form
            .bindFromRequest()
            .fold(
              formWithErrors => Future.successful(BadRequest(view(formWithErrors, lrn, mode, name))),
              value =>
                for {
                  updatedAnswers <- Future.fromTry(request.userAnswers.set(ContactTelephoneNumberPage, value))
                  _              <- sessionRepository.set(updatedAnswers)
                } yield Redirect(navigator.nextPage(ContactTelephoneNumberPage, mode, updatedAnswers))
            )
        case _ => Future.successful(Redirect(controllers.routes.SessionExpiredController.onPageLoad()))
      }
  }
}
