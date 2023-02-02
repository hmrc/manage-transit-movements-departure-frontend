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

import controllers.actions._
import controllers.{NavigatorOps, SettableOps, SettableOpsRunner}
import forms.ContainerIdentificationNumberFormProvider
import models.journeyDomain.transport.TransportDomain
import models.requests.DataRequest
import models.{Index, LocalReferenceNumber, Mode, RichOptionalJsArray}
import navigation.UserAnswersNavigator
import navigation.transport.EquipmentNavigatorProvider
import pages.sections.transport.equipment.EquipmentsSection
import pages.transport.equipment.index.ContainerIdentificationNumberPage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.transport.equipment.index.ContainerIdentificationNumberView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ContainerIdentificationNumberController @Inject() (
  override val messagesApi: MessagesApi,
  implicit val sessionRepository: SessionRepository,
  navigatorProvider: EquipmentNavigatorProvider,
  formProvider: ContainerIdentificationNumberFormProvider,
  actions: Actions,
  val controllerComponents: MessagesControllerComponents,
  view: ContainerIdentificationNumberView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  private def form(equipmentIndex: Index)(implicit request: DataRequest[_]): Form[String] =
    formProvider("transport.equipment.index.containerIdentificationNumber", otherContainerIdentificationNumbers(equipmentIndex))

  private def otherContainerIdentificationNumbers(equipmentIndex: Index)(implicit request: DataRequest[_]): Seq[String] = {
    val numberOfEquipments = request.userAnswers.get(EquipmentsSection).length
    (0 until numberOfEquipments)
      .map(Index(_))
      .filterNot(_ == equipmentIndex)
      .map(ContainerIdentificationNumberPage)
      .flatMap(request.userAnswers.get(_))
  }

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode, equipmentIndex: Index): Action[AnyContent] = actions.requireData(lrn) {
    implicit request =>
      val preparedForm = request.userAnswers.get(ContainerIdentificationNumberPage(equipmentIndex)) match {
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
            implicit val navigator: UserAnswersNavigator = navigatorProvider(mode, equipmentIndex)
            ContainerIdentificationNumberPage(equipmentIndex).writeToUserAnswers(value).updateTask[TransportDomain]().writeToSession().navigate()
          }
        )
  }
}
