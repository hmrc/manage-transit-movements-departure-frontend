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

package controllers.guaranteeDetails

import controllers.actions._
import controllers.{NavigatorOps, SettableOps, SettableOpsRunner}
import models.GuaranteeType.TIRGuarantee
import models.{Index, LocalReferenceNumber}
import pages.guaranteeDetails.guarantee.GuaranteeTypePage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.guaranteeDetails.GuaranteeAddedTIRView

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class GuaranteeAddedTIRController @Inject() (
  override val messagesApi: MessagesApi,
  implicit val sessionRepository: SessionRepository,
  actions: Actions,
  val controllerComponents: MessagesControllerComponents,
  view: GuaranteeAddedTIRView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  def onPageLoad(lrn: LocalReferenceNumber): Action[AnyContent] = actions.requireData(lrn) {
    implicit request =>
      Ok(view(lrn))
  }

  def onSubmit(lrn: LocalReferenceNumber): Action[AnyContent] = actions.requireData(lrn).async {
    implicit request =>
      GuaranteeTypePage(Index(0))
        .writeToUserAnswers(TIRGuarantee)
        .writeToSession()
        .navigateTo(controllers.routes.TaskListController.onPageLoad(lrn))
  }
}
