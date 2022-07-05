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
import controllers.{NavigatorOps, SettableOps, SettableOpsRunner}
import forms.guaranteeDetails.GuaranteeTypeFormProvider
import models.{LocalReferenceNumber, Mode}
import models.guaranteeDetails.GuaranteeType
import navigation.Navigator
import navigation.annotations.GuaranteeDetails
import pages.guaranteeDetails.GuaranteeTypePage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.guaranteeDetails.GuaranteeTypeView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class GuaranteeTypeController @Inject() (
  override val messagesApi: MessagesApi,
  implicit val sessionRepository: SessionRepository,
  @GuaranteeDetails implicit val navigator: Navigator,
  actions: Actions,
  formProvider: GuaranteeTypeFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: GuaranteeTypeView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  private val form = formProvider()

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = actions.requireData(lrn) {
    implicit request =>
      val preparedForm = request.userAnswers.get(GuaranteeTypePage) match {
        case None        => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, lrn, GuaranteeType.radioItemsU(request.userAnswers), mode))
  }

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = actions.requireData(lrn).async {
    implicit request =>
      form
        .bindFromRequest()
        .fold(
          formWithErrors => Future.successful(BadRequest(view(formWithErrors, lrn, GuaranteeType.radioItems, mode))),
          value => GuaranteeTypePage.writeToUserAnswers(value).writeToSession().navigateWith(mode)
        )
  }
}
