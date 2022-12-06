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

package controllers.transport.transportMeans.active

import config.FrontendAppConfig
import controllers.actions._
import controllers.{NavigatorOps, SettableOps, SettableOpsRunner}
import forms.YesNoFormProvider
import models.requests.DataRequest
import models.{LocalReferenceNumber, Mode}
import navigation.UserAnswersNavigator
import navigation.transport.TransportMeansNavigatorProvider
import pages.transport.transportMeans.active.AddAnotherBorderTransportPage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import viewModels.ListItem
import views.html.transport.transportMeans.active.AddAnotherBorderTransportView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class AddAnotherBorderTransportController @Inject() (
  override val messagesApi: MessagesApi,
  implicit val sessionRepository: SessionRepository,
  navigatorProvider: TransportMeansNavigatorProvider,
  actions: Actions,
  formProvider: YesNoFormProvider,
  config: FrontendAppConfig,
  val controllerComponents: MessagesControllerComponents,
  view: AddAnotherBorderTransportView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  // paragraph = Html(s"""<p class="govuk-body">${messages("transport.transportMeans.active.addAnotherBorderTransport.paragraph")}</p>""")

  private def form(allowMoreActiveBorderTransports: Boolean): Form[Boolean] =
    formProvider("transport.transportMeans.active.addAnotherBorderTransport", allowMoreActiveBorderTransports)

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = actions.requireData(lrn).async {
    implicit request =>
      viewData(mode) flatMap {

      }
      val preparedForm = request.userAnswers.get(AddAnotherBorderTransportPage) match {
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
          value => {
            implicit val navigator: UserAnswersNavigator = navigatorProvider(mode)
            AddAnotherBorderTransportPage.writeToUserAnswers(value).writeToSession().navigate()
          }
        )
  }

  private def viewData(mode: Mode)(implicit request: DataRequest[_], ec: ExecutionContext): Future[(Seq[ListItem], Int, Boolean)] =
    viewModelProvider.apply(request.userAnswers, mode).map {
      viewModel =>
        val activeBorderTransports = viewModel.listItems
        val numberOfActiveBorderTransports = activeBorderTransports.length
        (activeBorderTransports, numberOfActiveBorderTransports, numberOfActiveBorderTransports < config.maxActiveBorderTransports)
    }
}
