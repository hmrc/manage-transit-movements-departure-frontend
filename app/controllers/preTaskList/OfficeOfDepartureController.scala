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

package controllers.preTaskList

import controllers.actions._
import controllers.{NavigatorOps, SettableOps, SettableOpsRunner}
import forms.CustomsOfficeFormProvider
import models.journeyDomain.PreTaskListDomain
import models.reference.CustomsOffice
import models.{CustomsOfficeList, LocalReferenceNumber, Mode}
import navigation.{PreTaskListNavigatorProvider, UserAnswersNavigator}
import pages.preTaskList.OfficeOfDeparturePage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import services.CustomsOfficesService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.preTaskList.OfficeOfDepartureView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class OfficeOfDepartureController @Inject() (
  override val messagesApi: MessagesApi,
  implicit val sessionRepository: SessionRepository,
  navigatorProvider: PreTaskListNavigatorProvider,
  actions: Actions,
  checkIfPreTaskListAlreadyCompleted: PreTaskListCompletedAction,
  formProvider: CustomsOfficeFormProvider,
  customsOfficesService: CustomsOfficesService,
  val controllerComponents: MessagesControllerComponents,
  view: OfficeOfDepartureView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  private def form(customsOfficeList: CustomsOfficeList): Form[CustomsOffice] = formProvider("officeOfDeparture", customsOfficeList)

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = actions
    .requireData(lrn)
    .andThen(checkIfPreTaskListAlreadyCompleted)
    .async {
      implicit request =>
        customsOfficesService.getCustomsOfficesOfDeparture.map {
          customsOfficeList =>
            val preparedForm = request.userAnswers
              .get(OfficeOfDeparturePage)
              .flatMap(
                x => customsOfficeList.getCustomsOffice(x.id)
              )
              .map(form(customsOfficeList).fill)
              .getOrElse(form(customsOfficeList))

            Ok(view(preparedForm, lrn, customsOfficeList.customsOffices, mode))
        }
    }

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = actions
    .requireData(lrn)
    .andThen(checkIfPreTaskListAlreadyCompleted)
    .async {
      implicit request =>
        customsOfficesService.getCustomsOfficesOfDeparture.flatMap {
          customsOfficeList =>
            form(customsOfficeList)
              .bindFromRequest()
              .fold(
                formWithErrors => Future.successful(BadRequest(view(formWithErrors, lrn, customsOfficeList.customsOffices, mode))),
                value => {
                  implicit val navigator: UserAnswersNavigator = navigatorProvider(mode)
                  OfficeOfDeparturePage.writeToUserAnswers(value).updateTask[PreTaskListDomain]().writeToSession().navigate()
                }
              )
        }
    }

}
