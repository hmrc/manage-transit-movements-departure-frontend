/*
 * Copyright 2024 HM Revenue & Customs
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

import controllers.actions.*
import controllers.{NavigatorOps, SettableOps, SettableOpsRunner}
import forms.SelectableFormProvider.CustomsOfficeFormProvider
import models.reference.CustomsOffice
import models.{LocalReferenceNumber, Mode, SelectableList}
import navigation.{PreTaskListNavigatorProvider, UserAnswersNavigator}
import pages.preTaskList.{OfficeOfDepartureInCL010Page, OfficeOfDepartureInCL112Page, OfficeOfDepartureInCL147Page, OfficeOfDeparturePage}
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import services.{CountriesService, CustomsOfficesService}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.preTaskList.OfficeOfDepartureView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class OfficeOfDepartureController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  navigatorProvider: PreTaskListNavigatorProvider,
  actions: Actions,
  checkIfPreTaskListAlreadyCompleted: PreTaskListCompletedAction,
  formProvider: CustomsOfficeFormProvider,
  customsOfficesService: CustomsOfficesService,
  countriesService: CountriesService,
  val controllerComponents: MessagesControllerComponents,
  view: OfficeOfDepartureView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  private def form(customsOfficeList: SelectableList[CustomsOffice]): Form[CustomsOffice] =
    formProvider("officeOfDeparture", customsOfficeList)

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = actions
    .requireData(lrn)
    .andThen(checkIfPreTaskListAlreadyCompleted)
    .async {
      implicit request =>
        customsOfficesService.getCustomsOfficesOfDeparture.map {
          customsOfficeList =>
            val preparedForm = request.userAnswers.get(OfficeOfDeparturePage) match {
              case None        => form(customsOfficeList)
              case Some(value) => form(customsOfficeList).fill(value)
            }

            Ok(view(preparedForm, lrn, customsOfficeList.values, mode))
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
                formWithErrors => Future.successful(BadRequest(view(formWithErrors, lrn, customsOfficeList.values, mode))),
                value => {
                  val navigator: UserAnswersNavigator = navigatorProvider(mode)
                  for {
                    isInCL112 <- countriesService.isInCL112(value)
                    isInCL147 <- countriesService.isInCL147(value)
                    isInCL010 <- countriesService.isInCL010(value)
                    result <- OfficeOfDeparturePage
                      .writeToUserAnswers(value)
                      .appendValue(OfficeOfDepartureInCL112Page, isInCL112)
                      .appendValue(OfficeOfDepartureInCL147Page, isInCL147)
                      .appendValue(OfficeOfDepartureInCL010Page, isInCL010)
                      .writeToSession(sessionRepository)
                      .navigateWith(navigator)
                  } yield result
                }
              )
        }
    }

}
