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

package controllers.traderDetails.consignment.consignor

import controllers.actions._
import controllers.{NavigatorOps, SettableOps, SettableOpsRunner}
import forms.YesNoFormProvider
import models.journeyDomain.traderDetails.TraderDetailsDomain
import models.{LocalReferenceNumber, Mode}
import navigation.UserAnswersNavigator
import navigation.traderDetails.TraderDetailsNavigatorProvider
import pages.traderDetails.consignment.consignor.EoriYesNoPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.traderDetails.consignment.consignor.EoriYesNoView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class EoriYesNoController @Inject() (
  override val messagesApi: MessagesApi,
  implicit val sessionRepository: SessionRepository,
  navigatorProvider: TraderDetailsNavigatorProvider,
  actions: Actions,
  formProvider: YesNoFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: EoriYesNoView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  private val form = formProvider("traderDetails.consignment.consignor.eoriYesNo")

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = actions.requireData(lrn) {
    implicit request =>
      val preparedForm = request.userAnswers.get(EoriYesNoPage) match {
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
            EoriYesNoPage.writeToUserAnswers(value).updateTask[TraderDetailsDomain]().writeToSession().navigate()
          }
        )
  }
}
