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

import config.FrontendAppConfig
import controllers.actions._
import controllers.guaranteeDetails.guarantee.routes._
import controllers.routes._
import forms.AddAnotherFormProvider
import models.requests.DataRequest
import models.{Index, LocalReferenceNumber, NormalMode}
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import viewModels.ListItem
import viewModels.guaranteeDetails.AddAnotherGuaranteeViewModel.AddAnotherGuaranteeViewModelProvider
import views.html.guaranteeDetails.AddAnotherGuaranteeView

import javax.inject.Inject

class AddAnotherGuaranteeController @Inject() (
  override val messagesApi: MessagesApi,
  actions: Actions,
  formProvider: AddAnotherFormProvider,
  val controllerComponents: MessagesControllerComponents,
  config: FrontendAppConfig,
  viewModelProvider: AddAnotherGuaranteeViewModelProvider,
  view: AddAnotherGuaranteeView
) extends FrontendBaseController
    with I18nSupport {

  private def form(allowMoreGuarantees: Boolean): Form[Boolean] =
    formProvider("guaranteeDetails.addAnotherGuarantee", allowMoreGuarantees)

  def onPageLoad(lrn: LocalReferenceNumber): Action[AnyContent] = actions.requireData(lrn) {
    implicit request =>
      val (guarantees, numberOfGuarantees, allowMoreGuarantees) = viewData
      numberOfGuarantees match {
        case 0 => Redirect(routes.AddGuaranteeYesNoController.onPageLoad(lrn))
        case _ => Ok(view(form(allowMoreGuarantees), lrn, guarantees, allowMoreGuarantees))
      }
  }

  def onSubmit(lrn: LocalReferenceNumber): Action[AnyContent] = actions.requireData(lrn) {
    implicit request =>
      lazy val (guarantees, numberOfGuarantees, allowMoreGuarantees) = viewData
      form(allowMoreGuarantees)
        .bindFromRequest()
        .fold(
          formWithErrors => BadRequest(view(formWithErrors, lrn, guarantees, allowMoreGuarantees)),
          {
            case true  => Redirect(GuaranteeTypeController.onPageLoad(lrn, NormalMode, Index(numberOfGuarantees)))
            case false => Redirect(TaskListController.onPageLoad(lrn))
          }
        )
  }

  private def viewData(implicit request: DataRequest[_]): (Seq[ListItem], Int, Boolean) = {
    val guarantees         = viewModelProvider.apply(request.userAnswers).listItems
    val numberOfGuarantees = guarantees.length
    (guarantees, numberOfGuarantees, numberOfGuarantees < config.maxGuarantees)
  }
}
