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

package controllers.transport.equipment.index.seals

import controllers.actions._
import controllers.transport.equipment.index.routes
import controllers.{NavigatorOps, SettableOps, SettableOpsRunner}
import forms.YesNoFormProvider
import models.journeyDomain.transport.TransportDomain
import models.requests.SpecificDataRequestProvider1
import models.{Index, LocalReferenceNumber, Mode}
import pages.sections.transport.equipment.SealSection
import pages.transport.equipment.index.seals.IdentificationNumberPage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.transport.equipment.index.seals.RemoveSealYesNoView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class RemoveSealYesNoController @Inject() (
  override val messagesApi: MessagesApi,
  implicit val sessionRepository: SessionRepository,
  actions: Actions,
  getMandatoryPage: SpecificDataRequiredActionProvider,
  formProvider: YesNoFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: RemoveSealYesNoView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  private type Request = SpecificDataRequestProvider1[String]#SpecificDataRequest[_]

  private def form(implicit request: Request): Form[Boolean] =
    formProvider("transport.equipment.index.seals.removeSealYesNo", request.arg)

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode, equipmentIndex: Index, sealIndex: Index): Action[AnyContent] = actions
    .requireData(lrn)
    .andThen(getMandatoryPage.getFirst(IdentificationNumberPage(equipmentIndex, sealIndex))) {
      implicit request =>
        Ok(view(form, lrn, mode, equipmentIndex, sealIndex, request.arg))
    }

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode, equipmentIndex: Index, sealIndex: Index): Action[AnyContent] = actions
    .requireData(lrn)
    .andThen(getMandatoryPage.getFirst(IdentificationNumberPage(equipmentIndex, sealIndex)))
    .async {
      implicit request =>
        lazy val redirect = routes.AddAnotherSealController.onPageLoad(lrn, mode, equipmentIndex)
        form
          .bindFromRequest()
          .fold(
            formWithErrors => Future.successful(BadRequest(view(formWithErrors, lrn, mode, equipmentIndex, sealIndex, request.arg))),
            {
              case true =>
                SealSection(equipmentIndex, sealIndex)
                  .removeFromUserAnswers()
                  .updateTask[TransportDomain]()
                  .writeToSession()
                  .navigateTo(redirect)
              case false =>
                Future.successful(Redirect(redirect))
            }
          )
    }
}
