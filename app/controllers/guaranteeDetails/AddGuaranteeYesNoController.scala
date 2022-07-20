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

package controllers.guaranteeDetails

import controllers.actions._
import forms.YesNoFormProvider
import models.journeyDomain.guaranteeDetails.GuaranteeDetailsDomain
import models.{LocalReferenceNumber, NormalMode}
import navigation.UserAnswersNavigator
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.guaranteeDetails.AddGuaranteeYesNoView

import javax.inject.Inject

class AddGuaranteeYesNoController @Inject() (
  override val messagesApi: MessagesApi,
  implicit val sessionRepository: SessionRepository,
  actions: Actions,
  formProvider: YesNoFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: AddGuaranteeYesNoView
) extends FrontendBaseController
    with I18nSupport {

  private val form = formProvider("guaranteeDetails.addGuaranteeYesNo")

  def onPageLoad(lrn: LocalReferenceNumber): Action[AnyContent] = actions.requireData(lrn) {
    implicit request =>
      Ok(view(form, lrn))
  }

  def onSubmit(lrn: LocalReferenceNumber): Action[AnyContent] = actions.requireData(lrn) {
    implicit request =>
      form
        .bindFromRequest()
        .fold(
          formWithErrors => BadRequest(view(formWithErrors, lrn)),
          {
            case true =>
              Redirect(UserAnswersNavigator.nextPage[GuaranteeDetailsDomain](request.userAnswers, NormalMode))
            case false =>
              Redirect(controllers.routes.TaskListController.onPageLoad(lrn))
          }
        )
  }
}
