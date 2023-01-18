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

package controllers.transport.equipment

import controllers.actions._
import controllers.{NavigatorOps, SettableOps, SettableOpsRunner}
import forms.ContainerIdentificationFormProvider
import models.requests.DataRequest
import models.{Index, LocalReferenceNumber, Mode, RichOptionalJsArray}
import navigation.UserAnswersNavigator
import navigation.transport.TransportNavigatorProvider
import pages.sections.transport.EquipmentsSection
import pages.transport.equipment.IdentificationNumberPage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.transport.equipment.IdentificationNumberView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class IdentificationNumberController @Inject() (
  override val messagesApi: MessagesApi,
  implicit val sessionRepository: SessionRepository,
  navigatorProvider: TransportNavigatorProvider,
  formProvider: ContainerIdentificationFormProvider,
  actions: Actions,
  val controllerComponents: MessagesControllerComponents,
  view: IdentificationNumberView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  private def form(equipmentIndex: Index)(implicit request: DataRequest[_]): Form[String] =
    formProvider("transport.equipment.identificationNumber", otherContainerIdentificationNumbers(equipmentIndex))

  private def otherContainerIdentificationNumbers(equipmentIndex: Index)(implicit request: DataRequest[_]): Seq[String] = {
    val numberOfEquipments = request.userAnswers.get(EquipmentsSection).length
    (0 until numberOfEquipments)
      .map(Index(_))
      .filterNot(_ == equipmentIndex)
      .map(IdentificationNumberPage)
      .flatMap(request.userAnswers.get(_))
  }

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode, equipmentIndex: Index): Action[AnyContent] = actions.requireData(lrn) {
    implicit request =>
      val preparedForm = request.userAnswers.get(IdentificationNumberPage(equipmentIndex)) match {
        case None        => form(equipmentIndex)
        case Some(value) => form(equipmentIndex).fill(value)
      }
      Ok(view(preparedForm, lrn, mode, equipmentIndex))
  }

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode, equipmentIndex: Index): Action[AnyContent] = actions.requireData(lrn).async {
    implicit request =>
      form(equipmentIndex)
        .bindFromRequest()
        .fold(
          formWithErrors => Future.successful(BadRequest(view(formWithErrors, lrn, mode, equipmentIndex))),
          value => {
            implicit val navigator: UserAnswersNavigator = navigatorProvider(mode)
            IdentificationNumberPage(equipmentIndex).writeToUserAnswers(value).writeToSession().navigate()
          }
        )
  }
}
