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

package controllers.transport.authorisationsAndLimit.limit

import config.FrontendAppConfig
import controllers.actions._
import controllers.{NavigatorOps, SettableOps, SettableOpsRunner}
import forms.DateFormProvider
import models.{LocalReferenceNumber, Mode}
import navigation.UserAnswersNavigator
import navigation.transport.TransportNavigatorProvider
import pages.transport.authorisationsAndLimit.limit.LimitDatePage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.transport.authorisationsAndLimit.limit.LimitDateView

import java.time.LocalDate
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class LimitDateController @Inject() (
  override val messagesApi: MessagesApi,
  implicit val sessionRepository: SessionRepository,
  navigatorProvider: TransportNavigatorProvider,
  formProvider: DateFormProvider,
  actions: Actions,
  appConfig: FrontendAppConfig,
  val controllerComponents: MessagesControllerComponents,
  view: LimitDateView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  private val maxDate = LocalDate.now.plusDays(appConfig.maxDaysLimit)
  private val minDate = LocalDate.now()

  private val form = formProvider("transport.authorisationsAndLimit.limit.limitDate", minDate, maxDate)

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = actions.requireData(lrn) {
    implicit request =>
      val preparedForm = request.userAnswers.get(LimitDatePage) match {
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
            LimitDatePage.writeToUserAnswers(value).writeToSession().navigate()
          }
        )
  }
}
