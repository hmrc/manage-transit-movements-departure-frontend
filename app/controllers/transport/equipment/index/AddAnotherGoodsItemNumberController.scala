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

package controllers.transport.equipment.index

import config.FrontendAppConfig
import controllers.actions._
import controllers.transport.equipment.index.itemNumber.{routes => itemNumberRoutes}
import forms.AddAnotherFormProvider
import models.{Index, LocalReferenceNumber, Mode}
import navigation.transport.TransportNavigatorProvider
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import viewModels.transport.equipment.index.AddAnotherGoodsItemNumberViewModel.AddAnotherGoodsItemNumberViewModelProvider
import views.html.transport.equipment.index.AddAnotherGoodsItemNumberView

import javax.inject.Inject

class AddAnotherGoodsItemNumberController @Inject() (
  override val messagesApi: MessagesApi,
  implicit val sessionRepository: SessionRepository,
  navigatorProvider: TransportNavigatorProvider,
  actions: Actions,
  formProvider: AddAnotherFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: AddAnotherGoodsItemNumberView,
  viewModelProvider: AddAnotherGoodsItemNumberViewModelProvider
)(implicit config: FrontendAppConfig)
    extends FrontendBaseController
    with I18nSupport {

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode, equipmentIndex: Index): Action[AnyContent] = actions.requireData(lrn) {
    implicit request =>
      val viewModel = viewModelProvider(request.userAnswers, mode, equipmentIndex)
      val form      = formProvider("transport.equipment.index.addAnotherGoodsItemNumber", viewModel.allowMoreGoodsItemNumbers)
      viewModel.goodsItemNumbersCount match {
        case 0 => Redirect(routes.AddGoodsItemNumberYesNoController.onPageLoad(lrn, mode, equipmentIndex))
        case _ => Ok(view(form, lrn, mode, equipmentIndex, viewModel, viewModel.allowMoreGoodsItemNumbers))
      }
  }

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode, equipmentIndex: Index): Action[AnyContent] = actions.requireData(lrn) {
    implicit request =>
      val viewModel = viewModelProvider(request.userAnswers, mode, equipmentIndex)
      val form      = formProvider("transport.equipment.index.addAnotherGoodsItemNumber", viewModel.allowMoreGoodsItemNumbers)
      form
        .bindFromRequest()
        .fold(
          formWithErrors => BadRequest(view(formWithErrors, lrn, mode, equipmentIndex, viewModel, viewModel.allowMoreGoodsItemNumbers)),
          {
            case true =>
              Redirect(itemNumberRoutes.ItemNumberController.onPageLoad(lrn, mode, equipmentIndex, Index(viewModel.goodsItemNumbersCount)))
            case false =>
              Redirect(navigatorProvider(mode).nextPage(request.userAnswers))
          }
        )
  }

}
